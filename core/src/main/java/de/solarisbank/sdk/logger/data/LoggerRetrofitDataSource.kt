package de.solarisbank.sdk.logger.data

import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.logger.domain.model.LogContent
import io.reactivex.Single

class LoggerRetrofitDataSource(private val loggerAPI: LoggerAPI) {


    fun postLog(log: LogContent): Single<Boolean> {
        return loggerAPI.postLog(log)
    }
}

class LoggerRetrofitDataSourceFactory(
    private val coreModule: CoreModule,
    private val logApiProvider: Provider<LoggerAPI>
) : Factory<LoggerRetrofitDataSource> {
    override fun get(): LoggerRetrofitDataSource {
        return coreModule.provideLoggerRetrofitDataSource(logApiProvider.get())
    }


}