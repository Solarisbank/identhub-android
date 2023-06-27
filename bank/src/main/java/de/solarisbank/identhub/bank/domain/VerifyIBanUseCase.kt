package de.solarisbank.identhub.bank.domain

import de.solarisbank.identhub.bank.data.IbanVerificationModel
import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.data.utils.parseErrorResponseDto
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.Type
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.domain.usecase.transformResult
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

class VerifyIBanUseCase(
    private val verificationBankRepository: VerificationBankRepository,
    override val initialConfigStorage: InitialConfigStorage
) : SingleUseCase<String, IbanVerificationModel>(), NextStepSelector {

    private var ibanAttempts = 0

    override fun invoke(param: String): Single<NavigationalResult<IbanVerificationModel>> {
        ibanAttempts++
        var code: String? = null
        return verificationBankRepository.postVerify(Iban(
            param))
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
                                    param))
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
                return@map NavigationalResult(identificationDto.url, nextStep)
            }
            .transformResult()
            .map {
                val ibanVerificationDto : IbanVerificationModel
                if (it.succeeded) {
                    IdLogger.info("Verifying of IBAN is successful. NextStep: ${it.nextStep}")
                    ibanVerificationDto =  IbanVerificationModel.IbanVerificationSuccessful(it.data, it.nextStep)
                } else if (it is Result.Error){
                    val type = it.type
                    if (type is Type.BadRequest && (code == INVALID_IBAN)) {
                        IdLogger.warn("Invalid IBAN. Retries: $ibanAttempts")
                        ibanVerificationDto = if (
                            ibanAttempts < getInitializationDto().allowedRetries
                        ) {
                            IbanVerificationModel.InvalidBankIdError(true)
                        } else {
                            IbanVerificationModel.InvalidBankIdError(false)
                        }
                    } else if (type is Type.UnprocessableEntity) {
                        IdLogger.warn("Received 422 when verifying IBAN")
                        ibanVerificationDto = IbanVerificationModel.AlreadyIdentifiedSuccessfullyError
                    } else if(type is Type.PreconditionFailed && code == IDENTIFICATION_ATTEMPTS_EXCEEDED) {
                        IdLogger.warn("412 - Number of attempts exceeded")
                        ibanVerificationDto = IbanVerificationModel.ExceedMaximumAttemptsError
                    } else if(type is Type.PreconditionFailed) {
                        IdLogger.warn("Received 412")
                        ibanVerificationDto = IbanVerificationModel.InvalidBankIdError(false)
                    } else {
                        IdLogger.warn("Generic error when verifying IBAN")
                        ibanVerificationDto = IbanVerificationModel.GenericError
                    }
                } else {
                    IdLogger.warn("Unknown error when verifying IBAN")
                    ibanVerificationDto = IbanVerificationModel.GenericError
                }
                NavigationalResult(ibanVerificationDto)
            }
    }

    private fun getInitializationDto(): IdenthubInitialConfig {
        return initialConfigStorage.get()
    }

    companion object {
        private const val INVALID_IBAN = "invalid_iban"
        private const val IDENTIFICATION_ATTEMPTS_EXCEEDED = "identification_attempts_exceeded"
    }
}