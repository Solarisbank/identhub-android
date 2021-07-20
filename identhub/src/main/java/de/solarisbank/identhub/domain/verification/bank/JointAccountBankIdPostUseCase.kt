package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.data.utils.toIdentificationUiModel
import io.reactivex.Single
import timber.log.Timber

class JointAccountBankIdPostUseCase(private val verificationBankRepository: VerificationBankRepository)
    : SingleUseCase<Pair<String, IdentificationUiModel>, IdentificationUiModel>() {

    override fun invoke(pair: Pair<String, IdentificationUiModel>): Single<NavigationalResult<IdentificationUiModel>> {
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
                            nextStep = oldBankIdentification.nextStep,
                            documents = null
                    )
                    Timber.d("combinedIdentification : $combinedIdentification")
                    verificationBankRepository.deleteAll().blockingGet()
                    verificationBankRepository.save(combinedIdentification).blockingGet()
                    NavigationalResult(combinedIdentification.toIdentificationUiModel(), oldBankIdentification.nextStep)
                }
    }

}