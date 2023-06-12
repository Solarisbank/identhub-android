package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.data.initial.*
import org.koin.dsl.module
import retrofit2.Retrofit

val initialConfigModule = module {
    factory { get<Retrofit>().create(InitializationApi::class.java) }
    factory<InitializationDataSource> { InitialConfigRetrofitDataSource(get()) }
    factory<InitialConfigUseCase> { InitialConfigUseCaseImpl(get()) }
    factory<FirstStepUseCase> { FirstStepUseCaseImpl(get(), get())}
}