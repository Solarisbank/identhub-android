package de.solarisbank.sdk.logger.data

import de.solarisbank.sdk.logger.domain.model.LogContent
import io.reactivex.Single

class LoggerRetrofitDataSource(private val loggerAPI: LoggerAPI) {


    fun postLog(log: LogContent): Single<Boolean> {
        return loggerAPI.postLog(log)
    }
}