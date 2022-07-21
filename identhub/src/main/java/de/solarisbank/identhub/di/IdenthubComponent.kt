package de.solarisbank.identhub.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.contract.ContractActivityInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.identhub.contract.ContractActivityInjector.Companion.injectCustomizationRepository
import de.solarisbank.identhub.contract.ContractUiModule
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragmentInjector
import de.solarisbank.identhub.contract.sign.ContractSigningFragment
import de.solarisbank.identhub.contract.sign.ContractSigningFragmentInjector
import de.solarisbank.identhub.data.contract.ContractSignApi
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersDataSource
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersDataSourceFactory.Companion.create
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepositoryFactory.Companion.create
import de.solarisbank.identhub.data.di.contract.ContractSignModule
import de.solarisbank.identhub.di.ActivitySubModuleAssistedViewModelFactory.Companion.create
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.contract.step.parameters.QesStepParametersUseCase
import de.solarisbank.identhub.domain.contract.step.parameters.QesStepParametersUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.AuthorizeContractSignUseCaseFactory
import de.solarisbank.identhub.domain.di.contract.ConfirmContractSignUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.FetchPdfUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.GetDocumentsUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.GetIdentificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.di.contract.GetPersonDataUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.bank.*
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCaseFactory.Companion.create
import de.solarisbank.identhub.domain.verification.phone.*
import de.solarisbank.identhub.file.FileController
import de.solarisbank.identhub.file.FileControllerFactory
import de.solarisbank.identhub.identity.IdentityModule
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
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneApi
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneModule
import de.solarisbank.identhub.session.data.verification.phone.VerificationPhoneNetworkDataSource
import de.solarisbank.identhub.session.data.verification.phone.factory.ProvideVerificationPhoneApiFactory
import de.solarisbank.identhub.session.data.verification.phone.factory.ProvideVerificationPhoneRepositoryFactory
import de.solarisbank.identhub.session.data.verification.phone.factory.VerificationPhoneNetworkDataSourceFactory
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
import de.solarisbank.identhub.verfication.phone.PhoneVerificationFragment
import de.solarisbank.identhub.verfication.phone.PhoneVerificationFragmentInjector
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragmentInjector
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
    private val identityModule: IdentityModule,
    private val activitySubModule: ActivitySubModule,
    private val networkModule: NetworkModule,
    contractSignModule: ContractSignModule,
    verificationPhoneModule: VerificationPhoneModule,
    sessionModule: SessionModule,
    verificationBankDataModule: VerificationBankDataModule,
    private val verficationBankModule: VerificationBankModule,
    private val contractUiModule: ContractUiModule,
    private val identificationModule: IdentificationModule
) {
    private lateinit var applicationContextProvider: Provider<Context>
    private lateinit var customizationRepositoryProvider: Provider<CustomizationRepository>
    private lateinit var initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
    private lateinit var qesStepParametersDataSourceProvider: Provider<QesStepParametersDataSource>
    private lateinit var qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
    private lateinit var qesStepParametersUseCaseProvider: Provider<QesStepParametersUseCase>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var loggingInterceptorProvider: Provider<LoggerHttpInterceptor>
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
    private lateinit var verifyIBanUseCaseProvider: Provider<VerifyIBanUseCase>
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
    private lateinit var identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

    private fun initialize(
        libraryComponent: LibraryComponent,
        contractSignModule: ContractSignModule,
        sessionModule: SessionModule,
        verificationBankDataModule: VerificationBankDataModule,
        verificationPhoneModule: VerificationPhoneModule
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
        contractSignApiProvider = contractSignModule.provideContractSignApi(retrofitProvider.get())
        contractSignNetworkDataSourceProvider =
            contractSignModule.provideContractSignNetworkDataSource(
                contractSignApiProvider!!.get()
            )
        contractSignRepositoryProvider = contractSignModule.provideContractSignRepository(
            contractSignNetworkDataSourceProvider!!.get(),
            identificationLocalDataSourceProvider.get()!!
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
        qesStepParametersDataSourceProvider = DoubleCheck.provider(create())
        qesStepParametersRepositoryProvider =
            DoubleCheck.provider(create(qesStepParametersDataSourceProvider.get()))
        qesStepParametersUseCaseProvider =
            DoubleCheck.provider(create(qesStepParametersRepositoryProvider.get()))
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
        getMobileNumberUseCaseProvider = create(identificationRepositoryProvider)
        identificationPollingStatusUseCaseProvider = DoubleCheck.provider(
            create(
                identificationRepositoryProvider.get(),
                identityInitializationRepositoryProvider.get()
            )
        )
        authorizeContractSignUseCaseProvider =
            AuthorizeContractSignUseCaseFactory.create(contractSignRepositoryProvider)
        confirmContractSignUseCaseProvider = create(
            contractSignRepositoryProvider!!,
            identificationPollingStatusUseCaseProvider,
            qesStepParametersRepositoryProvider
        )
        authorizeVerificationPhoneUseCaseProvider =
            AuthorizeVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        confirmVerificationPhoneUseCaseProvider =
            ConfirmVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider)
        fetchingAuthorizedIBanStatusUseCaseProvider =
            FetchingAuthorizedIBanStatusUseCaseFactory.create(verificationBankRepositoryProvider)
        getDocumentsUseCaseProvider = create(
            contractSignRepositoryProvider!!
        )
        getIdentificationUseCaseProvider = create(
            contractSignRepositoryProvider!!, identityInitializationRepositoryProvider
        )
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

        fun build(): IdenthubComponent {
            return IdenthubComponent(
                coreModule,
                libraryComponent,
                identityModule,
                activitySubModule,
                networkModule,
                ContractSignModule(),
                VerificationPhoneModule(),
                SessionModule(),
                VerificationBankDataModule(),
                VerificationBankModule(),
                ContractUiModule(),
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
            fileControllerProvider =
                DoubleCheck.provider(FileControllerFactory.create(contextProvider))
            fetchPdfUseCaseProvider = create(
                contractSignRepositoryProvider!!, fileControllerProvider
            )
            bankIdPostUseCaseProvider = create(
                verificationBankRepositoryProvider!!, identityInitializationRepositoryProvider!!
            )
            processingVerificationUseCaseProvider = create(
                identificationPollingStatusUseCaseProvider!!,
                bankIdPostUseCaseProvider,
                identityInitializationRepositoryProvider!!
            )
            mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider(
                coreModule!!,
                identityModule!!,
                verficationBankModule,
                contractUiModule,
                authorizeVerificationPhoneUseCaseProvider!!,
                confirmVerificationPhoneUseCaseProvider!!,
                getDocumentsUseCaseProvider!!,
                getIdentificationUseCaseProvider!!,
                fetchingAuthorizedIBanStatusUseCaseProvider!!,
                fetchPdfUseCaseProvider,
                verifyIBanUseCaseProvider!!,
                identificationPollingStatusUseCaseProvider!!,
                bankIdPostUseCaseProvider,
                processingVerificationUseCaseProvider,
                customizationRepositoryProvider!!,
                initializationInfoRepositoryProvider!!,
                authorizeContractSignUseCaseProvider!!,
                confirmContractSignUseCaseProvider!!,
                getMobileNumberUseCaseProvider!!,
                qesStepParametersRepositoryProvider!!
            )
            saveStateViewModelMapProvider = SaveStateViewModelMapProvider(
                getDocumentsUseCaseProvider,
                getIdentificationUseCaseProvider,
                fetchingAuthorizedIBanStatusUseCaseProvider,
                initializationInfoRepositoryProvider,
                identityModule,
                sessionUrlRepositoryProvider,
                verficationBankModule,
                contractUiModule,
                qesStepParametersUseCaseProvider
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

        override fun inject(contractActivity: ContractActivity) {
            injectAssistedViewModelFactory(
                contractActivity,
                assistedViewModelFactoryProvider!!.get()
            )
            injectCustomizationRepository(contractActivity, customizationRepositoryProvider!!.get())
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
            override fun inject(phoneVerificationFragment: PhoneVerificationFragment) {
                PhoneVerificationFragmentInjector(baseFragmentDependencies).injectMembers(
                    phoneVerificationFragment
                )
            }

            override fun inject(successMessageFragment: VerificationPhoneSuccessMessageFragment) {
                VerificationPhoneSuccessMessageFragmentInjector(baseFragmentDependencies).injectMembers(
                    successMessageFragment
                )
            }

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

            override fun inject(contractSigningFragment: ContractSigningFragment) {
                ContractSigningFragmentInjector(baseFragmentDependencies).injectMembers(
                    contractSigningFragment
                )
            }

            override fun inject(contractSigningPreviewFragment: ContractSigningPreviewFragment) {
                ContractSigningPreviewFragmentInjector(baseFragmentDependencies).injectMembers(
                    contractSigningPreviewFragment
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
                        .setIdentityModule(IdentityModule())
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
            contractSignModule,
            sessionModule,
            verificationBankDataModule,
            verificationPhoneModule
        )
    }
}