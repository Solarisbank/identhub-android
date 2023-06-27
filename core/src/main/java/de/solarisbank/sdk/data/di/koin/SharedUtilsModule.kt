package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSourceImpl
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.domain.usecase.MobileNumberUseCase
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.domain.usecase.MobileNumberUseCaseImpl
import de.solarisbank.sdk.module.abstraction.GeneralModuleLoader
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

internal val sharedUtilsModule = module {
    factory { get<Retrofit>().create(IdentificationApi::class.java) }
    single<IdentificationRemoteDataSource> { IdentificationRetrofitDataSource(get()) }
    factory { get<Retrofit>().create(MobileNumberApi::class.java) }
    factory<MobileNumberDataSource> { MobileNumberDataSourceImpl(get()) }
    single { IdentificationRepository(get(), get()) }
    factory<MobileNumberUseCase> { MobileNumberUseCaseImpl(get()) }
    factory { IdentificationPollingStatusUseCase(get(), get()) }
    single { MockroLoader() } bind GeneralModuleLoader::class
    single { IdenthubDispatchers() }
}