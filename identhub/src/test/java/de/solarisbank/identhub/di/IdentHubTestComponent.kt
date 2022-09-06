package de.solarisbank.identhub.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.contract.step.parameters.*
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersDataSourceFactory.Companion.create
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepositoryFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.GetPersonDataUseCaseFactory
import de.solarisbank.identhub.domain.verification.bank.*
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.phone.*
import de.solarisbank.identhub.identity.IdentityModule
import de.solarisbank.identhub.session.data.di.NetworkModuleProvideUserAgentInterceptorFactory
import de.solarisbank.identhub.session.data.di.ProvideSessionUrlRepositoryFactory.Companion.create
import de.solarisbank.identhub.session.data.di.SessionModule
import de.solarisbank.identhub.session.data.di.SessionUrlLocalDataSourceFactory.Companion.create
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankApi
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankNetworkDataSource
import de.solarisbank.identhub.session.data.verification.bank.factory.ProvideVerificationBankApiFactory
import de.solarisbank.identhub.session.data.verification.bank.factory.ProvideVerificationBankRepositoryFactory
import de.solarisbank.identhub.session.data.verification.bank.factory.VerificationBankNetworkDataSourceFactory
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneApi
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneModule
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.session.data.verification.phone.factory.ProvideVerificationPhoneApiFactory
import de.solarisbank.identhub.session.data.verification.phone.factory.ProvideVerificationPhoneRepositoryFactory
import de.solarisbank.identhub.session.data.verification.phone.factory.VerificationPhoneNetworkDataSourceFactory
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import de.solarisbank.identhub.verfication.bank.VerificationBankActivityInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.identhub.verfication.bank.VerificationBankModule
import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.data.di.datasource.IdentificationRetrofitDataSourceFactory
import de.solarisbank.sdk.data.di.datasource.MobileNumberDataSourceFactory
import de.solarisbank.sdk.data.di.network.*
import de.solarisbank.sdk.data.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory.Companion.create
import de.solarisbank.sdk.data.di.network.api.IdentificationApiFactory
import de.solarisbank.sdk.data.di.network.api.MobileNumberApiFactory
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentificationRepositoryFactory
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.di.IdentificationPollingStatusUseCaseFactory
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoApi
import de.solarisbank.sdk.feature.config.InitializationInfoApiFactory
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSourceFactory
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.logger.LoggerHttpInterceptor
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class IdentHubTestComponent {
//    val identHubActivitySubcomponentImp = ActivitySubcomponentFactory().create(mockk<CoreActivityComponent>())
    private lateinit var coreModule: CoreModule
    private lateinit var verficationBankModule: VerificationBankModule
    private lateinit var identityModule: IdentityModule
    private lateinit var activitySubModule: ActivitySubModule
    private lateinit var networkModule: NetworkModule
    private lateinit var identificationModule: IdentificationModule

//    private lateinit var applicationContextProvider: Provider<Context>

    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoApiProvider: Provider<InitializationInfoApi>

    private lateinit var qesStepParametersDataSourceProvider: Provider<QesStepParametersDataSource>
    private lateinit var qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
    private lateinit var qesStepParametersUseCaseProvider: Provider<QesStepParametersUseCase>
    private lateinit var initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var verificationPhoneApiProvider: Provider<VerificationPhoneApi>
    private lateinit var verificationPhoneNetworkDataSourceProvider: Provider<VerificationPhoneNetworkDataSource>
    private lateinit var verificationPhoneRepositoryProvider: Provider<VerificationPhoneRepository>
    private lateinit var authorizeVerificationPhoneUseCaseProvider: Provider<AuthorizeVerificationPhoneUseCase>
    private lateinit var confirmVerificationPhoneUseCaseProvider: Provider<ConfirmVerificationPhoneUseCase>
    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
    private lateinit var verificationBankApiProvider: Provider<VerificationBankApi>
    private lateinit var verificationBankNetworkDataSourceProvider: Provider<VerificationBankNetworkDataSource>
    private lateinit var verificationBankRepositoryProvider: Provider<VerificationBankRepository>
    lateinit var verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>
    private lateinit var fetchingAuthorizedIBanStatusUseCaseProvider: Provider<FetchingAuthorizedIBanStatusUseCase>
    private lateinit var identificationApiProvider: Provider<IdentificationApi>
    private lateinit var identificationRetrofitDataSourceProvider: Provider<IdentificationRetrofitDataSource>
    private lateinit var mobileNumberApiProvider: Provider<MobileNumberApi>
    private lateinit var mobileNumberDataSourceProvider: Provider<MobileNumberDataSource>
    private lateinit var identificationLocalDataSourceProvider: Provider<out IdentificationLocalDataSource>
    private lateinit var identificationRepositoryProvider: Provider<IdentificationRepository>
    private lateinit var identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>
    private lateinit var getMobileNumberUseCaseProvider: Provider<GetMobileNumberUseCase>
    lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
    private lateinit var loggingInterceptorProvider: Provider<LoggerHttpInterceptor>


    private constructor (
        //todo provide all required mocked dependencies
        coreModule: CoreModule,
        libraryComponent: LibraryComponent,
        identityModule: IdentityModule,
        activitySubModule: ActivitySubModule,
        networkModule: NetworkModule,
        verificationPhoneModule: VerificationPhoneModule,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule,
        verficationBankModule: VerificationBankModule,
        identificationModule: IdentificationModule
    ) {
        this.coreModule = coreModule
        this.identityModule = identityModule
        this.activitySubModule = activitySubModule
        this.networkModule = networkModule
        this.verficationBankModule = verficationBankModule
        this.identificationModule = identificationModule
        initialize(
//            libraryComponent,
            sessionModule,
            verificationBankDataModule,
            verificationPhoneModule
        )
    }

    private fun initialize(
//        libraryComponent: LibraryComponent,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule,
        verificationPhoneModule: VerificationPhoneModule
    ) {
        activitySubModule = ActivitySubModule()
//        applicationContextProvider = ApplicationContextProvider(libraryComponent)
        qesStepParametersDataSourceProvider = DoubleCheck.provider(create())
        qesStepParametersRepositoryProvider =
            DoubleCheck.provider(create(qesStepParametersDataSourceProvider.get()))
        qesStepParametersUseCaseProvider =
            DoubleCheck.provider(QesStepParametersUseCaseFactory.create(qesStepParametersRepositoryProvider.get()))


        identificationLocalDataSourceProvider =
            identificationModule.provideIdentificationLocalDataSource()
        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(
            create(sessionModule)
        )
        sessionUrlRepositoryProvider = DoubleCheck.provider(
            create(sessionModule, sessionUrlLocalDataSourceProvider!!)
        )

        //todo move to identityInitializationRepositoryTestFactory
        identityInitializationRepositoryProvider = DoubleCheck.provider(
            object : Factory<IdentityInitializationRepository> {
                override fun get(): IdentityInitializationRepository {
                    return mockk() {
                        every { getInitializationDto() } returns basicInitializationDto
                    }
                }
            })
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(
            create(
                networkModule, sessionUrlRepositoryProvider
            )
        )
        loggingInterceptorProvider = DoubleCheck.provider(
            Factory { LoggerHttpInterceptor() })
        rxJavaCallAdapterFactoryProvider =
            DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        moshiConverterFactoryProvider =
            DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))
        userAgentInterceptorProvider =
            DoubleCheck.provider(NetworkModuleProvideUserAgentInterceptorFactory.create())
        httpLoggingInterceptorProvider = DoubleCheck.provider(
            NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule)
        )
        okHttpClientProvider = DoubleCheck.provider(
            NetworkModuleProvideOkHttpClientFactory.create(
                networkModule,
                dynamicBaseUrlInterceptorProvider,
                userAgentInterceptorProvider,
                httpLoggingInterceptorProvider, loggingInterceptorProvider
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
        verificationPhoneApiProvider = DoubleCheck.provider(
            ProvideVerificationPhoneApiFactory.create(
                verificationPhoneModule,
                retrofitProvider
            )
        )
        verificationPhoneNetworkDataSourceProvider =
            VerificationPhoneNetworkDataSourceFactory.create(
                verificationPhoneModule,
                verificationPhoneApiProvider
            )
        verificationPhoneRepositoryProvider = DoubleCheck.provider(
            ProvideVerificationPhoneRepositoryFactory.create(
                verificationPhoneModule,
                verificationPhoneNetworkDataSourceProvider
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
            IdentificationApiFactory.create(
                identificationModule,
                retrofitProvider.get()
            )
        )
        identificationRetrofitDataSourceProvider = DoubleCheck.provider(
            IdentificationRetrofitDataSourceFactory.create(
                identificationModule,
                identificationApiProvider.get()
            )
        )

        mobileNumberApiProvider = MobileNumberApiFactory.create(retrofitProvider.get())
        mobileNumberDataSourceProvider =
            MobileNumberDataSourceFactory.create(mobileNumberApiProvider.get())
        identificationRepositoryProvider = DoubleCheck.provider(
            IdentificationRepositoryFactory.create(
                identificationLocalDataSourceProvider.get(),
                identificationRetrofitDataSourceProvider.get(),
                mobileNumberDataSourceProvider.get()
            )
        )
        getMobileNumberUseCaseProvider =
            GetPersonDataUseCaseFactory.create(identificationRepositoryProvider)
        identificationPollingStatusUseCaseProvider = DoubleCheck.provider(
            IdentificationPollingStatusUseCaseFactory.create(
                identificationRepositoryProvider.get(),
                identityInitializationRepositoryProvider.get()
            )
        )

        authorizeVerificationPhoneUseCaseProvider =
            AuthorizeVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        confirmVerificationPhoneUseCaseProvider =
            ConfirmVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        fetchingAuthorizedIBanStatusUseCaseProvider =
            FetchingAuthorizedIBanStatusUseCaseFactory.create(verificationBankRepositoryProvider)
        verifyIBanUseCaseProvider = VerifyIBanUseCaseFactory.create(
            verificationBankRepositoryProvider,
            identityInitializationRepositoryProvider
        )
        initializationInfoApiProvider = DoubleCheck.provider(
            InitializationInfoApiFactory(
                coreModule, retrofitProvider
            )
        )
        initializationInfoRetrofitDataSourceProvider = DoubleCheck.provider(
            InitializationInfoRetrofitDataSourceFactory(
                coreModule, initializationInfoApiProvider
            )
        )
        customizationRepositoryProvider = DoubleCheck.provider(
            object :  Factory<CustomizationRepository> {
                override fun get(): CustomizationRepository {
                    return mockk<CustomizationRepository>()
                }
            }
        )
    }


    fun activitySubcomponent(): IdentHubActivitySubcomponent.Factory {
        return ActivitySubcomponentFactory()
    }

    private class Builder {
        private lateinit var coreModule: CoreModule
        private lateinit var identityModule: IdentityModule
        private lateinit var libraryComponent: LibraryComponent
        private lateinit var activitySubModule: ActivitySubModule
        private lateinit var networkModule: NetworkModule
        private lateinit var identificationModule: IdentificationModule
        fun setCoreModule(coreModule: CoreModule): Builder {
            this.coreModule = coreModule
            return this
        }

        fun setIdentityModule(identityModule: IdentityModule): Builder {
            this.identityModule = identityModule
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

        fun setIdentificationModule(identificationModule: IdentificationModule): Builder {
            this.identificationModule = identificationModule
            return this
        }

        fun build(): IdentHubTestComponent {
            return IdentHubTestComponent(
                coreModule,
                libraryComponent,
                identityModule,
                activitySubModule,
                networkModule,
                VerificationPhoneModule(),
                SessionModule(),
                VerificationBankDataModule(),
                VerificationBankModule(),
                identificationModule,
            )
        }
    }

//    internal class ApplicationContextProvider(private val libraryComponent: LibraryComponent) :
//        Provider<Context> {
//        override fun get(): Context {
//            return libraryComponent.applicationContext()
//        }
//    }

    inner class ActivitySubcomponentFactory : IdentHubActivitySubcomponent.Factory {
        override fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent {
            return IdentHubActivitySubcomponentImp(activityComponent, activitySubModule)
        }
    }

    private inner class IdentHubActivitySubcomponentImp(
        activityComponent: CoreActivityComponent,
        activitySubModule: ActivitySubModule
    ) :
        IdentHubActivitySubcomponent {
        private lateinit var contextProvider: Provider<Context>
        private lateinit var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>
        private lateinit var bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>
        lateinit var processingVerificationUseCaseProvider: Provider<ProcessingVerificationUseCase>
        private lateinit var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>
        private lateinit var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>

        init {
            initialize(activityComponent, activitySubModule)
        }

        private fun initialize(
            activityComponent: CoreActivityComponent,
            identHubModule: ActivitySubModule
        ) {
            contextProvider = mockk<ContextProvider>() {
                every { get() } returns mockk()
            }

            bankIdPostUseCaseProvider = BankIdPostUseCaseFactory.create(
                verificationBankRepositoryProvider,
                identityInitializationRepositoryProvider
            )
            processingVerificationUseCaseProvider = create(
                identificationPollingStatusUseCaseProvider,
                bankIdPostUseCaseProvider,
                identityInitializationRepositoryProvider
            )
        }

        override fun inject(verificationBankActivity: VerificationBankActivity) {
            injectAssistedViewModelFactory(
                verificationBankActivity,
                assistedViewModelFactoryProvider!!.get()
            )
        }

        override fun fragmentComponent(): FragmentComponent.Factory {
            return mockk()
        }

        internal inner class ContextProvider(private val activityComponent: CoreActivityComponent) :
            Provider<Context> {
            override fun get(): Context {
                return activityComponent.context()
            }
        }
    }

    companion object {

        //todo modify real IdentHubComponent to allow the passing of mocked Modules
        fun getTestInstance(
            coreModule: CoreModule = mockk(relaxed = true),
            identityModule: IdentityModule = IdentityModule(),
            activitySubModule: ActivitySubModule = ActivitySubModule(),
            networkModule: NetworkModule = NetworkModule(),
            identificationModule: IdentificationModule = IdentificationModule(),
        ): IdentHubTestComponent {
            return Builder()
                .setLibraryComponent(mockk())
                .setCoreModule(coreModule)
                .setIdentityModule(identityModule)
                .setActivitySubModule(activitySubModule)
                .setNetworkModule(networkModule)
                .setIdentificationModule(identificationModule)
                .build()
                .apply { activitySubcomponent().create(mockk()) }
        }
    }
}