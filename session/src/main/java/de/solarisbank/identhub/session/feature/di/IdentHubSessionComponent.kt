package de.solarisbank.identhub.session.feature.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.data.datasource.DynamicIdetityRetrofitDataSource
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
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.db.DatabaseModule
import de.solarisbank.sdk.data.di.db.DatabaseModuleProvideIdentificationDaoFactory
import de.solarisbank.sdk.data.di.db.DatabaseModuleProvideRoomFactory
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.room.IdentityRoomDatabase
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
    val networkModule: NetworkModule,
    val identHubSessionModule: IdentHubSessionModule,
    private val sessionModule: SessionModule,
    private val databaseModule: DatabaseModule,
    private val applicationContextProvider: ApplicationContextProvider
){

    private lateinit var identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
    private lateinit var identificationDaoProvider: Provider<IdentificationDao>
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
    private lateinit var identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>
    private lateinit var dynamicIdetityRetrofitDataSourceProvider: Provider<DynamicIdetityRetrofitDataSource>
    private lateinit var identHubSessionRepositoryProvider: Provider<IdentHubSessionRepository>
    private lateinit var identHubSessionUseCaseProvider: Provider<IdentHubSessionUseCase>
    private lateinit var sharedPreferencesProvider: Provider<SharedPreferences>
    private lateinit var identityInitializationSharedPrefsDataSourceProvider: Provider<IdentityInitializationSharedPrefsDataSource>
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

    init {
        initialize()
    }

    private fun initialize() {

        identityRoomDatabaseProvider = DoubleCheck.provider(DatabaseModuleProvideRoomFactory.create(databaseModule, applicationContextProvider))
        identificationDaoProvider = DoubleCheck.provider(
            DatabaseModuleProvideIdentificationDaoFactory.create(databaseModule, identityRoomDatabaseProvider))
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
        identityInitializationSharedPrefsDataSourceProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationSharedPrefsDataSource> {
            override fun get(): IdentityInitializationSharedPrefsDataSource {
                return IdentityInitializationSharedPrefsDataSource(sharedPreferencesProvider.get())
            }
        })
        identityInitializationRepositoryProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationRepository> {
            override fun get(): IdentityInitializationRepository {
                return IdentityInitializationRepositoryImpl(identityInitializationSharedPrefsDataSourceProvider.get())
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

        identificationRoomDataSourceProvider = DoubleCheck.provider(object :
            Factory<IdentificationRoomDataSource> {
            override fun get(): IdentificationRoomDataSource {
                return IdentificationRoomDataSource(identificationDaoProvider.get())
            }
        })

        identHubSessionRepositoryProvider = DoubleCheck.provider(object :
            Factory<IdentHubSessionRepository> {
            override fun get(): IdentHubSessionRepository {
                return IdentHubSessionRepository(dynamicIdetityRetrofitDataSourceProvider.get(), identificationRoomDataSourceProvider.get())
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
                DoubleCheck.provider(IdentHubViewModelProvider(identHubSessionModule, identHubSessionUseCaseProvider.get()))

        private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> =
                DoubleCheck.provider(
                    IdentHubeSessionViewModelFactory.create(
                        identHubSessionModule,
                        saveStateViewModelMapProvider,
                        mapOfClassOfAndProviderOfViewModelProvider
                    )
                )

        override fun inject(identHubSessionObserver: IdentHubSessionObserver) {
            IdentHubSessionObserverInjector.injectAssistedViewModelFactory(identHubSessionObserver, assistedViewModelFactoryProvider.get())
        }

    }

    internal class IdentHubViewModelProvider(
        private val identHubSessionModule: IdentHubSessionModule,
        private val identHubSessionUseCase: IdentHubSessionUseCase
    ) : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
        override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
            return (LinkedHashMap<Class<out ViewModel>, Provider<ViewModel>>() )
                    .also {
                        it.put(IdentHubSessionViewModel::class.java, IdentHubSessionViewModelFactory.create(identHubSessionModule, identHubSessionUseCase))
                    }
        }

        companion object {
            fun create(
                identHubSessionModule: IdentHubSessionModule,
                identHubSessionUseCase: IdentHubSessionUseCase
            ): IdentHubViewModelProvider {
                return IdentHubViewModelProvider(identHubSessionModule, identHubSessionUseCase)
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
                            networkModule = NetworkModule(),
                            databaseModule = DatabaseModule(),
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