package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.session.data.verification.bank.model.IBan
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import timber.log.Timber

class BankIdPostUseCase(
    private val verificationBankRepository: VerificationBankRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<Pair<String, IdentificationDto?>, IdentificationDto>(), NextStepSelector {

    override fun invoke(pair: Pair<String, IdentificationDto?>): Single<NavigationalResult<IdentificationDto>> {
        return verificationBankRepository.postBankIdIdentification(IBan(pair.first))
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