package de.solarisbank.identhub.phone.di

import de.solarisbank.identhub.phone.data.VerificationPhoneApi
import de.solarisbank.identhub.phone.data.VerificationPhoneDataSourceRepository
import de.solarisbank.identhub.phone.data.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.phone.data.VerificationPhoneRetrofitDataSource
import de.solarisbank.identhub.phone.domain.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.phone.domain.ConfirmVerificationPhoneUseCase
import de.solarisbank.identhub.phone.domain.GetMobileNumberUseCase
import de.solarisbank.identhub.phone.domain.VerificationPhoneRepository
import de.solarisbank.identhub.phone.feature.PhoneVerificationUseCase
import de.solarisbank.identhub.phone.feature.PhoneVerificationUseCaseImpl
import de.solarisbank.identhub.phone.feature.PhoneVerificationViewModel
import de.solarisbank.identhub.phone.feature.PhoneViewModel
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.CustomizationRepositoryImpl
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSourceImpl
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

// Hacky module to bridge old dependency code
private val phoneBridgeModule = module {
    single<IdentificationLocalDataSource> { IdentHubSessionViewModel.INSTANCE!!.getIdentificationLocalDataSourceProvider().get() }
    single<IdentityInitializationDataSource> { IdentHubSessionViewModel.INSTANCE!!.getInitializationInMemoryDataSourceProvider().get() }
    single<InitializationInfoRepository> { IdentHubSessionViewModel.INSTANCE!!.initializationInfoRepositoryProvider.get() }
    factory { get<Retrofit>().create(IdentificationApi::class.java) }
    single<IdentificationRetrofitDataSource> { IdentificationRetrofitDataSourceImpl(get()) }
    factory { get<Retrofit>().create(MobileNumberApi::class.java) }
    single { MobileNumberDataSource(get()) }
    single { IdentificationRepository(get(), get(), get()) }
    factory { GetMobileNumberUseCase(get()) }
    single<IdentityInitializationRepository> { IdentityInitializationRepositoryImpl(get()) }
    factory { IdentificationPollingStatusUseCase(get(), get()) }
    single<CustomizationRepository> { CustomizationRepositoryImpl(get(), get()) }
}

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
    private val moduleList = listOf(phoneBridgeModule, phoneVerificationModule)

    fun loadModules() {
        getKoin().loadModules(moduleList)
    }

    fun unloadModules() {
        getKoin().unloadModules(moduleList)
    }
}