package de.solarisbank.sdk.feature.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.CustomizationRepositoryImpl
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepositoryImpl
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.logger.config.LoggerRepository
import de.solarisbank.sdk.logger.config.LoggerRepositoryImpl
import de.solarisbank.sdk.logger.data.LoggerAPI
import de.solarisbank.sdk.logger.data.LoggerApiFactory
import de.solarisbank.sdk.logger.data.LoggerRetrofitDataSource
import retrofit2.Retrofit

class CoreModule {
    fun provideAlertViewModel(customizationRepository: Provider<CustomizationRepository>): AlertViewModel {
        return AlertViewModel(customizationRepository.get())
    }

    fun provideCustomizationRepository(
        context: Context,
        initializationInfoRepository: InitializationInfoRepository
    ): CustomizationRepository {
        return CustomizationRepositoryImpl(
            context,
            initializationInfoRepository
        )
    }

    fun provideInitializationInfoApi(retrofit: Retrofit): InitializationInfoApi {
        return retrofit.create(InitializationInfoApi::class.java)
    }

    fun provideInitializationInfoRetrofitDataSource(api: InitializationInfoApi): InitializationInfoRetrofitDataSource {
        return InitializationInfoRetrofitDataSource(api)
    }

    fun provideInitializationInfoRepository(
        savedStateHandle: SavedStateHandle,
        initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
        sessionUrlRepository: SessionUrlRepository
    ): InitializationInfoRepository {
        return InitializationInfoRepositoryImpl(
            savedStateHandle,
            initializationInfoRetrofitDataSource,
            sessionUrlRepository
        )
    }


    fun provideInitializeLoggerApi(retrofit: Retrofit): LoggerAPI {
        return retrofit.create(LoggerAPI::class.java)
    }

    fun provideLoggerRetrofitDataSource(loggerAPI: LoggerAPI): LoggerRetrofitDataSource {
        return LoggerRetrofitDataSource(loggerAPI)
    }

    fun provideLoggerRepository(loggerRetorfitDataSource: LoggerRetrofitDataSource): LoggerRepository {
        return LoggerRepositoryImpl(loggerRetorfitDataSource)
    }
}