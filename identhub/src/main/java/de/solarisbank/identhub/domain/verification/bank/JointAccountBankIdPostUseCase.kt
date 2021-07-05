package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.data.utils.toIdentificationUiModel
import io.reactivex.Single

class JointAccountBankIdPostUseCase(private val verificationBankRepository: VerificationBankRepository)
    : SingleUseCase<String, IdentificationUiModel>() {

    override fun invoke(iBan: String): Single<NavigationalResult<IdentificationUiModel>> {
        return verificationBankRepository.postBankIdIdentification(IBan(iBan))
                .map { newBankIdIdentification ->
                    val oldBankDto = verificationBankRepository.getIdentification().blockingGet()
                    //todo refactor with DAO transaction
                    val combinedIdentification = IdentificationDto(
                            id = newBankIdIdentification.id,
                            url = oldBankDto.url,
                            status = oldBankDto.status,
                            method = oldBankDto.method,
                            nextStep = oldBankDto.nextStep,
                            documents = null
                    )
                    verificationBankRepository.deleteAll().blockingGet()
                    verificationBankRepository.save(combinedIdentification).blockingGet()
                    NavigationalResult(combinedIdentification.toIdentificationUiModel(), combinedIdentification.nextStep)
                }
    }

}