package de.solarisbank.sdk.fourthline.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.ip.IpObtainingUseCase
import de.solarisbank.identhub.domain.ip.IpObtainingUseCaseFactory
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationSharedPrefsDataSource
import de.solarisbank.identhub.session.data.di.IdentityInitializationSharedPrefsDataSourceFactory
import de.solarisbank.identhub.session.data.di.NetworkModuleProvideUserAgentInterceptorFactory
import de.solarisbank.identhub.session.data.di.ProvideSessionUrlRepositoryFactory
import de.solarisbank.identhub.session.data.di.SessionModule
import de.solarisbank.identhub.session.data.di.SessionUrlLocalDataSourceFactory.Companion.create
import de.solarisbank.identhub.session.data.ip.*
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.person.PersonDataApi
import de.solarisbank.identhub.session.data.person.PersonDataApiFactory
import de.solarisbank.identhub.session.data.person.PersonDataDataSource
import de.solarisbank.identhub.session.data.person.PersonDataDataSourceFactory
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryFactory
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.datasource.*
import de.solarisbank.sdk.data.di.*
import de.solarisbank.sdk.data.di.datasource.IdentificationRetrofitDataSourceFactory
import de.solarisbank.sdk.data.di.datasource.MobileNumberDataSourceFactory
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory.Companion.create
import de.solarisbank.sdk.data.di.network.api.IdentificationApiFactory
import de.solarisbank.sdk.data.di.network.api.MobileNumberApiFactory
import de.solarisbank.sdk.data.repository.*
import de.solarisbank.sdk.domain.di.IdentificationPollingStatusUseCaseFactory
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoApiFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSourceFactory
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.customization.CustomizationRepositoryFactory
import de.solarisbank.sdk.feature.customization.CustomizationSharedPrefsCacheFactory
import de.solarisbank.sdk.feature.di.BaseFragmentDependencies
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.data.identification.*
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationApiFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRepositoryFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRetrofitDataSourceFactory
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoInMemoryDataSource
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoInMemoryDataSourceFactory
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepository
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepositoryFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadApi
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProvideKycUploadDataSourceFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProviderKycUploadApiFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProviderKycUploadRepositoryFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationRepository
import de.solarisbank.sdk.fourthline.data.location.LocationRepositoryFactory
import de.solarisbank.sdk.fourthline.data.network.IdentificationIdInterceptor
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCaseFactory
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCaseFactory
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCaseFactory
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCaseFactory
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivityInjector
import de.solarisbank.sdk.fourthline.feature.ui.kyc.result.UploadResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.kyc.result.UploadResultFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragment
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.passing.possibility.PassingPossibilityFragment
import de.solarisbank.sdk.fourthline.feature.ui.passing.possibility.PassingPossibilityFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.scan.*
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsInjector
import de.solarisbank.sdk.fourthline.feature.ui.terms.welcome.WelcomeContainerFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.welcome.WelcomeContainerFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragment
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FourthlineComponent private constructor(
    private val coreModule: CoreModule,
    val fourthlineModule: FourthlineModule,
    val activitySubModule: FourthlineActivitySubModule,
    val sessionModule: SessionModule,
    val fourthlineIdentificationModule: FourthlineIdentificationModule,
    val networkModule: NetworkModule,
    private val libraryComponent: LibraryComponent,
    private val kycUploadModule: KycUploadModule,
    private val identificationModule: IdentificationModule
) {

    private lateinit var applicationContextProvider: Provider<Context>
    private lateinit var deleteKycInfoUseCaseProvider: Provider<DeleteKycInfoUseCase>

    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>

    private lateinit var fourthlineIdentificationApiProvider: Provider<FourthlineIdentificationApi>
    private lateinit var fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>
    private lateinit var identificationLocalDataSourceProvider: Provider<out IdentificationLocalDataSource>
    private lateinit var personDataApiProvider: Provider<PersonDataApi>
    private lateinit var personDataDataSourceProvider: Provider<PersonDataDataSource>
    private lateinit var fourthlineIdentificationRepositoryProvider: Provider<FourthlineIdentificationRepository>
    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
    private lateinit var personDataUseCaseProvider: Provider<PersonDataUseCase>
    private lateinit var kycInfoInMemoryDataSourceProvider: Provider<KycInfoInMemoryDataSource>
    private lateinit var kycInfoRepositoryProvider: Provider<KycInfoRepository>
    private lateinit var kycInfoUseCaseProvider: Provider<KycInfoUseCase>
    private lateinit var locationDataSourceProvider: Provider<LocationDataSource>
    private lateinit var locationRepositoryProvider: Provider<LocationRepository>
    private lateinit var locationUseCaseProvider: Provider<LocationUseCase>
    private lateinit var ipApiProvider: Provider<IpApi>
    private lateinit var ipDataSourceProvider: Provider<IpDataSource>
    private lateinit var ipRepositoryProvider: Provider<IpRepository>
    private lateinit var ipObtainingUseCaseProvider: Provider<IpObtainingUseCase>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<out Interceptor>
    private lateinit var identificationIdInterceptorProvider: Provider<IdentificationIdInterceptor>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>

    private lateinit var identificationApiProvider: Provider<IdentificationApi>
    private lateinit var identificationRetrofitDataSourceProvider: Provider<IdentificationRetrofitDataSource>
    private lateinit var identificationRepositoryProvider: Provider<IdentificationRepository>
    private lateinit var mobileNumberApiProvider: Provider<MobileNumberApi>
    private lateinit var mobileNumberDataSourceProvider: Provider<MobileNumberDataSource>


    private lateinit var sharedPreferencesProvider: Provider<SharedPreferences>
    private lateinit var identitySharedPrefsDataSourceProvider: Provider<IdentityInitializationSharedPrefsDataSource>
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

    private lateinit var kycUploadApiProvider: Provider<KycUploadApi>
    private lateinit var kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>
    private lateinit var kycUploadRepositoryProvider: Provider<KycUploadRepository>
    private lateinit var kycUploadUseCaseProvider: Provider<KycUploadUseCase>
    private lateinit var identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>
    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoApiProvider: Provider<InitializationInfoApi>
    private lateinit var initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>

    init {
        initialize()
    }

    private fun initialize() {
        applicationContextProvider = ApplicationContextProvider(libraryComponent)
        identificationLocalDataSourceProvider = IdentHub.getIdentificationLocalDataSourceProvider()
        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))

        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(create(sessionModule))
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider))

        sharedPreferencesProvider = DoubleCheck.provider(object :
            Factory<SharedPreferences> {
            override fun get(): SharedPreferences {
                return applicationContextProvider.get().getSharedPreferences("identhub", Context.MODE_PRIVATE)
            }
        })
        identitySharedPrefsDataSourceProvider = DoubleCheck.provider(
            IdentityInitializationSharedPrefsDataSourceFactory.create(sharedPreferencesProvider))
        identityInitializationRepositoryProvider = DoubleCheck.provider(
            IdentityInitializationRepositoryFactory.create(identitySharedPrefsDataSourceProvider))
        deleteKycInfoUseCaseProvider = DeleteKycInfoUseCaseFactory.create(applicationContextProvider)
        kycInfoInMemoryDataSourceProvider = KycInfoInMemoryDataSourceFactory.create()
        kycInfoRepositoryProvider = KycInfoRepositoryFactory.create(kycInfoInMemoryDataSourceProvider)
        kycInfoUseCaseProvider = KycInfoUseCaseFactory.create(identityInitializationRepositoryProvider, kycInfoRepositoryProvider)
        locationDataSourceProvider = LocationDataSourceFactory.create(applicationContextProvider.get())
        locationRepositoryProvider = LocationRepositoryFactory.create(locationDataSourceProvider.get())
        locationUseCaseProvider = LocationUseCaseFactory.create(locationRepositoryProvider.get())

        identificationIdInterceptorProvider = DoubleCheck.provider(object :
            Factory<IdentificationIdInterceptor> {
            override fun get(): IdentificationIdInterceptor {
                return IdentificationIdInterceptor(identificationLocalDataSourceProvider.get())
            }
        })
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(create(networkModule, sessionUrlRepositoryProvider))
        userAgentInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideUserAgentInterceptorFactory.create())
        httpLoggingInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule))
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(
            networkModule,
            dynamicBaseUrlInterceptorProvider,
            identificationIdInterceptorProvider,
            userAgentInterceptorProvider,
            httpLoggingInterceptorProvider
        ))
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(
            networkModule,
            moshiConverterFactoryProvider,
            okHttpClientProvider,
            rxJavaCallAdapterFactoryProvider
        ))
        mobileNumberApiProvider = MobileNumberApiFactory.create(retrofitProvider.get())
        mobileNumberDataSourceProvider =
            MobileNumberDataSourceFactory.create(mobileNumberApiProvider.get())
        identificationApiProvider = DoubleCheck.provider(IdentificationApiFactory.create(identificationModule, retrofitProvider.get()))
        identificationRetrofitDataSourceProvider = DoubleCheck.provider(
            IdentificationRetrofitDataSourceFactory.create(identificationModule, identificationApiProvider.get()))
        identificationRepositoryProvider = DoubleCheck.provider(
            IdentificationRepositoryFactory.create(
            identificationLocalDataSourceProvider.get(), identificationRetrofitDataSourceProvider.get(), mobileNumberDataSourceProvider.get()
        ))
        fourthlineIdentificationApiProvider = DoubleCheck.provider(
            ProvideFourthlineIdentificationApiFactory.create(
                fourthlineIdentificationModule,
                retrofitProvider
            ))
        fourthlineIdentificationRetrofitDataSourceProvider = DoubleCheck.provider(
            ProvideFourthlineIdentificationRetrofitDataSourceFactory.create(
                fourthlineIdentificationModule,
                fourthlineIdentificationApiProvider
            ))
        kycUploadApiProvider = DoubleCheck.provider(ProviderKycUploadApiFactory.create(kycUploadModule, retrofitProvider))
        kycUploadRetrofitDataSourceProvider = DoubleCheck.provider(ProvideKycUploadDataSourceFactory.create(kycUploadModule, kycUploadApiProvider))

        personDataApiProvider = PersonDataApiFactory.create(retrofitProvider.get())
        personDataDataSourceProvider = PersonDataDataSourceFactory.create(personDataApiProvider.get())
        fourthlineIdentificationRepositoryProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRepositoryFactory.create(
                fourthlineIdentificationModule,
                fourthlineIdentificationRetrofitDataSourceProvider,
                identificationLocalDataSourceProvider,
                personDataDataSourceProvider
        ))

        identificationRepositoryProvider = DoubleCheck.provider(
            IdentificationRepositoryFactory.create(
            identificationLocalDataSourceProvider.get(), identificationRetrofitDataSourceProvider.get(), mobileNumberDataSourceProvider.get()
        ))
        identificationPollingStatusUseCaseProvider = DoubleCheck.provider(
            IdentificationPollingStatusUseCaseFactory.create(identificationRepositoryProvider.get(), identityInitializationRepositoryProvider.get()))


        kycUploadRepositoryProvider = DoubleCheck.provider(ProviderKycUploadRepositoryFactory.create(
            kycUploadModule,
            fourthlineIdentificationRetrofitDataSourceProvider,
            identificationLocalDataSourceProvider,
            kycUploadRetrofitDataSourceProvider,
            sessionUrlLocalDataSourceProvider
        ))
        kycUploadUseCaseProvider = KycUploadUseCaseFactory.create(
            kycUploadRepositoryProvider,
            deleteKycInfoUseCaseProvider,
            identificationPollingStatusUseCaseProvider,
            identityInitializationRepositoryProvider
            )


        ipApiProvider = DoubleCheck.provider(IpApiFactory.create(retrofitProvider.get()))
        ipDataSourceProvider = DoubleCheck.provider(IpDataSourceFactory.create(ipApiProvider.get()))
        ipRepositoryProvider = DoubleCheck.provider(IpRepositoryFactory.create(ipDataSourceProvider.get()))
        ipObtainingUseCaseProvider = DoubleCheck.provider(IpObtainingUseCaseFactory.create(ipRepositoryProvider.get()))
        personDataUseCaseProvider = DoubleCheck.provider(object :
            Factory<PersonDataUseCase> {
            override fun get(): PersonDataUseCase {
                return PersonDataUseCase(
                        fourthlineIdentificationRepositoryProvider.get(),
                        sessionUrlLocalDataSourceProvider.get()
                )
            }
        })

        initializationInfoApiProvider = DoubleCheck.provider(InitializationInfoApiFactory(coreModule, retrofitProvider))
        initializationInfoRetrofitDataSourceProvider = DoubleCheck.provider(InitializationInfoRetrofitDataSourceFactory(coreModule, initializationInfoApiProvider))
        val customizationSharedPrefsStoreProvider = DoubleCheck.provider(CustomizationSharedPrefsCacheFactory(coreModule, sharedPreferencesProvider))
        customizationRepositoryProvider = DoubleCheck.provider(CustomizationRepositoryFactory(
            coreModule,
            applicationContextProvider,
            initializationInfoRetrofitDataSourceProvider,
            customizationSharedPrefsStoreProvider,
            sessionUrlRepositoryProvider
        ))
    }

    internal class ContextProvider(private val activityComponent: CoreActivityComponent) :
        Provider<Context> {
        override fun get(): Context {
            return activityComponent.context()
        }
    }

    internal class ApplicationContextProvider(private val libraryComponent: LibraryComponent) :
        Provider<Context> {
        override fun get(): Context {
            return libraryComponent.applicationContext()
        }
    }

    fun activitySubcomponent(): FourthlineActivitySubcomponent.Factory {
        return FourthlineActivitySubcomponentFactory()
    }

    inner class FourthlineActivitySubcomponentFactory : FourthlineActivitySubcomponent.Factory {
        override fun create(activityComponent: CoreActivityComponent): FourthlineActivitySubcomponent {
            return FourthlineActivitySubcomponentImpl(activityComponent, fourthlineModule)
        }
    }

    inner class FourthlineActivitySubcomponentImpl(
        val coreActivityComponent: CoreActivityComponent,
        val fourthlineModule: FourthlineModule
    ) : FourthlineActivitySubcomponent {

        private val contextProvider: Provider<Context> = ContextProvider(coreActivityComponent)
        private var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> =
                FourthlineSaveStateViewModelMapProvider.create(
                    fourthlineModule,
                    personDataUseCaseProvider,
                    kycInfoUseCaseProvider,
                    locationUseCaseProvider,
                    ipObtainingUseCaseProvider,
                    kycUploadUseCaseProvider,
                    deleteKycInfoUseCaseProvider
                )
        private var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> =
                DoubleCheck.provider(FourthlineViewModelMapProvider(coreModule, customizationRepositoryProvider))
        private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> =
                DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(
                        fourthlineModule,
                        mapOfClassOfAndProviderOfViewModelProvider,
                        saveStateViewModelMapProvider
                ))


        override fun inject(fourthlineActivity: FourthlineActivity) {
            FourthlineActivityInjector.injectAssistedViewModelFactory(fourthlineActivity, assistedViewModelFactoryProvider.get())
        }

        override fun fragmentComponent(): FourthlineFragmentComponent.Factory {
            return FourthlineFragmentComponentFactory()
        }
        inner class FourthlineFragmentComponentFactory : FourthlineFragmentComponent.Factory {
            override fun create(): FourthlineFragmentComponent {
                return FragmentComponentImpl(this@FourthlineActivitySubcomponentImpl.fourthlineModule)
            }
        }

        private inner class FragmentComponentImpl(fourthlineModule: FourthlineModule) : FourthlineFragmentComponent{

            val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> = DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(
                    fourthlineModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider
            ))
            val baseFragmentDependencies = BaseFragmentDependencies(assistedViewModelFactoryProvider, customizationRepositoryProvider)

            override fun inject(termsAndConditionsFragment: TermsAndConditionsFragment) {
                TermsAndConditionsInjector(baseFragmentDependencies).injectMembers(termsAndConditionsFragment)
            }

            override fun inject(welcomeContainerFragment: WelcomeContainerFragment) {
                WelcomeContainerFragmentInjector(baseFragmentDependencies).injectMembers(welcomeContainerFragment)
            }

            override fun inject(welcomePageFragment: WelcomePageFragment) {
                WelcomePageFragmentInjector(baseFragmentDependencies).injectMembers(welcomePageFragment)
            }

            override fun inject(selfieFragment: SelfieFragment) {
                SelfieFragmentInjector(assistedViewModelFactoryProvider, customizationRepositoryProvider)
                    .injectMembers(selfieFragment)
            }

            override fun inject(selfieResultFragment: SelfieResultFragment) {
                SelfieResultFragmentInjector(baseFragmentDependencies).injectMembers(selfieResultFragment)
            }

            override fun inject(docTypeSelectionFragment: DocTypeSelectionFragment) {
                DocTypeSelectionFragmentInjector(baseFragmentDependencies).injectMembers(docTypeSelectionFragment)
            }

            override fun inject(docScanFragment: DocScanFragment) {
                DocScanFragmentInjector(assistedViewModelFactoryProvider, customizationRepositoryProvider)
                    .injectMembers(docScanFragment)
            }

            override fun inject(docScanResultFragment: DocScanResultFragment) {
                DocScanResultFragmentInjector(baseFragmentDependencies).injectMembers(docScanResultFragment)
            }

            override fun inject(kycUploadFragment: KycUploadFragment) {
                KycUploadFragmentInjector(baseFragmentDependencies).injectMembers(kycUploadFragment)
            }

            override fun inject(passingPossibilityFragment: PassingPossibilityFragment) {
                PassingPossibilityFragmentInjector(baseFragmentDependencies).injectMembers(passingPossibilityFragment)
            }

            override fun inject(uploadResultFragment: UploadResultFragment) {
                UploadResultFragmentInjector(baseFragmentDependencies).injectMembers(uploadResultFragment)
            }

        }
    }

    internal class SharedPreferencesProvider(private val activityComponent: CoreActivityComponent) :
        Provider<SharedPreferences> {
        override fun get(): SharedPreferences {
            return activityComponent.sharedPreferences()
        }
    }

    companion object {
        private val lock = Any()
        private var fourthlineComponent: FourthlineComponent? = null

        fun getInstance(libraryComponent: LibraryComponent): FourthlineComponent {
            synchronized(lock) {
                if (fourthlineComponent == null) {
                    fourthlineComponent = FourthlineComponent(
                        coreModule = CoreModule(),
                        fourthlineModule = FourthlineModule(),
                        sessionModule = SessionModule(),
                        fourthlineIdentificationModule = FourthlineIdentificationModule(),
                        activitySubModule = FourthlineActivitySubModule(),
                        networkModule = NetworkModule(),
                        libraryComponent = libraryComponent,
                        kycUploadModule = KycUploadModule(),
                        identificationModule = IdentificationModule()
                    )
                }

                return fourthlineComponent!!
            }
        }
    }
}