package de.solarisbank.sdk.fourthline.di

import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.feature.storage.PersistentStorage
import de.solarisbank.sdk.feature.storage.SharedPrefsStorage
import de.solarisbank.sdk.fourthline.data.FourthlineStorage
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationApi
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.ip.IpApi
import de.solarisbank.sdk.fourthline.data.ip.IpDataSource
import de.solarisbank.sdk.fourthline.data.ip.IpDataSourceImpl
import de.solarisbank.sdk.fourthline.data.ip.IpRepository
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoInMemoryDataSource
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadApi
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.data.location.LocationRepository
import de.solarisbank.sdk.fourthline.data.location.LocationRepositoryImpl
import de.solarisbank.sdk.fourthline.data.person.PersonDataApi
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataSourceImpl
import de.solarisbank.sdk.fourthline.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCaseImpl
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadViewModel
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val FourthlineStorageName = "identhub_fourthline_storage"

private val fourthlineModule = module {
    factory { get<Retrofit>().create(FourthlineIdentificationApi::class.java) }
    factory<FourthlineIdentificationDataSource> { FourthlineIdentificationRetrofitDataSource(get()) }
    factory<PersonDataSource> { PersonDataSourceImpl(get()) }
    factory { get<Retrofit>().create(PersonDataApi::class.java) }
    single { FourthlineIdentificationRepository(get(), get(), get()) }
    factory { PersonDataUseCase(get(), get(), get()) }
    factory { DeleteKycInfoUseCase(get()) }
    single { KycInfoInMemoryDataSource() }
    single { KycInfoRepository(get()) }
    factory<KycInfoUseCase> { KycInfoUseCaseImpl(get(), get()) }
    single<LocationDataSource> { LocationDataSourceImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    factory { get<Retrofit>().create(IpApi::class.java) }
    factory<IpDataSource> { IpDataSourceImpl(get()) }
    single { IpRepository(get()) }
    factory { LocationUseCase(get()) }
    factory { IpObtainingUseCase(get()) }
    factory { get<Retrofit>().create(KycUploadApi::class.java) }
    factory<KycUploadDataSource> { KycUploadRetrofitDataSource(get()) }
    single { KycUploadRepository(get(), get()) }
    factory { KycUploadUseCase(get(), get(), get(), get()) }
    single { FourthlineStorage(get(named(FourthlineStorageName))) }
    single<PersistentStorage>(named(FourthlineStorageName)) {
        SharedPrefsStorage(get(), FourthlineStorageName)
    }
    viewModel {
        KycSharedViewModel(get(), get(), get(), get(), get(), get())
    }
    viewModel {
        FourthlineViewModel(get(), get())
    }
    viewModel {
        KycUploadViewModel(get())
    }
    viewModel {
        TermsAndConditionsViewModel(get())
    }
}

internal object FourthlineKoin: IdenthubKoinComponent {
    private val fourthlineModuleList = listOf(fourthlineModule)

    fun loadModules() {
        loadModules(fourthlineModuleList)
    }

    fun unloadModules() {
        unloadModules(fourthlineModuleList)
    }
}