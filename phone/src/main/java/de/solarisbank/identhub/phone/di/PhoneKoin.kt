package de.solarisbank.identhub.phone.di

import de.solarisbank.identhub.phone.data.VerificationPhoneApi
import de.solarisbank.identhub.phone.data.VerificationPhoneDataSourceRepository
import de.solarisbank.identhub.phone.data.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.phone.data.VerificationPhoneRetrofitDataSource
import de.solarisbank.identhub.phone.domain.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.phone.domain.ConfirmVerificationPhoneUseCase
import de.solarisbank.identhub.phone.domain.VerificationPhoneRepository
import de.solarisbank.identhub.phone.feature.PhoneVerificationUseCase
import de.solarisbank.identhub.phone.feature.PhoneVerificationUseCaseImpl
import de.solarisbank.identhub.phone.feature.PhoneVerificationViewModel
import de.solarisbank.identhub.phone.feature.PhoneViewModel
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
    factory<PhoneVerificationUseCase> { PhoneVerificationUseCaseImpl(get(), get(), get()) }
    viewModel { PhoneVerificationViewModel(get()) }
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