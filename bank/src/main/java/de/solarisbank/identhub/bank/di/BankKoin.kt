package de.solarisbank.identhub.bank.di

import de.solarisbank.identhub.bank.domain.*
import de.solarisbank.identhub.bank.domain.api.VerificationBankApi
import de.solarisbank.identhub.bank.domain.api.VerificationBankDataSourceRepository
import de.solarisbank.identhub.bank.domain.api.VerificationBankNetworkDataSource
import de.solarisbank.identhub.bank.domain.api.VerificationBankRetrofitDataSource
import de.solarisbank.identhub.bank.feature.VerificationBankViewModel
import de.solarisbank.identhub.bank.feature.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.bank.feature.iban.VerificationBankIbanViewModel
import de.solarisbank.identhub.bank.feature.processing.ProcessingVerificationViewModel
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

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
    private val bankModuleList = listOf(bankModule)

    fun loadModules() {
        loadModules(bankModuleList)
    }

    fun unloadModules() {
        unloadModules(bankModuleList)
    }
}