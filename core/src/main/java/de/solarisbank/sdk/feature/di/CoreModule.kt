package de.solarisbank.sdk.feature.di

import android.content.Context
import android.content.SharedPreferences
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.customization.CustomizationRepositoryImpl
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.customization.CustomizationSharedPrefsStore
import de.solarisbank.sdk.feature.di.internal.Provider
import retrofit2.Retrofit

class CoreModule {
    fun provideAlertViewModel(customizationRepository: Provider<CustomizationRepository>): AlertViewModel {
        return AlertViewModel(customizationRepository.get())
    }

    fun provideCustomizationRepository(
        context: Context,
        initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
        customizationSharedPrefsStore: CustomizationSharedPrefsStore,
        sessionUrlRepository: SessionUrlRepository
    ): CustomizationRepository {
        return CustomizationRepositoryImpl(
            context,
            initializationInfoRetrofitDataSource,
            customizationSharedPrefsStore,
            sessionUrlRepository
        )
    }

    fun provideInitializationInfoApi(retrofit: Retrofit): InitializationInfoApi {
        return retrofit.create(InitializationInfoApi::class.java)
    }

    fun provideInitializationInfoRetrofitDataSource(api: InitializationInfoApi): InitializationInfoRetrofitDataSource {
        return InitializationInfoRetrofitDataSource(api)
    }

    fun provideCustomizationSharedPrefsStore(sharedPreferences: SharedPreferences): CustomizationSharedPrefsStore {
        return CustomizationSharedPrefsStore(sharedPreferences)
    }
}