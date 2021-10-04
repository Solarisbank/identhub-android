package de.solarisbank.identhub.session.feature.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.data.datasource.DynamicIdetityRetrofitDataSource
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationSharedPrefsDataSource
import de.solarisbank.identhub.session.data.di.NetworkModuleProvideUserAgentInterceptorFactory
import de.solarisbank.identhub.session.data.di.ProvideSessionUrlRepositoryFactory
import de.solarisbank.identhub.session.data.di.SessionModule
import de.solarisbank.identhub.session.data.di.SessionUrlLocalDataSourceFactory
import de.solarisbank.identhub.session.data.network.InitializeIdentificationApi
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.repository.IdentHubSessionRepository
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.identhub.session.feature.IdentHubSessionObserver
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.datasource.IdentificationInMemoryDataSourceFactory
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoApiFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSourceFactory
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.customization.CustomizationRepositoryFactory
import de.solarisbank.sdk.feature.customization.CustomizationSharedPrefsCacheFactory
import de.solarisbank.sdk.feature.customization.CustomizationSharedPrefsStore
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//todo split to a separate gradle module
class IdentHubSessionComponent private constructor(
    val coreModule: CoreModule,
    val networkModule: NetworkModule,
    val identHubSessionModule: IdentHubSessionModule,
    private val sessionModule: SessionModule,
    private val applicationContextProvider: ApplicationContextProvider
){

    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>

    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<de.solarisbank.sdk.data.repository.SessionUrlRepository>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var identificationApiProvider: Provider<InitializeIdentificationApi>
    private lateinit var identificationInMemoryDataSourceProvider: Provider<IdentificationInMemoryDataSource>
    private lateinit var dynamicIdetityRetrofitDataSourceProvider: Provider<DynamicIdetityRetrofitDataSource>
    private lateinit var identHubSessionRepositoryProvider: Provider<IdentHubSessionRepository>
    private lateinit var identHubSessionUseCaseProvider: Provider<IdentHubSessionUseCase>
    private lateinit var sharedPreferencesProvider: Provider<SharedPreferences>
    private lateinit var identityInitializationDataSourceProvider: Provider<IdentityInitializationDataSource>
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoApiProvider: Provider<InitializationInfoApi>
    private lateinit var initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>
    private lateinit var customizationSharedPrefsStoreProvider: Provider<CustomizationSharedPrefsStore>

    init {
        initialize()
    }

    fun getIdentificationLocalDataSourceProvider(): Provider<IdentificationInMemoryDataSource> {
        return identificationInMemoryDataSourceProvider
    }

    private fun initialize() {

        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))
        userAgentInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideUserAgentInterceptorFactory.create())
        httpLoggingInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule))
        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(SessionUrlLocalDataSourceFactory.create(sessionModule))
        sharedPreferencesProvider = DoubleCheck.provider(object :
            Factory<SharedPreferences> {
            override fun get(): SharedPreferences {
                return applicationContextProvider.get().getSharedPreferences("identhub", Context.MODE_PRIVATE)
            }
        })
        identityInitializationDataSourceProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationDataSource> {
            override fun get(): IdentityInitializationSharedPrefsDataSource {
                return IdentityInitializationSharedPrefsDataSource(sharedPreferencesProvider.get())
            }
        })
        identityInitializationRepositoryProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationRepository> {
            override fun get(): IdentityInitializationRepository {
                return IdentityInitializationRepositoryImpl(identityInitializationDataSourceProvider.get())
            }
        })
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider))
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideDynamicUrlInterceptorFactory.create(networkModule, sessionUrlRepositoryProvider))
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(
                networkModule, userAgentInterceptorProvider, httpLoggingInterceptorProvider
        ))
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(
                networkModule,
                moshiConverterFactoryProvider,
                okHttpClientProvider,
                rxJavaCallAdapterFactoryProvider))

        identificationApiProvider = DoubleCheck.provider(object :
            Factory<InitializeIdentificationApi> {
            override fun get(): InitializeIdentificationApi {
                return retrofitProvider.get().create(InitializeIdentificationApi::class.java)
            }
        })

        dynamicIdetityRetrofitDataSourceProvider = DoubleCheck.provider(object :
            Factory<DynamicIdetityRetrofitDataSource> {
            override fun get(): DynamicIdetityRetrofitDataSource {
                return DynamicIdetityRetrofitDataSource(identificationApiProvider.get())
            }
        })

        identificationInMemoryDataSourceProvider = DoubleCheck.provider(
            IdentificationInMemoryDataSourceFactory.create()
        )

        identHubSessionRepositoryProvider = DoubleCheck.provider(object :
            Factory<IdentHubSessionRepository> {
            override fun get(): IdentHubSessionRepository {
                return IdentHubSessionRepository(
                    dynamicIdetityRetrofitDataSourceProvider.get(),
                    identificationInMemoryDataSourceProvider.get()
                )
            }
        })

        identHubSessionUseCaseProvider = DoubleCheck.provider(object :
            Factory<IdentHubSessionUseCase> {
            override fun get(): IdentHubSessionUseCase {
                return IdentHubSessionUseCase(
                        identHubSessionRepositoryProvider.get(),
                        sessionUrlRepositoryProvider.get(),
                        identityInitializationRepositoryProvider.get()
                        )
            }
        })

        initializationInfoApiProvider = DoubleCheck.provider(InitializationInfoApiFactory(coreModule, retrofitProvider))
        initializationInfoRetrofitDataSourceProvider = DoubleCheck.provider(
            InitializationInfoRetrofitDataSourceFactory(coreModule, initializationInfoApiProvider)
        )
        customizationSharedPrefsStoreProvider = DoubleCheck.provider(
            CustomizationSharedPrefsCacheFactory(coreModule, sharedPreferencesProvider)
        )
        customizationRepositoryProvider = DoubleCheck.provider(
            CustomizationRepositoryFactory(
                coreModule,
                applicationContextProvider,
                initializationInfoRetrofitDataSourceProvider,
                customizationSharedPrefsStoreProvider,
                sessionUrlRepositoryProvider
            )
        )
    }

    internal class ApplicationContextProvider(val applicationContext: Context) :
        Provider<Context> {
        override fun get(): Context {
            return applicationContext
        }
    }

    fun identHubSessionObserverSubComponent(): IdentHubObserverSubcomponent.Factory {
        return IdentHubSessionObserverSubComponentFactory()
    }

    inner class IdentHubSessionObserverSubComponentFactory : IdentHubObserverSubcomponent.Factory {
        override fun create(): IdentHubObserverSubcomponent {
            return IdentHubSessionObserverSubComponent(identHubSessionModule)
        }
    }

    inner class IdentHubSessionObserverSubComponent(
            val identHubSessionModule: IdentHubSessionModule
    ) : IdentHubObserverSubcomponent {

        private var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> =
                DoubleCheck.provider(object :
                    Factory<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {
                    override fun get(): Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>> {
                        return emptyMap()
                    }

                })

        private var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> =
                DoubleCheck.provider(IdentHubViewModelProvider(identHubSessionModule, identHubSessionUseCaseProvider.get(), customizationRepositoryProvider))

        private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> =
                DoubleCheck.provider(
                    IdentHubeSessionViewModelFactory.create(
                        identHubSessionModule,
                        saveStateViewModelMapProvider,
                        mapOfClassOfAndProviderOfViewModelProvider
                    )
                )

        override fun inject(identHubSessionObserver: IdentHubSessionObserver) {
            IdentHubSessionObserverInjector.injectAssistedViewModelFactory(identHubSessionObserver,
                assistedViewModelFactoryProvider.get())
        }

    }

    internal class IdentHubViewModelProvider(
        private val identHubSessionModule: IdentHubSessionModule,
        private val identHubSessionUseCase: IdentHubSessionUseCase,
        private val customizationRepositoryProvider: Provider<CustomizationRepository>,
    ) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
        override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
            return (LinkedHashMap<Class<out ViewModel>, Provider<ViewModel>>() )
                    .also {
                        it[IdentHubSessionViewModel::class.java] = IdentHubSessionViewModelFactory(identHubSessionModule, identHubSessionUseCase, customizationRepositoryProvider)
                    }
        }
    }

    companion object {
        private val lock = Any()
        private var identHubSessionComponent: IdentHubSessionComponent? = null

        fun getInstance(applicationContext: Context): IdentHubSessionComponent {
            synchronized(lock) {
                if(identHubSessionComponent == null) {
                    identHubSessionComponent = IdentHubSessionComponent(
                            coreModule = CoreModule(),
                            networkModule = NetworkModule(),
                            sessionModule = SessionModule(),
                            identHubSessionModule = IdentHubSessionModule(),
                            applicationContextProvider = ApplicationContextProvider(applicationContext)
                    )
                }
            }
            return identHubSessionComponent!!
        }
    }

}