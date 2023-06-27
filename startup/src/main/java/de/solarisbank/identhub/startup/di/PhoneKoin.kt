package de.solarisbank.identhub.startup.di

import de.solarisbank.identhub.startup.data.VerificationPhoneApi
import de.solarisbank.identhub.startup.data.VerificationPhoneDataSourceRepository
import de.solarisbank.identhub.startup.data.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.startup.data.VerificationPhoneRetrofitDataSource
import de.solarisbank.identhub.startup.domain.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.startup.domain.ConfirmVerificationPhoneUseCase
import de.solarisbank.identhub.startup.domain.VerificationPhoneRepository
import de.solarisbank.identhub.startup.feature.PhoneVerificationUseCase
import de.solarisbank.identhub.startup.feature.PhoneVerificationUseCaseImpl
import de.solarisbank.identhub.startup.feature.PhoneVerificationViewModel
import de.solarisbank.identhub.startup.feature.PhoneViewModel
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private val phoneVerificationModule = module {
    factory { get<Retrofit>().create(VerificationPhoneApi::class.java) }
    factory<VerificationPhoneNetworkDataSource> { VerificationPhoneRetrofitDataSource(get()) }
    single<VerificationPhoneRepository> { VerificationPhoneDataSourceRepository(get()) }
    factory { AuthorizeVerificationPhoneUseCase(get()) }
    factory { ConfirmVerificationPhoneUseCase(get()) }
    factory<PhoneVerificationUseCase> { PhoneVerificationUseCaseImpl(get(), get()) }
    viewModel { PhoneVerificationViewModel(get(), get(), get()) }
    viewModel { PhoneViewModel() }
}

object PhoneKoin: IdenthubKoinComponent {
    private val moduleList = listOf(phoneVerificationModule)

    fun loadModules() {
        loadModules(moduleList)
    }

    fun unloadModules() {
        unloadModules(moduleList)
    }
}