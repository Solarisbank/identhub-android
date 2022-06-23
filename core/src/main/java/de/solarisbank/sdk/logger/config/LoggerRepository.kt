package de.solarisbank.sdk.logger.config

import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
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


class LoggerRepositoryFactory(
    private val coreModule: CoreModule,
    private val loggerRetrofitDataSourceProvider: Provider<LoggerRetrofitDataSource>
) : Factory<LoggerRepository> {
    override fun get(): LoggerRepository {
        return coreModule.provideLoggerRepository(loggerRetrofitDataSourceProvider.get())
    }

}