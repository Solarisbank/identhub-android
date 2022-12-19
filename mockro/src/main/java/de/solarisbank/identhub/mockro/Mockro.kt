package de.solarisbank.identhub.mockro

import androidx.annotation.Keep
import de.solarisbank.identhub.bank.domain.api.VerificationBankNetworkDataSource
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.mockro.fakes.*
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.di.koin.MockroInterface
import de.solarisbank.sdk.data.di.koin.MockroPersona
import de.solarisbank.sdk.data.initial.InitializationDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationDataSource
import de.solarisbank.sdk.fourthline.data.ip.IpDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import org.koin.core.Koin
import org.koin.dsl.module

@Keep
class Mockro: MockroInterface {
    override fun loadModules(koin: Koin) {
        koin.loadModules(
            listOf(coreMockroModule, bankMockroModule, fourthlineMockroModule, qesMockroModule)
        )
    }

    override fun setPersona(persona: MockroPersona) {
        currentPersona = persona
    }

    companion object {
        var currentPersona: MockroPersona = MockroPersona.BankHappyPath
    }
}

private val coreMockroModule = module {
    factory<InitializationDataSource> { FakeInitializationDataSource() }
    factory<IdentificationRemoteDataSource> { FakeIdentificationRemoteDataSource() }
}

private val bankMockroModule = module {
    factory<VerificationBankNetworkDataSource> { FakeVerificationBankNetworkDataSource() }
}

private val fourthlineMockroModule = module {
    factory<FourthlineIdentificationDataSource> { FakeFourthlineIdentificationDataSource() }
    factory<PersonDataSource> { FakePersonDataSource() }
    factory<IpDataSource> { FakeIpDataSource() }
    factory<KycUploadDataSource> { FakeKycUploadDataSource() }
    factory<KycInfoUseCase> { FakeKycInfoUseCase() }
    factory<LocationDataSource> { FakeLocationDataSource() }
}

private val qesMockroModule = module {
    factory<ContractSignNetworkDataSource> { FakeContractSignNetworkDataSource() }
}