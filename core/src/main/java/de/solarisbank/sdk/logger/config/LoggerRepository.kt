package de.solarisbank.sdk.logger.config

import de.solarisbank.sdk.logger.data.LoggerRetrofitDataSource
import de.solarisbank.sdk.logger.domain.model.LogContent
import io.reactivex.Single


interface LoggerRepository {
    fun postLog(log: LogContent): Single<Boolean>
}

class LoggerRepositoryImpl(
    private val loggerRetrofitDataSource: LoggerRetrofitDataSource
) : LoggerRepository {
    override fun postLog(log: LogContent): Single<Boolean> {
        return loggerRetrofitDataSource.postLog(log)
    }

}