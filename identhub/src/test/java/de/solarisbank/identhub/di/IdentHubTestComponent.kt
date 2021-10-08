package de.solarisbank.identhub.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.contract.ContractActivityInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.identhub.contract.ContractModule
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.bank.*
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.phone.*
import de.solarisbank.identhub.file.FileController
import de.solarisbank.identhub.file.FileControllerFactory
import de.solarisbank.identhub.identity.IdentityActivity
import de.solarisbank.identhub.identity.IdentityActivityInjector
import de.solarisbank.identhub.identity.IdentityModule
import de.solarisbank.identhub.session.data.contract.ContractSignApi
import de.solarisbank.identhub.session.data.contract.ContractSignModule
import de.solarisbank.identhub.session.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.session.data.contract.factory.ContractSignNetworkDataSourceFactory
import de.solarisbank.identhub.session.data.contract.factory.ProvideContractSignApiFactory
import de.solarisbank.identhub.session.data.contract.factory.ProvideContractSignRepositoryFactory.Companion.create
import de.solarisbank.identhub.session.data.di.NetworkModuleProvideUserAgentInterceptorFactory
import de.solarisbank.identhub.session.data.di.ProvideSessionUrlRepositoryFactory.Companion.create
import de.solarisbank.identhub.session.data.di.SessionModule
import de.solarisbank.identhub.session.data.di.SessionUrlLocalDataSourceFactory.Companion.create
import de.solarisbank.identhub.session.data.network.UserAgentInterceptor
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferencesFactory.Companion.create
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
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.data.di.datasource.IdentificationInMemoryDataSourceFactory
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
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class IdentHubTestComponent {

    private lateinit var coreModule: CoreModule
    private lateinit var verficationBankModule: VerificationBankModule
    private lateinit var contractModule: ContractModule
    private lateinit var identityModule: IdentityModule
//    private lateinit var activitySubModule: ActivitySubModule
    private lateinit var networkModule: NetworkModule
    private lateinit var identificationModule: IdentificationModule

//    private lateinit var applicationContextProvider: Provider<Context>

    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoApiProvider: Provider<InitializationInfoApi>
    private lateinit var initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>
    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var contractSignApiProvider: Provider<ContractSignApi>
    private lateinit var contractSignNetworkDataSourceProvider: Provider<ContractSignNetworkDataSource>
    private lateinit var contractSignRepositoryProvider: Provider<ContractSignRepository>
    private lateinit var authorizeContractSignUseCaseProvider: Provider<AuthorizeContractSignUseCase>
    private lateinit var confirmContractSignUseCaseProvider: Provider<ConfirmContractSignUseCase>
    private lateinit var deleteAllLocalStorageUseCaseProvider: Provider<DeleteAllLocalStorageUseCase>
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
    private lateinit var getDocumentsUseCaseProvider: Provider<GetDocumentsUseCase>
    private lateinit var getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>
    private lateinit var identificationApiProvider: Provider<IdentificationApi>
    private lateinit var identificationRetrofitDataSourceProvider: Provider<IdentificationRetrofitDataSource>
    private lateinit var mobileNumberApiProvider: Provider<MobileNumberApi>
    private lateinit var mobileNumberDataSourceProvider: Provider<MobileNumberDataSource>
    private lateinit var identificationLocalDataSourceProvider: Provider<out IdentificationLocalDataSource>
    private lateinit var identificationRepositoryProvider: Provider<IdentificationRepository>
    private lateinit var identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>
    private lateinit var getMobileNumberUseCaseProvider: Provider<GetMobileNumberUseCase>
//    private lateinit var sharedPreferencesProvider: Provider<SharedPreferences>
//    private lateinit var identityInitializationSharedPrefsDataSourceProvider: Provider<IdentityInitializationSharedPrefsDataSource>
    lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>


    private constructor (
        //todo provide all required mocked dependencies
        coreModule: CoreModule = CoreModule(),
        libraryComponent: LibraryComponent = mockk<LibraryComponent>(),
        identityModule: IdentityModule = IdentityModule(),
//        activitySubModule: ActivitySubModule,
        networkModule: NetworkModule,
        contractSignModule: ContractSignModule,
        verificationPhoneModule: VerificationPhoneModule,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule,
        verficationBankModule: VerificationBankModule,
        contractModule: ContractModule,
        identificationModule: IdentificationModule
    ) {
        this.coreModule = coreModule
        this.identityModule = identityModule
//        this.activitySubModule = activitySubModule
        this.networkModule = networkModule
        this.verficationBankModule = verficationBankModule
        this.contractModule = contractModule
        this.identificationModule = identificationModule
        initialize(
//            libraryComponent,
            contractSignModule,
            sessionModule,
            verificationBankDataModule,
            verificationPhoneModule
        )
    }

    private fun initialize(
//        libraryComponent: LibraryComponent,
        contractSignModule: ContractSignModule,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule,
        verificationPhoneModule: VerificationPhoneModule
    ) {
//        applicationContextProvider = ApplicationContextProvider(libraryComponent)
        identificationLocalDataSourceProvider = DoubleCheck.provider(
            IdentificationInMemoryDataSourceFactory.create()
        )
        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(
            create(sessionModule)
        )
        sessionUrlRepositoryProvider = DoubleCheck.provider(
            create(sessionModule, sessionUrlLocalDataSourceProvider!!)
        )
//        sharedPreferencesProvider = DoubleCheck.provider(
//            Factory {
//                applicationContextProvider.get()
//                    .getSharedPreferences("identhub", Context.MODE_PRIVATE)
//            } as Factory<SharedPreferences>)
//        identityInitializationSharedPrefsDataSourceProvider = DoubleCheck.provider(
//            Factory {
//                IdentityInitializationSharedPrefsDataSource(
//                    sharedPreferencesProvider.get()
//                )
//            } as Factory<IdentityInitializationSharedPrefsDataSource>)
        //todo move to identityInitializationRepositoryTestFactory
        identityInitializationRepositoryProvider = DoubleCheck.provider(
            object : Factory<IdentityInitializationRepository> {
                override fun get(): IdentityInitializationRepository {
                    return mockk<IdentityInitializationRepository>() {
                        every { getInitializationDto() } returns basicInitializationDto }
                }
            })
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(
            create(
                networkModule, sessionUrlRepositoryProvider
            )
        )
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
                httpLoggingInterceptorProvider
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
        contractSignApiProvider = DoubleCheck.provider(
            ProvideContractSignApiFactory.create(
                contractSignModule,
                retrofitProvider
            )
        )
        contractSignNetworkDataSourceProvider =
            ContractSignNetworkDataSourceFactory.create(contractSignModule, contractSignApiProvider)
        contractSignRepositoryProvider = DoubleCheck.provider(
            create(
                contractSignModule, contractSignNetworkDataSourceProvider,
                identificationLocalDataSourceProvider!!
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
        authorizeContractSignUseCaseProvider =
            AuthorizeContractSignUseCaseFactory.create(contractSignRepositoryProvider)
        confirmContractSignUseCaseProvider =
            ConfirmContractSignUseCaseFactory.create(contractSignRepositoryProvider)
        deleteAllLocalStorageUseCaseProvider =
            DeleteAllLocalStorageUseCaseFactory.create(contractSignRepositoryProvider)
        authorizeVerificationPhoneUseCaseProvider =
            AuthorizeVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        confirmVerificationPhoneUseCaseProvider =
            ConfirmVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        fetchingAuthorizedIBanStatusUseCaseProvider =
            FetchingAuthorizedIBanStatusUseCaseFactory.create(verificationBankRepositoryProvider)
        getDocumentsUseCaseProvider =
            GetDocumentsUseCaseFactory.create(contractSignRepositoryProvider)
        getIdentificationUseCaseProvider =
            create(contractSignRepositoryProvider, identityInitializationRepositoryProvider)
        verifyIBanUseCaseProvider = VerifyIBanUseCaseFactory.create(
            verificationBankRepositoryProvider,
            identityInitializationRepositoryProvider
        )
        initializationInfoApiProvider = DoubleCheck.provider(
            InitializationInfoApiFactory(
                coreModule!!, retrofitProvider
            )
        )
        initializationInfoRetrofitDataSourceProvider = DoubleCheck.provider(
            InitializationInfoRetrofitDataSourceFactory(
                coreModule, initializationInfoApiProvider
            )
        )
//        val customizationSharedPrefsStoreProvider = DoubleCheck.provider(
//            CustomizationSharedPrefsCacheFactory(
//                coreModule, sharedPreferencesProvider
//            )
//        )
        customizationRepositoryProvider = DoubleCheck.provider(
            object :  Factory<CustomizationRepository> {
                override fun get(): CustomizationRepository {
                    return mockk<CustomizationRepository>()
                }

            }
        )
    }


//    fun activitySubcomponent(): IdentHubActivitySubcomponent.Factory? {
//        return ActivitySubcomponentFactory()
//    }

    private class Builder constructor() {
        private lateinit var coreModule: CoreModule
        private lateinit var identityModule: IdentityModule
        private lateinit var libraryComponent: LibraryComponent
        private lateinit var activitySubModule: ActivitySubModule
        private lateinit var networkModule: NetworkModule
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

        fun build(): IdentHubTestComponent {
            return IdentHubTestComponent(
                coreModule,
                libraryComponent,
                identityModule,
//                activitySubModule,
                networkModule,
                ContractSignModule(),
                VerificationPhoneModule(),
                SessionModule(),
                VerificationBankDataModule(),
                VerificationBankModule(),
                ContractModule(),
                IdentificationModule()
            )
        }
    }

//    internal class ApplicationContextProvider(private val libraryComponent: LibraryComponent) :
//        Provider<Context> {
//        override fun get(): Context {
//            return libraryComponent.applicationContext()
//        }
//    }

//    inner class ActivitySubcomponentFactory : IdentHubActivitySubcomponent.Factory {
//        override fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent {
//            return IdentHubActivitySubcomponentImp(activityComponent, this@IdentHubTestComponent.activitySubModule)
//        }
//    }

    private inner class IdentHubActivitySubcomponentImp(
        activityComponent: CoreActivityComponent,
        activitySubModule: ActivitySubModule
    ) :
        IdentHubActivitySubcomponent {
        private lateinit var contextProvider: Provider<Context>
        private lateinit var sharedPreferencesProvider: Provider<SharedPreferences>
        private lateinit var identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>
        private lateinit var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>
        private lateinit var fileControllerProvider: Provider<FileController>
        private lateinit var fetchPdfUseCaseProvider: Provider<FetchPdfUseCase>
        private lateinit var bankIdPostUseCaseProvider: Provider<BankIdPostUseCase>
        lateinit var processingVerificationUseCaseProvider: Provider<ProcessingVerificationUseCase>
        private lateinit var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>
        private lateinit var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>

        private fun initialize(
            activityComponent: CoreActivityComponent,
            identHubModule: ActivitySubModule
        ) {
            contextProvider = ContextProvider(activityComponent)
            sharedPreferencesProvider = SharedPreferencesProvider(activityComponent)
            identificationStepPreferencesProvider = DoubleCheck.provider(
                create(
                    identHubModule,
                    sharedPreferencesProvider!!
                )
            )
            fileControllerProvider =
                DoubleCheck.provider(FileControllerFactory.create(contextProvider))
            fetchPdfUseCaseProvider = FetchPdfUseCaseFactory.create(
                contractSignRepositoryProvider,
                fileControllerProvider
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
            bankIdPostUseCaseProvider = BankIdPostUseCaseFactory.create(
                verificationBankRepositoryProvider,
                identityInitializationRepositoryProvider
            )
            processingVerificationUseCaseProvider = create(
                identificationPollingStatusUseCaseProvider,
                bankIdPostUseCaseProvider,
                identityInitializationRepositoryProvider
            )
//            mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider.create(
//                coreModule,
//                identityModule,
//                verficationBankModule,
//                contractModule,
//                this@IdentHubTestComponent.authorizeVerificationPhoneUseCaseProvider,
//                this@IdentHubTestComponent.confirmVerificationPhoneUseCaseProvider,
//                this@IdentHubTestComponent.getDocumentsUseCaseProvider,
//                this@IdentHubTestComponent.getIdentificationUseCaseProvider,
//                this@IdentHubTestComponent.fetchingAuthorizedIBanStatusUseCaseProvider,
//                fetchPdfUseCaseProvider,
//                identificationStepPreferencesProvider,
//                this@IdentHubTestComponent.verifyIBanUseCaseProvider,
//                identificationPollingStatusUseCaseProvider,
//                bankIdPostUseCaseProvider,
//                processingVerificationUseCaseProvider,
//                customizationRepositoryProvider
//            )
//            saveStateViewModelMapProvider = SaveStateViewModelMapProvider.create(
//                this@IdentHubTestComponent.authorizeContractSignUseCaseProvider,
//                this@IdentHubTestComponent.confirmContractSignUseCaseProvider,
//                deleteAllLocalStorageUseCaseProvider,
//                fetchPdfUseCaseProvider,
//                this@IdentHubTestComponent.getDocumentsUseCaseProvider,
//                this@IdentHubTestComponent.getIdentificationUseCaseProvider,
//                this@IdentHubTestComponent.identificationPollingStatusUseCaseProvider,
//                this@IdentHubTestComponent.fetchingAuthorizedIBanStatusUseCaseProvider,
//                this@IdentHubTestComponent.identityModule,
//                identificationStepPreferencesProvider,
//                this@IdentHubTestComponent.sessionUrlRepositoryProvider,
//                this@IdentHubTestComponent.verficationBankModule,
//                this@IdentHubTestComponent.contractModule,
//                getMobileNumberUseCaseProvider
//            )
//            assistedViewModelFactoryProvider = DoubleCheck.provider(
//                create(
//                    this@IdentHubTestComponent.activitySubModule,
//                    mapOfClassOfAndProviderOfViewModelProvider,
//                    saveStateViewModelMapProvider
//                )
//            )
        }

        override fun inject(verificationBankActivity: VerificationBankActivity) {
            injectAssistedViewModelFactory(
                verificationBankActivity,
                assistedViewModelFactoryProvider!!.get()
            )
        }

        override fun inject(contractActivity: ContractActivity) {
            injectAssistedViewModelFactory(
                contractActivity,
                assistedViewModelFactoryProvider!!.get()
            )
        }

        override fun inject(identityActivity: IdentityActivity?) {
            IdentityActivityInjector.injectAssistedViewModelFactory(
                identityActivity,
                assistedViewModelFactoryProvider!!.get()
            )
        }

        override fun fragmentComponent(): FragmentComponent.Factory {
            return mockk<FragmentComponent.Factory>()
        }

        internal inner class ContextProvider(private val activityComponent: CoreActivityComponent) :
            Provider<Context> {
            override fun get(): Context {
                return activityComponent.context()
            }
        }

        internal inner class SharedPreferencesProvider(private val activityComponent: CoreActivityComponent) :
            Provider<SharedPreferences> {
            override fun get(): SharedPreferences {
                return activityComponent.sharedPreferences()
            }
        }

//        internal inner class FragmentComponentFactory : FragmentComponent.Factory {
//            override fun create(): FragmentComponent {
//                return FragmentComponentImp()
//            }
//        }

//        private inner class FragmentComponentImp constructor() : FragmentComponent {
//            private val fragmentAssistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>
//            private val baseFragmentDependencies: BaseFragmentDependencies
//            override fun inject(verificationPhoneFragment: VerificationPhoneFragment) {
//                VerificationPhoneFragmentInjector(baseFragmentDependencies).injectMembers(
//                    verificationPhoneFragment
//                )
//            }
//
//            override fun inject(successMessageFragment: VerificationPhoneSuccessMessageFragment) {
//                VerificationPhoneSuccessMessageFragmentInjector(baseFragmentDependencies).injectMembers(
//                    successMessageFragment
//                )
//            }
//
//            override fun inject(errorMessageFragment: VerificationPhoneErrorMessageFragment) {
//                VerificationPhoneErrorMessageFragmentInjector(baseFragmentDependencies).injectMembers(
//                    errorMessageFragment
//                )
//            }
//
//            override fun inject(verificationBankIbanFragment: VerificationBankIbanFragment) {
//                VerificationBankFragmentInjector(baseFragmentDependencies).injectMembers(
//                    verificationBankIbanFragment
//                )
//            }
//
//            override fun inject(verificationBankExternalGatewayFragment: VerificationBankExternalGatewayFragment) {
//                VerificationBankExternalGatewayFragmentInjector(baseFragmentDependencies).injectMembers(
//                    verificationBankExternalGatewayFragment
//                )
//            }
//
//            override fun inject(contractSigningFragment: ContractSigningFragment) {
//                ContractSigningFragmentInjector(baseFragmentDependencies).injectMembers(
//                    contractSigningFragment
//                )
//            }
//
//            override fun inject(contractSigningPreviewFragment: ContractSigningPreviewFragment) {
//                ContractSigningPreviewFragmentInjector(baseFragmentDependencies).injectMembers(
//                    contractSigningPreviewFragment
//                )
//            }
//
//            override fun inject(progressIndicatorFragment: ProgressIndicatorFragment) {
//                ProgressIndicatorFragmentInjector(baseFragmentDependencies).injectMembers(
//                    progressIndicatorFragment
//                )
//            }
//
//            override fun inject(verificationBankIntroFragment: VerificationBankIntroFragment) {
//                VerificationBankIntroFragmentInjector(baseFragmentDependencies).injectMembers(
//                    verificationBankIntroFragment
//                )
//            }

//            init {
//                fragmentAssistedViewModelFactoryProvider = DoubleCheck.provider(
//                    create(
//                        this@IdentHubTestComponent.activitySubModule,
//                        mapOfClassOfAndProviderOfViewModelProvider!!,
//                        saveStateViewModelMapProvider!!
//                    )
//                )
//                baseFragmentDependencies = BaseFragmentDependencies(
//                    fragmentAssistedViewModelFactoryProvider,
//                    customizationRepositoryProvider
//                )
            }
//        }

//        init {
//            initialize(activityComponent, activitySubModule)
//        }
//    }

    companion object {

        fun getTestInstance(networkModule: NetworkModule): IdentHubTestComponent {
            return Builder()
                .setLibraryComponent(mockk<LibraryComponent>())
                .setCoreModule(CoreModule())
                .setIdentityModule(IdentityModule())
                .setActivitySubModule(ActivitySubModule())
                .setNetworkModule(networkModule)
                .build()
        }
    }
}