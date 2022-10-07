package de.solarisbank.identhub.session.feature.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.session.data.datasource.DynamicIdetityRetrofitDataSource
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationInMemoryDataSource
import de.solarisbank.identhub.session.data.datasource.SessionStateSavedStateHandleDataSource
import de.solarisbank.identhub.session.data.di.*
import de.solarisbank.identhub.session.data.network.InitializeIdentificationApi
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.repository.IdentHubSessionRepository
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.session.data.repository.SessionStateRepository
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.datasource.IdentificationInMemoryDataSourceFactory
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.config.InitializationInfoApiFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepositoryFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSourceFactory
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.logger.LoggerHttpInterceptor
import de.solarisbank.sdk.logger.LoggerUseCase
import de.solarisbank.sdk.logger.config.LoggerRepository
import de.solarisbank.sdk.logger.config.LoggerRepositoryFactory
import de.solarisbank.sdk.logger.data.LoggerAPI
import de.solarisbank.sdk.logger.data.LoggerApiFactory
import de.solarisbank.sdk.logger.data.LoggerRetrofitDataSource
import de.solarisbank.sdk.logger.data.LoggerRetrofitDataSourceFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//todo split to a separate gradle module
class IdentHubViewModelComponent private constructor(
    val coreModule: CoreModule,
    val networkModule: NetworkModule,
    private val savedStateHandle: SavedStateHandle,
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
    lateinit var sessionUrlRepositoryProvider: Provider<de.solarisbank.sdk.data.repository.SessionUrlRepository>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var loggingInterceptorProvider: Provider<LoggerHttpInterceptor>
    private lateinit var identificationApiProvider: Provider<InitializeIdentificationApi>
    private lateinit var identificationInMemoryDataSourceProvider: Provider<IdentificationLocalDataSource>
    private lateinit var dynamicIdetityRetrofitDataSourceProvider: Provider<DynamicIdetityRetrofitDataSource>
    private lateinit var identHubSessionRepositoryProvider: Provider<IdentHubSessionRepository>
    private lateinit var identHubSessionStateSavedStateHandleDataSourceProvider: Provider<SessionStateSavedStateHandleDataSource>
    private lateinit var identHubSessionStateRepositoryProvider: Provider<SessionStateRepository>
    lateinit var identHubSessionUseCaseProvider: Provider<IdentHubSessionUseCase>
    private lateinit var identityInitializationDataSourceProvider: Provider<IdentityInitializationDataSource>
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
    lateinit var initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>

    lateinit var loggerRetrofitDataSourceProvider: Provider<LoggerRetrofitDataSource>
    lateinit var loggerRepositoryProvider: Provider<LoggerRepository>
    lateinit var loggerApiProvider: Provider<LoggerAPI>
    lateinit var loggerUseCaseProvider: Provider<LoggerUseCase>

    init {
        initialize()
    }

    fun getIdentificationLocalDataSourceProvider(): Provider<IdentificationLocalDataSource> {
        return identificationInMemoryDataSourceProvider
    }

    fun getIdentityInitializationDataSource(): Provider<IdentityInitializationDataSource> {
        return identityInitializationDataSourceProvider
    }

    fun getLoggerUseCase(): Provider<LoggerUseCase> {
        return loggerUseCaseProvider
    }

    private fun initialize() {

        rxJavaCallAdapterFactoryProvider =
            DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        moshiConverterFactoryProvider =
            DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))
        userAgentInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideUserAgentInterceptorFactory.create()
        )
        httpLoggingInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule)
        )
        httpLoggingInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule)
        )
        sessionUrlLocalDataSourceProvider =
            DoubleCheck.provider(SessionUrlLocalDataSourceFactory.create(sessionModule))

        identityInitializationDataSourceProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationDataSource> {
            override fun get(): IdentityInitializationInMemoryDataSource {
                return IdentityInitializationInMemoryDataSource()
            }
        })
        identityInitializationRepositoryProvider = DoubleCheck.provider(object :
            Factory<IdentityInitializationRepository> {
            override fun get(): IdentityInitializationRepository {
                return IdentityInitializationRepositoryImpl(identityInitializationDataSourceProvider.get())
            }
        })
        sessionUrlRepositoryProvider =
            DoubleCheck.provider(
                ProvideSessionUrlRepositoryFactory.create(
                    sessionModule,
                    sessionUrlLocalDataSourceProvider
                )
            )
        dynamicBaseUrlInterceptorProvider =
            DoubleCheck.provider(
                NetworkModuleProvideDynamicUrlInterceptorFactory.create(
                    networkModule,
                    sessionUrlRepositoryProvider
                )
            )
        loggingInterceptorProvider = DoubleCheck.provider(object : Factory<LoggerHttpInterceptor> {
            override fun get(): LoggerHttpInterceptor {
                return LoggerHttpInterceptor()
            }
        })
        okHttpClientProvider = DoubleCheck.provider(
            NetworkModuleProvideOkHttpClientFactory.create(
                networkModule,
                userAgentInterceptorProvider,
                dynamicBaseUrlInterceptorProvider,
                httpLoggingInterceptorProvider,
                loggingInterceptorProvider
            )
        )
        retrofitProvider = DoubleCheck.provider(
            NetworkModuleProvideRetrofitFactory.create(
                networkModule,
                moshiConverterFactoryProvider,
                okHttpClientProvider,
                rxJavaCallAdapterFactoryProvider
            )
        )

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

        identHubSessionStateSavedStateHandleDataSourceProvider = DoubleCheck.provider(
            ProvideSessionStateDataSourceFactory.create(savedStateHandle)
        )

        identHubSessionStateRepositoryProvider = DoubleCheck.provider(
            ProvideSessionStateRepositoryFactory.create(identHubSessionStateSavedStateHandleDataSourceProvider.get())
        )

        identHubSessionUseCaseProvider = DoubleCheck.provider(object :
            Factory<IdentHubSessionUseCase> {
            override fun get(): IdentHubSessionUseCase {
                return IdentHubSessionUseCase(
                    identHubSessionRepositoryProvider.get(),
                    initializationInfoRepositoryProvider.get(),
                    identHubSessionStateRepositoryProvider.get(),
                    sessionUrlRepositoryProvider.get(),
                    identityInitializationRepositoryProvider.get()
                )
            }
        })

        val initializationInfoApiProvider = DoubleCheck.provider(InitializationInfoApiFactory(coreModule, retrofitProvider))
        val initializationInfoRetrofitDataSourceProvider = DoubleCheck.provider(
            InitializationInfoRetrofitDataSourceFactory(coreModule, initializationInfoApiProvider)
        )

        initializationInfoRepositoryProvider = DoubleCheck.provider(
            InitializationInfoRepositoryFactory(
                coreModule,
                savedStateHandle,
                initializationInfoRetrofitDataSourceProvider,
                sessionUrlRepositoryProvider
            )
        )
        loggerApiProvider = DoubleCheck.provider(LoggerApiFactory(coreModule, retrofitProvider))
        loggerRetrofitDataSourceProvider =
            DoubleCheck.provider(LoggerRetrofitDataSourceFactory(coreModule, loggerApiProvider))
        loggerRepositoryProvider = DoubleCheck.provider(
            LoggerRepositoryFactory(
                coreModule,
                loggerRetrofitDataSourceProvider
            )
        )
        loggerUseCaseProvider = DoubleCheck.provider(object : Factory<LoggerUseCase> {
            override fun get(): LoggerUseCase {
                return LoggerUseCase(loggerRepositoryProvider.get())
            }

        })
    }

    internal class ApplicationContextProvider(val applicationContext: Context) :
        Provider<Context> {
        override fun get(): Context {
            return applicationContext
        }
    }


    companion object {
        private val lock = Any()
        private var identHubSessionComponent: IdentHubViewModelComponent? = null

        fun getInstance(applicationContext: Context, savedStateHandle: SavedStateHandle): IdentHubViewModelComponent {
            synchronized(lock) {
                if(identHubSessionComponent == null) {
                    identHubSessionComponent = IdentHubViewModelComponent(
                        coreModule = CoreModule(),
                        networkModule = NetworkModule(),
                        savedStateHandle = savedStateHandle,
                        sessionModule = SessionModule(),
                        applicationContextProvider = ApplicationContextProvider(applicationContext)
                    )
                }
            }
            return identHubSessionComponent!!
        }
    }

}