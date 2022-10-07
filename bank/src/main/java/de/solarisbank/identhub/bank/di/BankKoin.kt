package de.solarisbank.identhub.bank.di

import de.solarisbank.identhub.bank.domain.*
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.bank.domain.api.VerificationBankApi
import de.solarisbank.identhub.bank.domain.api.VerificationBankDataSourceRepository
import de.solarisbank.identhub.bank.domain.api.VerificationBankNetworkDataSource
import de.solarisbank.identhub.bank.domain.api.VerificationBankRetrofitDataSource
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.identhub.bank.feature.iban.VerificationBankIbanViewModel
import de.solarisbank.identhub.bank.feature.VerificationBankViewModel
import de.solarisbank.identhub.bank.feature.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.bank.feature.processing.ProcessingVerificationViewModel
import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.CustomizationRepositoryImpl
import de.solarisbank.sdk.data.datasource.*
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

// Hacky module to bridge old dependency code
private val bankBridgeModule = module {
    single<IdentificationLocalDataSource> { IdentHubSessionViewModel.INSTANCE!!.getIdentificationLocalDataSourceProvider().get() }
    single<IdentityInitializationDataSource> { IdentHubSessionViewModel.INSTANCE!!.getInitializationInMemoryDataSourceProvider().get() }
    single<InitializationInfoRepository> { IdentHubSessionViewModel.INSTANCE!!.initializationInfoRepositoryProvider.get() }
    single {IdentHubSessionViewModel.INSTANCE!!.sessionUrlRepository }
    factory { get<Retrofit>().create(IdentificationApi::class.java) }
    single<IdentificationRetrofitDataSource> { IdentificationRetrofitDataSourceImpl(get()) }
    factory { get<Retrofit>().create(MobileNumberApi::class.java) }
    single { MobileNumberDataSource(get()) }
    single { IdentificationRepository(get(), get(), get()) }
    single<IdentityInitializationRepository> { IdentityInitializationRepositoryImpl(get()) }
    factory { IdentificationPollingStatusUseCase(get(), get()) }
    single<CustomizationRepository> { CustomizationRepositoryImpl(get(), get()) }
}

private val bankModule = module {
    factory<VerificationBankApi> { get<Retrofit>().create(
        VerificationBankApi::class.java) }
    single<VerificationBankNetworkDataSource> {
        VerificationBankRetrofitDataSource(
            get())
    }
    single<VerificationBankRepository> { VerificationBankDataSourceRepository(get(), get()) }
    factory { BankIdPostUseCase(get(), get()) }
    factory { VerifyIBanUseCase(get(), get()) }
    factory { FetchingAuthorizedIBanStatusUseCase(get()) }
    factory { ProcessingVerificationUseCase(get(), get(), get()) }
    viewModel { VerificationBankViewModel() }
    viewModel { VerificationBankIbanViewModel(get(), get(), get()) }
    viewModel { params -> VerificationBankExternalGateViewModel(get(), get(), params.get()) }
    viewModel { ProcessingVerificationViewModel(get()) }
}

internal object BankKoin: IdenthubKoinComponent {
    private val bankModuleList = listOf(bankBridgeModule, bankModule)

    fun loadModules() {
        getKoin().loadModules(bankModuleList)
    }

    fun unloadModules() {
        getKoin().unloadModules(bankModuleList)
    }
}