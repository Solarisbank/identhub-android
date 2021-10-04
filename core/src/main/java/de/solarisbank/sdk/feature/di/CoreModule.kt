package de.solarisbank.sdk.feature.di

import android.content.Context
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepositoryImpl
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.customization.CustomizationRepositoryImpl
import de.solarisbank.sdk.feature.di.internal.Provider
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
        initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
        sessionUrlRepository: SessionUrlRepository
    ): InitializationInfoRepository {
        return InitializationInfoRepositoryImpl(
            initializationInfoRetrofitDataSource,
            sessionUrlRepository
        )
    }
}