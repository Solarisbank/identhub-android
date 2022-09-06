package de.solarisbank.identhub.qes.di.core

import de.solarisbank.identhub.qes.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.data.contract.*
import de.solarisbank.identhub.qes.contract.ContractViewModel
import de.solarisbank.identhub.qes.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.qes.domain.*
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
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
private val qesBridgeModule = module {
    single<IdentificationLocalDataSource> { IdentHubSessionViewModel.INSTANCE!!.getIdentificationLocalDataSourceProvider().get() }
    single<IdentityInitializationDataSource> { IdentHubSessionViewModel.INSTANCE!!.getInitializationInMemoryDataSourceProvider().get() }
    single<InitializationInfoRepository> {IdentHubSessionViewModel.INSTANCE!!.initializationInfoRepositoryProvider.get() }
    factory { get<Retrofit>().create(IdentificationApi::class.java) }
    single<IdentificationRetrofitDataSource> {IdentificationRetrofitDataSourceImpl(get()) }
    factory { get<Retrofit>().create(MobileNumberApi::class.java) }
    single { MobileNumberDataSource(get()) }
    single { IdentificationRepository(get(), get(), get()) }
    factory { GetMobileNumberUseCase(get()) }
    single<IdentityInitializationRepository> { IdentityInitializationRepositoryImpl(get()) }
    factory { IdentificationPollingStatusUseCase(get(), get()) }
    single<CustomizationRepository> { CustomizationRepositoryImpl(get(), get()) }
}

private val qesModule = module {
    factory { get<Retrofit>().create(ContractSignApi::class.java) }
    factory<ContractSignNetworkDataSource> { ContractSignRetrofitDataSource(get()) }
    single<ContractSignRepository> { ContractSignRepositoryImpl(get(), get())}
    factory { GetIdentificationUseCase(get(), get()) }
    viewModel { ContractViewModel() }
}

private val qesPreviewDocumentsModule = module {
    factory { GetDocumentsUseCase(get()) }
    factory { FileController(get()) }
    factory { FetchPdfUseCase(get(), get()) }
    viewModel { ContractSigningPreviewViewModel(get(), get(), get()) }
}

private val qesSignDocumentsModule = module {
    factory { AuthorizeContractSignUseCase(get()) }
    factory { ConfirmContractSignUseCase(get(), get()) }
    viewModel { ContractSigningViewModel(get(), get(), get()) }
}

internal object QesKoin: IdenthubKoinComponent {
    private val qesModulesList = listOf(qesBridgeModule, qesModule, qesPreviewDocumentsModule,
        qesSignDocumentsModule)

    fun loadModules() {
        getKoin().loadModules(qesModulesList)
    }

    fun unloadModules() {
        getKoin().unloadModules(qesModulesList)
    }
}