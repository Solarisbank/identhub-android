package de.solarisbank.identhub.data.contract.step.parameters

import de.solarisbank.identhub.data.dto.QesStepParametersDto

class QesStepParametersRepository(
    private val qesStepParametersDataSource: QesStepParametersDataSource
) {

    fun saveQesStepParameters(
        qesStepParametersDto: QesStepParametersDto
    ) {
        qesStepParametersDataSource.qesStepParametersDto = qesStepParametersDto
    }

    fun getQesStepParameters(): QesStepParametersDto? {
        return qesStepParametersDataSource.qesStepParametersDto
    }

}