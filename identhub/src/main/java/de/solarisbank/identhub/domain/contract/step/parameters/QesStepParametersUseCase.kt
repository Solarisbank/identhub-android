package de.solarisbank.identhub.domain.contract.step.parameters

import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.data.dto.QesStepParametersDto

class QesStepParametersUseCase(
    private val qesStepParametersRepository: QesStepParametersRepository
) {

    fun saveParameters(fourthlineStepParametersDto: QesStepParametersDto) {
        qesStepParametersRepository.saveQesStepParameters(fourthlineStepParametersDto)
    }

    fun getParameters(): QesStepParametersDto? {
        return qesStepParametersRepository.getQesStepParameters()
    }

}