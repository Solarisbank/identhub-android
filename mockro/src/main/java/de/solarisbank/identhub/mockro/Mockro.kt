package de.solarisbank.identhub.mockro

import androidx.annotation.Keep
import de.solarisbank.identhub.bank.domain.api.VerificationBankNetworkDataSource
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.mockro.fakes.*
import de.solarisbank.identhub.phone.data.VerificationPhoneNetworkDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberNetworkDataSource
import de.solarisbank.sdk.data.di.koin.MockroInterface
import de.solarisbank.sdk.data.di.koin.MockroPersona
import de.solarisbank.sdk.data.di.koin.MockroOptions
import de.solarisbank.sdk.data.initial.InitializationDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource
import de.solarisbank.sdk.fourthline.data.terms.TermsAndConditionsUseCase
import de.solarisbank.sdk.fourthline.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import org.koin.core.Koin
import org.koin.dsl.module

@Keep
class Mockro: MockroInterface {
    override fun loadModules(koin: Koin) {
        koin.loadModules(
            listOf(coreMockroModule, bankMockroModule, phoneMockroModule, fourthlineMockroModule, qesMockroModule)
        )
    }

    override fun setPersona(persona: MockroPersona, options: MockroOptions?) {
        currentPersona = persona
        flowOptions = options
    }

    companion object {
        var currentPersona: MockroPersona = MockroPersona.FourthlineSigningHappyPath
        var flowOptions: MockroOptions? = null
    }
}

private val coreMockroModule = module {
    factory<InitializationDataSource> { FakeInitializationDataSource() }
    factory<IdentificationRemoteDataSource> { FakeIdentificationRemoteDataSource() }
    factory<MobileNumberNetworkDataSource> { FakeMobileNumberNetworkSource() }
}

private val bankMockroModule = module {
    factory<VerificationBankNetworkDataSource> { FakeVerificationBankNetworkDataSource() }
}

private val phoneMockroModule = module {
    factory<VerificationPhoneNetworkDataSource> { FakeVerificationPhoneNetworkDataSource() }
}

private val fourthlineMockroModule = module {
    factory<FourthlineIdentificationDataSource> { FakeFourthlineIdentificationDataSource() }
    factory<PersonDataSource> { FakePersonDataSource() }
    factory<IpObtainingUseCase> {  FakeIpObtainingUseCase() }
    factory<KycUploadDataSource> { FakeKycUploadDataSource() }
    factory<KycInfoUseCase> { FakeKycInfoUseCase() }
    factory<LocationDataSource> { FakeLocationDataSource() }
    factory<TermsAndConditionsUseCase> { FakeTermsUseCase() }
}

private val qesMockroModule = module {
    factory<ContractSignNetworkDataSource> { FakeContractSignNetworkDataSource() }
}