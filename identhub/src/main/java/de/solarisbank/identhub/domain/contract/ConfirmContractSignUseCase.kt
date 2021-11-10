package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.domain.usecase.CompletableUseCase
import io.reactivex.Completable
import timber.log.Timber

class ConfirmContractSignUseCase(
    private val contractSignRepository: ContractSignRepository
    ) : CompletableUseCase<String> {

    override fun execute(confirmToken: String): Completable {
        return contractSignRepository.getIdentification()
            .flatMap { identificationDto: IdentificationDto ->
                Timber.d("execute, identificationDto: $identificationDto")
                contractSignRepository.confirmToken(
                    identificationDto.id,
                    TransactionAuthenticationNumber(confirmToken)
                )
            }
            .flatMapCompletable { identificationDto: IdentificationDto ->
                contractSignRepository.save(
                    identificationDto
                )
            }
    }
}