package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.logger.config.LoggerRepository
import de.solarisbank.sdk.logger.config.LoggerRepositoryImpl
import de.solarisbank.sdk.logger.data.LoggerAPI
import de.solarisbank.sdk.logger.data.LoggerRetrofitDataSource
import org.koin.dsl.module
import retrofit2.Retrofit

internal val loggerModule = module {
    single { get<Retrofit>().create(LoggerAPI::class.java) }
    factory { LoggerRetrofitDataSource(get()) }
    single<LoggerRepository> { LoggerRepositoryImpl(get()) }
}