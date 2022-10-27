package de.solarisbank.identhub.qes.di.core

import de.solarisbank.identhub.data.contract.*
import de.solarisbank.identhub.qes.contract.ContractViewModel
import de.solarisbank.identhub.qes.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.qes.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.qes.domain.*
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

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
    factory { ConfirmContractSignUseCase(get(), get(), get()) }
    viewModel { ContractSigningViewModel(get(), get(), get()) }
}

internal object QesKoin: IdenthubKoinComponent {
    private val qesModulesList = listOf(qesModule, qesPreviewDocumentsModule,
        qesSignDocumentsModule)

    fun loadModules() {
        loadModules(qesModulesList)
    }

    fun unloadModules() {
        unloadModules(qesModulesList)
    }
}