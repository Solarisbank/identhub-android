package de.solarisbank.sdk.fourthline.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor
import de.solarisbank.identhub.data.room.IdentityRoomDatabase
import de.solarisbank.identhub.data.session.SessionModule
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.data.session.factory.ProvideSessionUrlRepositoryFactory
import de.solarisbank.identhub.data.session.factory.SessionUrlLocalDataSourceFactory.Companion.create
import de.solarisbank.identhub.di.database.DatabaseModule
import de.solarisbank.identhub.di.database.DatabaseModuleProvideIdentificationDaoFactory
import de.solarisbank.identhub.di.database.DatabaseModuleProvideRoomFactory
import de.solarisbank.identhub.di.network.*
import de.solarisbank.identhub.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory.Companion.create
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.core.di.CoreActivityComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.core.di.internal.DoubleCheck
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.data.identification.*
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationApiFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRepositoryFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRetrofitDataSourceFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRoomDataSourceFactory
import de.solarisbank.sdk.fourthline.data.network.IdentificationIdInterceptor
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivityInjector
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragment
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.loaction.LocationAccessFragment
import de.solarisbank.sdk.fourthline.feature.ui.loaction.LocationAccessFragmentInjector
import de.solarisbank.sdk.fourthline.feature.ui.scan.*
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsInjector
import de.solarisbank.sdk.fourthline.feature.ui.terms.welcome.WelcomeContainerFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.welcome.WelcomeContainerFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragment
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector.Companion.injectAssistedViewModelFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FourthlineComponent private constructor(
        val fourthlineModule: FourthlineModule,
        val activitySubModule: FourthlineActivitySubModule,
        val sessionModule: SessionModule,
        val fourthlineIdentificationModule: FourthlineIdentificationModule,
        val networkModule: NetworkModule,
        private val databaseModule: DatabaseModule,
        private val libraryComponent: LibraryComponent
) {

    private lateinit var applicationContextProvider: Provider<Context>
    private lateinit var identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
    private lateinit var identificationDaoProvider: Provider<IdentificationDao>

    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>

    private lateinit var fourthlineIdentificationApiProvider: Provider<FourthlineIdentificationApi>
    private lateinit var fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>
    private lateinit var identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>
    private lateinit var fourthlineIdentificationRepositoryProvider: Provider<FourthlineIdentificationRepository>
    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
    private lateinit var personDataUseCaseProvider: Provider<PersonDataUseCase>
    private lateinit var kycInfoUseCaseProvider: Provider<KycInfoUseCase>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<out Interceptor>
    private lateinit var identificationIdInterceptorProvider: Provider<IdentificationIdInterceptor>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>

    init {
        initialize()
    }

    private fun initialize() {
        applicationContextProvider = ApplicationContextProvider(libraryComponent)

        identityRoomDatabaseProvider = DoubleCheck.provider(DatabaseModuleProvideRoomFactory.create(databaseModule, applicationContextProvider))
        identificationDaoProvider = DoubleCheck.provider(DatabaseModuleProvideIdentificationDaoFactory.create(databaseModule, identityRoomDatabaseProvider))

        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))

        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(create(sessionModule))
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider))

        //todo replace with factory
        kycInfoUseCaseProvider = DoubleCheck.provider(object : Factory<KycInfoUseCase> {
            override fun get(): KycInfoUseCase {
                return KycInfoUseCase()
            }
        })

        identificationRoomDataSourceProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRoomDataSourceFactory.create(fourthlineIdentificationModule, identificationDaoProvider))
        identificationIdInterceptorProvider = DoubleCheck.provider(object : Factory<IdentificationIdInterceptor> {
            override fun get(): IdentificationIdInterceptor {
                return IdentificationIdInterceptor(identificationRoomDataSourceProvider.get())
            }
        })

        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(create(networkModule, sessionUrlRepositoryProvider))
        userAgentInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideUserAgentInterceptorFactory.create(networkModule))
        httpLoggingInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule))
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(
                networkModule, dynamicBaseUrlInterceptorProvider, identificationIdInterceptorProvider, userAgentInterceptorProvider, httpLoggingInterceptorProvider
        ))
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(networkModule, moshiConverterFactoryProvider, okHttpClientProvider, rxJavaCallAdapterFactoryProvider))
        fourthlineIdentificationApiProvider = DoubleCheck.provider(ProvideFourthlineIdentificationApiFactory.create(fourthlineIdentificationModule, retrofitProvider))
        fourthlineIdentificationRetrofitDataSourceProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRetrofitDataSourceFactory.create(fourthlineIdentificationModule, fourthlineIdentificationApiProvider))

        fourthlineIdentificationRepositoryProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRepositoryFactory.create(
                fourthlineIdentificationModule,
                fourthlineIdentificationRetrofitDataSourceProvider,
                identificationRoomDataSourceProvider
        ))



        personDataUseCaseProvider = DoubleCheck.provider(object : Factory<PersonDataUseCase> {
            override fun get(): PersonDataUseCase {
                return PersonDataUseCase(
                        fourthlineIdentificationRepositoryProvider.get(),
                        sessionUrlLocalDataSourceProvider.get()
                )
            }
        })
    }

    internal class ContextProvider(private val activityComponent: CoreActivityComponent) : Provider<Context> {
        override fun get(): Context {
            return activityComponent.context()
        }
    }

    internal class ApplicationContextProvider(private val libraryComponent: LibraryComponent) : Provider<Context> {
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
                        kycInfoUseCaseProvider
                )
        private var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> =
                DoubleCheck.provider(EmptyMapOfClassOfAndProviderOfViewModelProvider())
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

            var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> = DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(
                    fourthlineModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider
            ))

            override fun inject(termsAndConditionsFragment: TermsAndConditionsFragment) {
                TermsAndConditionsInjector.injectAssistedViewModelFactory(termsAndConditionsFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(welcomeContainerFragment: WelcomeContainerFragment) {
                injectAssistedViewModelFactory(
                        welcomeContainerFragment, assistedViewModelFactoryProvider.get()
                )
            }

            override fun inject(welcomePageFragment: WelcomePageFragment) {
                injectAssistedViewModelFactory(
                        welcomePageFragment, assistedViewModelFactoryProvider.get()
                )
            }

            override fun inject(selfieFragment: SelfieFragment) {
                injectAssistedViewModelFactory(selfieFragment, assistedViewModelFactoryProvider.get())
            }
            override fun inject(selfieResultFragment: SelfieResultFragment) {
                injectAssistedViewModelFactory(selfieResultFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(docTypeSelectionFragment: DocTypeSelectionFragment) {
                DocTypeSelectionFragmentInjector.injectAssistedViewModelFactory(docTypeSelectionFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(docScanFragment: DocScanFragment) {
                DocScanFragmentInjector.injectAssistedViewModelFactory(docScanFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(docScanResultFragment: DocScanResultFragment) {
                DocScanResultFragmentInjector.injectAssistedViewModelFactory(docScanResultFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(locationAccessFragment: LocationAccessFragment) {
                LocationAccessFragmentInjector.injectAssistedViewModelFactory(locationAccessFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(kycUploadFragment: KycUploadFragment) {
                KycUploadFragmentInjector.injectAssistedViewModelFactory(kycUploadFragment, assistedViewModelFactoryProvider.get())
            }


        }
    }

    internal class SharedPreferencesProvider(private val activityComponent: CoreActivityComponent) : Provider<SharedPreferences> {
        override fun get(): SharedPreferences {
            return activityComponent.sharedPreferences()
        }
    }

    internal class EmptyMapOfClassOfAndProviderOfViewModelProvider() : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
        override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
            return emptyMap()
        }
    }

    companion object {
        private val lock = Any()
        private var fourthlineComponent: FourthlineComponent? = null

        fun getInstance(libraryComponent: LibraryComponent): FourthlineComponent {
            synchronized(lock) {
                if (fourthlineComponent == null) {
                    fourthlineComponent = FourthlineComponent(
                            fourthlineModule = FourthlineModule(),
                            sessionModule = SessionModule(),
                            fourthlineIdentificationModule = FourthlineIdentificationModule(),
                            activitySubModule = FourthlineActivitySubModule(),
                            networkModule = NetworkModule(),
                            libraryComponent = libraryComponent,
                            databaseModule = DatabaseModule()
                    )
                }

                return fourthlineComponent!!
            }
        }
    }
}