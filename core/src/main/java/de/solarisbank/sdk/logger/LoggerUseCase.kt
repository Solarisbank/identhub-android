package de.solarisbank.sdk.logger

import de.solarisbank.sdk.logger.config.LoggerRepository
import de.solarisbank.sdk.logger.domain.model.LogContent
import io.reactivex.Single

class LoggerUseCase(
    private val loggerRepository: LoggerRepository
) {


    fun invoke(param: LogContent): Single<Boolean> {
        return loggerRepository.postLog(param)
    }
}