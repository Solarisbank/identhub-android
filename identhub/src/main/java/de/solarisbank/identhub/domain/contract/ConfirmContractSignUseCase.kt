package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.data.dto.ContractSigningState
import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.domain.model.PollingParametersDto
import de.solarisbank.sdk.domain.model.result.*
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import timber.log.Timber

class ConfirmContractSignUseCase(
    private val contractSignRepository: ContractSignRepository,
    private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
    private val qesStepParametersRepository: QesStepParametersRepository
    ) : SingleUseCase<String, ContractSigningState>() {

    override fun invoke(confirmToken: String): Single<NavigationalResult<ContractSigningState>> {
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
            }.andThen(
                identificationPollingStatusUseCase.execute(
                    PollingParametersDto(
                        qesStepParametersRepository
                            .getQesStepParameters()!!
                            .isFourthlineSigning
                    )
                )
            )
            .map { result ->
                return@map if (
                    result.succeeded &&
                    Status.getEnum(result.data?.status) == Status.SUCCESSFUL
                ) {
                    ContractSigningState.SUCCESSFUL(result.data!!.id)
                } else if (
                    result.succeeded &&
                    qesStepParametersRepository.getQesStepParameters() != null &&
                    qesStepParametersRepository.getQesStepParameters()!!.isFourthlineSigning &&
                    Status.getEnum(result.data?.status) == Status.CONFIRMED
                ) {
                    ContractSigningState.CONFIRMED(result.data!!.id)
                } else if (
                    Status.getEnum(result.data?.status) == Status.FAILED ||
                    (Status.getEnum(result.data?.status) == Status.CONFIRMED &&
                            (qesStepParametersRepository.getQesStepParameters() == null ||
                                    !qesStepParametersRepository.getQesStepParameters()!!.isFourthlineSigning))
                ) {
                    ContractSigningState.FAILED(result.data!!.id)
                } else if (
                    result.throwable?.message?.contains("422") == true
                    || result.throwable?.message?.contains("409") == true
                ) {
                    ContractSigningState.GENERIC_ERROR
                } else {
                    Timber.e("Unexpected condition")
                    ContractSigningState.GENERIC_ERROR
                }
            }
            .map { NavigationalResult(it) }
    }

}