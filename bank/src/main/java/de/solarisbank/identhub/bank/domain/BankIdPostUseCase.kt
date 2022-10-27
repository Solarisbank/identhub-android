package de.solarisbank.identhub.bank.domain

import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import timber.log.Timber

class BankIdPostUseCase(
    private val verificationBankRepository: VerificationBankRepository,
    override val initialConfigStorage: InitialConfigStorage
) : SingleUseCase<Pair<String, IdentificationDto?>, IdentificationDto>(), NextStepSelector {

    override fun invoke(pair: Pair<String, IdentificationDto?>): Single<NavigationalResult<IdentificationDto>> {
        return verificationBankRepository.postBankIdIdentification(Iban(
            pair.first))
                .map { newBankIdIdentification ->
                    Timber.d("newBankIdIdentification : $newBankIdIdentification")
                    val oldBankIdentification = pair.second
                    Timber.d("oldBankIdentification : $oldBankIdentification")
                    //todo refactor with DAO transaction
                    val combinedIdentification = IdentificationDto(
                            id = newBankIdIdentification.id,
                            url = newBankIdIdentification.url,
                            status = newBankIdIdentification.status,
                            method = newBankIdIdentification.method,
                            nextStep = oldBankIdentification?.nextStep,
                            fallbackStep = oldBankIdentification?.fallbackStep,
                            documents = null
                    )
                    Timber.d("combinedIdentification : $combinedIdentification")
                    verificationBankRepository.save(combinedIdentification).blockingGet()
                    val nextStep = if (oldBankIdentification != null) {
                        selectNextStep(
                            oldBankIdentification.nextStep,
                            oldBankIdentification.fallbackStep
                        )
                    } else {
                        selectNextStep(
                            newBankIdIdentification.nextStep,
                            newBankIdIdentification.fallbackStep
                        )
                    }
                    NavigationalResult(combinedIdentification, nextStep)
                }
    }

}