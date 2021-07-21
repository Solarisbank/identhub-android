package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.network.transformResult
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.data.dto.IbanVerificationDto
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.data.dto.InitializationDto
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.core.network.utils.parseErrorResponseDto
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.Type
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

class VerifyIBanUseCase(
    private val getIdentificationUseCase: GetIdentificationUseCase,
    private val verificationBankRepository: VerificationBankRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<String, IbanVerificationDto>(), NextStepSelector {

    private var ibanAttemts = 0

    override fun invoke(iBan: String): Single<NavigationalResult<IbanVerificationDto>> {
        ibanAttemts++
        var code: String? = null
        return verificationBankRepository.postVerify(IBan(iBan))
                .onErrorResumeNext { t ->
                    if (t is HttpException) {
                        code = t.parseErrorResponseDto()?.errors?.get(0)?.code.toString()
                        if (t.code() == HttpURLConnection.HTTP_PRECON_FAILED && code != IDENTIFICATION_ATTEMPTS_EXCEEDED) {
                            /**
                             * IBAN fail case -> register bank_id
                             */
                            Timber.d("onErrorResumeNext 1")
                            val bankIdIdentification = verificationBankRepository.postBankIdIdentification(IBan(iBan))
                            Timber.d("onErrorResumeNext 2")
                            return@onErrorResumeNext bankIdIdentification
                        } else {
                            Timber.d("onErrorResumeNext 3")
                            return@onErrorResumeNext Single.error(t)
                        }
                    } else {
                        Timber.d("onErrorResumeNext 4")
                        return@onErrorResumeNext Single.error(t)
                    }
                }
                .flatMapCompletable { identificationDto: IdentificationDto -> verificationBankRepository.save(identificationDto) }
                .andThen(
                        getIdentificationUseCase.execute(Unit)
                                .map {
                                    Timber.d("andThen")
                                    val data = it.data!!
                                    val nextStep = selectNextStep(data.nextStep, data.fallbackStep)
                                    return@map NavigationalResult(data.url, nextStep)
                                }
                )
                .transformResult()
                .map {

                    var ibanVerificationDto : IbanVerificationDto
                    if (it.succeeded) {
                        Timber.d("Iban verification result 1 : ${it.data}, ${it.nextStep}")
                        ibanVerificationDto =  IbanVerificationDto.IbanVerificationSuccessful(it.data, it.nextStep)
                    } else if (it is Result.Error){
                        val type = it.type
                        Timber.d("Iban verification result $it, code : $code")
                        val initializationDto = getInitializationDto()
                        if (type is Type.BadRequest && (code == INVALID_IBAN) && initializationDto!!.fallbackStep != null) {
                            ibanVerificationDto = if (
                                    initializationDto?.allowedRetries != null &&
                                    ibanAttemts < getInitializationDto()!!.allowedRetries
                            ) {
                                Timber.d("Iban verification result 2")
                                IbanVerificationDto.InvalidBankIdError(initializationDto!!.fallbackStep!!, true)
                            } else {
                                Timber.d("Iban verification result 3")
                                IbanVerificationDto.InvalidBankIdError(initializationDto!!.fallbackStep!!, false)
                            }
                        } else if (type is Type.UnprocessableEntity) {
                            Timber.d("Iban verification result 4")
                            ibanVerificationDto = IbanVerificationDto.AlreadyIdentifiedSuccessfullyError
                        } else if(type is Type.PreconditionFailed && code == IDENTIFICATION_ATTEMPTS_EXCEEDED) {
                            Timber.d("Iban verification result 5")
                            ibanVerificationDto = IbanVerificationDto.ExceedMaximumAttemptsError
                        } else if(type is Type.PreconditionFailed) {
                            Timber.d("Iban verification result 5.1")
                            ibanVerificationDto = IbanVerificationDto.InvalidBankIdError(initializationDto!!.fallbackStep!!, false)
                        } else {
                            Timber.d("Iban verification result 6")
                            ibanVerificationDto = IbanVerificationDto.GenericError
                        }
                    } else {
                        Timber.d("Iban verification result 7")
                        ibanVerificationDto = IbanVerificationDto.GenericError
                    }
                    NavigationalResult(ibanVerificationDto)
                }
    }

    private fun Result.Error.parseErrorCode() : String? {
        return if (this.throwable is HttpException) {
            Timber.d("parseErrorCode()")
            try { (this.throwable as HttpException)
                    .parseErrorResponseDto()?.errors?.get(0)
                    ?.code.toString()
            } catch (e: Exception) {
                Timber.w(e,"Error during errorDto parsing")
                null
            }
        } else {
            null
        }
    }

    private fun getInitializationDto(): InitializationDto? {
        return identityInitializationRepository.getInitializationDto()
    }

    companion object {
        private const val INVALID_IBAN = "invalid_iban"
        private const val IDENTIFICATION_ATTEMPTS_EXCEEDED = "identification_attempts_exceeded"

    }
}