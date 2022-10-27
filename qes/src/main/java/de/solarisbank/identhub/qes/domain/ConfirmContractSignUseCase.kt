package de.solarisbank.identhub.qes.domain

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.identhub.session.module.config.QesConfig
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.domain.model.PollingParametersDto
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class ConfirmContractSignUseCase(
    private val contractSignRepository: ContractSignRepository,
    private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
    private val qesConfig: QesConfig
) : SingleUseCase<String, ContractSigningResult>() {

    override fun invoke(confirmToken: String): Single<NavigationalResult<ContractSigningResult>> {
        return contractSignRepository.getIdentification()
            .flatMap { identificationDto: IdentificationDto ->
                contractSignRepository.confirmToken(
                    identificationDto.id,
                    confirmToken
                )
            }
            .flatMapCompletable { identificationDto: IdentificationDto ->
                contractSignRepository.save(
                    identificationDto
                )
            }.andThen(
                identificationPollingStatusUseCase.execute(PollingParametersDto(
                    qesConfig.isConfirmAcceptable
                ))
            )
            .map { result ->
                return@map if (
                    result.succeeded &&
                    Status.getEnum(result.data?.status) == Status.SUCCESSFUL
                ) {
                    ContractSigningResult.Successful(result.data!!.id)
                } else if (
                    result.succeeded &&
                    Status.getEnum(result.data?.status) == Status.CONFIRMED
                ) {
                    ContractSigningResult.Confirmed(result.data!!.id)
                } else if (
                    Status.getEnum(result.data?.status) == Status.FAILED
                ) {
                    ContractSigningResult.Failed(result.data!!.id)
                } else {
                    ContractSigningResult.GenericError
                }
            }
            .map { NavigationalResult(it) }
    }
}