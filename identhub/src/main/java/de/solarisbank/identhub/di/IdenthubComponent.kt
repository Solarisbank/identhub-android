package de.solarisbank.identhub.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.di.ActivitySubModuleAssistedViewModelFactory.Companion.create
import de.solarisbank.identhub.domain.verification.bank.*
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.progress.ProgressIndicatorFragmentInjector
import de.solarisbank.identhub.session.data.di.NetworkModuleProvideUserAgentInterceptorFactory
import de.solarisbank.identhub.session.data.di.ProvideSessionUrlRepositoryFactory.Companion.create
import de.solarisbank.identhub.session.data.di.SessionModule
import de.solarisbank.identhub.session.data.di.SessionUrlLocalDataSourceFactory.Companion.create
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.repository.IdentityInitializationRepositoryImpl
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankApi
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankNetworkDataSource
import de.solarisbank.identhub.session.data.verification.bank.factory.ProvideVerificationBankApiFactory
import de.solarisbank.identhub.session.data.verification.bank.factory.ProvideVerificationBankRepositoryFactory
import de.solarisbank.identhub.session.data.verification.bank.factory.VerificationBankNetworkDataSourceFactory
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel.Companion.INSTANCE
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import de.solarisbank.identhub.verfication.bank.VerificationBankActivityInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.identhub.verfication.bank.VerificationBankActivityInjector.Companion.injectCustomizationRepository
import de.solarisbank.identhub.verfication.bank.VerificationBankFragmentInjector
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanFragment
import de.solarisbank.identhub.verfication.bank.VerificationBankModule
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragmentInjector
import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.CustomizationRepositoryFactory
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.data.di.datasource.IdentificationRetrofitDataSourceFactory.Companion.create
import de.solarisbank.sdk.data.di.datasource.MobileNumberDataSourceFactory.Companion.create
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory.Companion.create
import de.solarisbank.sdk.data.di.network.api.IdentificationApiFactory.Companion.create
import de.solarisbank.sdk.data.di.network.api.MobileNumberApiFactory.Companion.create
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentificationRepositoryFactory.Companion.create
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.di.IdentificationPollingStatusUseCaseFactory.Companion.create
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.BaseFragmentDependencies
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.logger.LoggerHttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class IdenthubComponent private constructor(
    private val coreModule: CoreModule,
    libraryComponent: LibraryComponent,
    private val activitySubModule: ActivitySubModule,
    private val networkModule: NetworkModule,
    sessionModule: SessionModule,
    verificationBankDataModule: VerificationBankDataModule,
    private val verficationBankModule: VerificationBankModule,
    private val identificationModule: IdentificationModule
) {
    private lateinit var applicationContextProvider: Provider<Context>
    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var loggingInterceptorProvider: Provider<LoggerHttpInterceptor>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
    private lateinit var verificationBankApiProvider: Provider<VerificationBankApi>
    private lateinit var verificationBankNetworkDataSourceProvider: Provider<VerificationBankNetworkDataSource>
    private lateinit var verificationBankRepositoryProvider: Provider<VerificationBankRepository>
    private lateinit var verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>
    private lateinit var fetchingAuthorizedIBanStatusUseCaseProvider: Provider<FetchingAuthorizedIBanStatusUseCase>
    private lateinit var identificationApiProvider: Provider<IdentificationApi>
    private lateinit var identificationRetrofitDataSourceProvider: Provider<IdentificationRetrofitDataSource>
    private lateinit var mobileNumberApiProvider: Provider<MobileNumberApi>
    private lateinit var mobileNumberDataSourceProvider: Provider<MobileNumberDataSource>
    private lateinit var identificationLocalDataSourceProvider: Provider<IdentificationLocalDataSource>
    private lateinit var identificationRepositoryProvider: Provider<IdentificationRepository>
    private lateinit var identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

    private fun initialize(
        libraryComponent: LibraryComponent,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule
    ) {
        applicationContextProvider = ApplicationContextProvider(libraryComponent)
        identificationLocalDataSourceProvider =
            INSTANCE!!.getIdentificationLocalDataSourceProvider()
        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(create(sessionModule))
        sessionUrlRepositoryProvider =
            DoubleCheck.provider(create(sessionModule, sessionUrlLocalDataSourceProvider))
        identityInitializationRepositoryProvider = DoubleCheck.provider(
            Factory {
                IdentityInitializationRepositoryImpl(
                    INSTANCE!!.getInitializationInMemoryDataSourceProvider().get()
                )
            })
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(
            create(
                networkModule!!,
                sessionUrlRepositoryProvider
            )
        )
        loggingInterceptorProvider = DoubleCheck.provider(
            Factory { LoggerHttpInterceptor() } as Factory<LoggerHttpInterceptor>)
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(
            NetworkModuleProvideRxJavaCallAdapterFactory.create(
                networkModule
            )
        )
        moshiConverterFactoryProvider = DoubleCheck.provider(
            NetworkModuleProvideMoshiConverterFactory.create(
                networkModule
            )
        )
        userAgentInterceptorProvider =
            DoubleCheck.provider(NetworkModuleProvideUserAgentInterceptorFactory.create())
        httpLoggingInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideHttpLoggingInterceptorFactory.create(
                networkModule
            )
        )
        okHttpClientProvider = DoubleCheck.provider(
            NetworkModuleProvideOkHttpClientFactory.create(
                networkModule,
                dynamicBaseUrlInterceptorProvider,
                userAgentInterceptorProvider,
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
        verificationBankApiProvider = DoubleCheck.provider(
            ProvideVerificationBankApiFactory.create(
                verificationBankDataModule,
                retrofitProvider
            )
        )
        verificationBankNetworkDataSourceProvider = VerificationBankNetworkDataSourceFactory.create(
            verificationBankDataModule,
            verificationBankApiProvider
        )
        verificationBankRepositoryProvider = DoubleCheck.provider(
            ProvideVerificationBankRepositoryFactory.create(
                verificationBankDataModule,
                verificationBankNetworkDataSourceProvider,
                identificationLocalDataSourceProvider
            )
        )
        identificationApiProvider = DoubleCheck.provider(
            create(
                identificationModule, retrofitProvider.get()
            )
        )
        identificationRetrofitDataSourceProvider = DoubleCheck.provider(
            create(
                identificationModule, identificationApiProvider.get()
            )
        )
        mobileNumberApiProvider = create(retrofitProvider.get())
        mobileNumberDataSourceProvider = create(mobileNumberApiProvider.get())
        identificationRepositoryProvider = DoubleCheck.provider(
            create(
                identificationLocalDataSourceProvider.get()!!,
                identificationRetrofitDataSourceProvider.get(),
                mobileNumberDataSourceProvider.get()
            )
        )
        identificationPollingStatusUseCaseProvider = DoubleCheck.provider(
            create(
                identificationRepositoryProvider.get(),
                identityInitializationRepositoryProvider.get()
            )
        )
        fetchingAuthorizedIBanStatusUseCaseProvider =
            FetchingAuthorizedIBanStatusUseCaseFactory.create(verificationBankRepositoryProvider)
        verifyIBanUseCaseProvider = VerifyIBanUseCaseFactory.create(
            verificationBankRepositoryProvider,
            identityInitializationRepositoryProvider
        )
        initializationInfoRepositoryProvider =
            IdentHubSessionViewModel.initializationInfoRepositoryProvider!!
        customizationRepositoryProvider = DoubleCheck.provider(
            CustomizationRepositoryFactory(
                coreModule,
                applicationContextProvider,
                initializationInfoRepositoryProvider
            )
        )
    }

    fun activitySubcomponent(): IdentHubActivitySubcomponent.Factory {
        return ActivitySubcomponentFactory()
    }

    private class Builder {
        private lateinit var coreModule: CoreModule
        private lateinit var libraryComponent: LibraryComponent
        private lateinit var activitySubModule: ActivitySubModule
        private lateinit var networkModule: NetworkModule
        fun setCoreModule(coreModule: CoreModule): Builder {
            this.coreModule = coreModule
            return this
        }

        fun setLibraryComponent(libraryComponent: LibraryComponent): Builder {
            this.libraryComponent = libraryComponent
            return this
        }

        fun setActivitySubModule(activitySubModule: ActivitySubModule): Builder {
            this.activitySubModule = activitySubModule
            return this
        }

        fun setNetworkModule(networkModule: NetworkModule): Builder {
            this.networkModule = networkModule
            return this
        }

        fun build(): IdenthubComponent {
            return IdenthubComponent(
                coreModule,
                libraryComponent,
                activitySubModule,
                networkModule,
                SessionModule(),
                VerificationBankDataModule(),
                VerificationBankModule(),
                IdentificationModule()
            )
        }
    }

    internal class ApplicationContextProvider(private val libraryComponent: LibraryComponent) :
        Provider<Context> {
        override fun get(): Context {
            return libraryComponent.applicationContext()
        }
    }

    inner class ActivitySubcomponentFactory : IdentHubActivitySubcomponent.Factory {
        override fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent {
            return IdentHubActivitySubcomponentImp(activityComponent, activitySubModule)
        }
    }

    private inner class IdentHubActivitySubcomponentImp(
        activityComponent: CoreActivityComponent,
        activitySubModule: ActivitySubModule
    ) : IdentHubActivitySubcomponent {
        private lateinit var contextProvider: Provider<Context>
        private lateinit var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>
        private lateinit var bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>
        lateinit var processingVerificationUseCaseProvider: Provider<ProcessingVerificationUseCase>
        private lateinit var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>
        private lateinit var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>

        private fun initialize(
            activityComponent: CoreActivityComponent,
            identHubModule: ActivitySubModule
        ) {
            contextProvider = ContextProvider(activityComponent)
            bankIdPostUseCaseProvider = create(
                verificationBankRepositoryProvider!!, identityInitializationRepositoryProvider!!
            )
            processingVerificationUseCaseProvider = create(
                identificationPollingStatusUseCaseProvider!!,
                bankIdPostUseCaseProvider,
                identityInitializationRepositoryProvider!!
            )
            mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider(
                coreModule,
                verficationBankModule,
                verifyIBanUseCaseProvider!!,
                bankIdPostUseCaseProvider,
                processingVerificationUseCaseProvider,
                customizationRepositoryProvider!!,
                initializationInfoRepositoryProvider!!
            )
            saveStateViewModelMapProvider = SaveStateViewModelMapProvider(
                fetchingAuthorizedIBanStatusUseCaseProvider,
                initializationInfoRepositoryProvider,
                sessionUrlRepositoryProvider,
                verficationBankModule,
                identificationLocalDataSourceProvider
            )
            assistedViewModelFactoryProvider = DoubleCheck.provider(
                create(
                    activitySubModule!!,
                    mapOfClassOfAndProviderOfViewModelProvider,
                    saveStateViewModelMapProvider
                )
            )
        }

        override fun inject(verificationBankActivity: VerificationBankActivity) {
            injectAssistedViewModelFactory(
                verificationBankActivity,
                assistedViewModelFactoryProvider!!.get()
            )
            injectCustomizationRepository(
                verificationBankActivity,
                customizationRepositoryProvider!!.get()
            )
        }

        override fun fragmentComponent(): FragmentComponent.Factory {
            return FragmentComponentFactory()
        }

        internal inner class ContextProvider(private val activityComponent: CoreActivityComponent) :
            Provider<Context> {
            override fun get(): Context {
                return activityComponent.context()
            }
        }

        internal inner class FragmentComponentFactory : FragmentComponent.Factory {
            override fun create(): FragmentComponent {
                return FragmentComponentImp()
            }
        }

        private inner class FragmentComponentImp : FragmentComponent {
            private val fragmentAssistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>
            private val baseFragmentDependencies: BaseFragmentDependencies

            override fun inject(verificationBankIbanFragment: VerificationBankIbanFragment) {
                VerificationBankFragmentInjector(baseFragmentDependencies).injectMembers(
                    verificationBankIbanFragment
                )
            }

            override fun inject(verificationBankExternalGatewayFragment: VerificationBankExternalGatewayFragment) {
                VerificationBankExternalGatewayFragmentInjector(baseFragmentDependencies).injectMembers(
                    verificationBankExternalGatewayFragment
                )
            }

            override fun inject(progressIndicatorFragment: ProgressIndicatorFragment) {
                ProgressIndicatorFragmentInjector(baseFragmentDependencies).injectMembers(
                    progressIndicatorFragment
                )
            }

            init {
                fragmentAssistedViewModelFactoryProvider = DoubleCheck.provider(
                    create(
                        activitySubModule!!,
                        mapOfClassOfAndProviderOfViewModelProvider!!,
                        saveStateViewModelMapProvider!!
                    )
                )
                baseFragmentDependencies = BaseFragmentDependencies(
                    fragmentAssistedViewModelFactoryProvider,
                    customizationRepositoryProvider
                )
            }
        }

        init {
            initialize(activityComponent, activitySubModule)
        }
    }

    companion object {
        private val lock = Any()
        private var identhubComponent: IdenthubComponent? = null
        fun getInstance(libraryComponent: LibraryComponent): IdenthubComponent {
            synchronized(lock) {
                if (identhubComponent == null) {
                    identhubComponent = Builder()
                        .setLibraryComponent(libraryComponent)
                        .setCoreModule(CoreModule())
                        .setActivitySubModule(ActivitySubModule())
                        .setNetworkModule(NetworkModule())
                        .build()
                }
                return identhubComponent!!
            }
        }
    }

    init {
        initialize(
            libraryComponent,
            sessionModule,
            verificationBankDataModule
        )
    }
}