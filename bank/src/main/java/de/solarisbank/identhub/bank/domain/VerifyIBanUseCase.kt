package de.solarisbank.identhub.bank.domain

import de.solarisbank.identhub.bank.data.IbanVerificationModel
import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.utils.parseErrorResponseDto
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.Type
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.domain.usecase.transformResult
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

class VerifyIBanUseCase(
    private val verificationBankRepository: VerificationBankRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<String, IbanVerificationModel>(), NextStepSelector {

    private var ibanAttemts = 0

    override fun invoke(iBan: String): Single<NavigationalResult<IbanVerificationModel>> {
        ibanAttemts++
        var code: String? = null
        return verificationBankRepository.postVerify(Iban(
            iBan))
                .onErrorResumeNext { t ->
                    if (t is HttpException) {
                        code = t.parseErrorResponseDto()?.errors?.get(0)?.code.toString()
                        if (t.code() == HttpURLConnection.HTTP_PRECON_FAILED && code != IDENTIFICATION_ATTEMPTS_EXCEEDED) {
                            /**
                             * IBAN fail case -> register bank_id
                             */
                            Timber.d("onErrorResumeNext 1")
                            val bankIdIdentification = verificationBankRepository.postBankIdIdentification(
                                Iban(
                                    iBan))
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
            .map { identificationDto ->
                verificationBankRepository.save(identificationDto).blockingGet()
                val nextStep = selectNextStep(identificationDto.nextStep, identificationDto.fallbackStep)
                Timber.d("map, nextStep = $nextStep")
                return@map NavigationalResult(identificationDto.url, nextStep)
            }
            .transformResult()
            .map {
                var ibanVerificationDto : IbanVerificationModel
                if (it.succeeded) {
                    Timber.d("Iban verification result 1 : ${it.data}, ${it.nextStep}")
                    ibanVerificationDto =  IbanVerificationModel.IbanVerificationSuccessful(it.data, it.nextStep)
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
                            IbanVerificationModel.InvalidBankIdError(initializationDto!!.fallbackStep!!, true)
                        } else {
                            Timber.d("Iban verification result 3")
                            IbanVerificationModel.InvalidBankIdError(initializationDto!!.fallbackStep!!, false)
                        }
                    } else if (type is Type.UnprocessableEntity) {
                        Timber.d("Iban verification result 4")
                        ibanVerificationDto = IbanVerificationModel.AlreadyIdentifiedSuccessfullyError
                    } else if(type is Type.PreconditionFailed && code == IDENTIFICATION_ATTEMPTS_EXCEEDED) {
                        Timber.d("Iban verification result 5")
                        ibanVerificationDto = IbanVerificationModel.ExceedMaximumAttemptsError
                    } else if(type is Type.PreconditionFailed) {
                        Timber.d("Iban verification result 5.1")
                        ibanVerificationDto = IbanVerificationModel.InvalidBankIdError(initializationDto!!.fallbackStep!!, false)
                    } else {
                        Timber.d("Iban verification result 6")
                        ibanVerificationDto = IbanVerificationModel.GenericError
                    }
                } else {
                    Timber.d("Iban verification result 7")
                    ibanVerificationDto = IbanVerificationModel.GenericError
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