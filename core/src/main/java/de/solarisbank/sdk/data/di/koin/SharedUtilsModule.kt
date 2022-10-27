package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.domain.usecase.GetMobileNumberUseCase
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import org.koin.dsl.module
import retrofit2.Retrofit

val sharedUtilsModule = module {
    factory { get<Retrofit>().create(IdentificationApi::class.java) }
    single<IdentificationRemoteDataSource> { IdentificationRetrofitDataSource(get()) }
    factory { get<Retrofit>().create(MobileNumberApi::class.java) }
    single { MobileNumberDataSource(get()) }
    single { IdentificationRepository(get(), get(), get()) }
    factory { GetMobileNumberUseCase(get()) }
    factory { IdentificationPollingStatusUseCase(get(), get()) }
}