package de.solarisbank.sdk.fourthline.domain.step.parameters

import de.solarisbank.sdk.fourthline.data.dto.FourthlineStepParametersDto
import de.solarisbank.sdk.fourthline.data.step.parameters.FourthlineStepParametersRepository

class FourthlineStepParametersUseCase(
    val fourthlineStepParametersRepository: FourthlineStepParametersRepository
) {

    fun saveParameters(fourthlineStepParametersDto: FourthlineStepParametersDto) {
        fourthlineStepParametersRepository.saveFourthlineStepParameters(fourthlineStepParametersDto)
    }

    fun getParameters(): FourthlineStepParametersDto? {
        return fourthlineStepParametersRepository.getFourthlineStepParameters()
    }


}