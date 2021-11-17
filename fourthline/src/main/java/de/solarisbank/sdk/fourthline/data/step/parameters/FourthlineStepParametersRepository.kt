package de.solarisbank.sdk.fourthline.data.step.parameters

import de.solarisbank.sdk.fourthline.data.dto.FourthlineStepParametersDto

class FourthlineStepParametersRepository(
    private val fourthlineStepParametersDataSource: FourthlineStepParametersDataSource
) {

    fun saveFourthlineStepParameters(
        fourthlineStepParametersDto: FourthlineStepParametersDto
    ) {
        fourthlineStepParametersDataSource.fourthlineStepParametersDto = fourthlineStepParametersDto
    }

    fun getFourthlineStepParameters(): FourthlineStepParametersDto? {
        return fourthlineStepParametersDataSource.fourthlineStepParametersDto
    }

}