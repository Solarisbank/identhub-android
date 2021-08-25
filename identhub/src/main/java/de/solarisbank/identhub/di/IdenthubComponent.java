package de.solarisbank.identhub.di;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import de.solarisbank.identhub.contract.ContractActivity;
import de.solarisbank.identhub.contract.ContractActivityInjector;
import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragmentInjector;
import de.solarisbank.identhub.contract.sign.ContractSigningFragment;
import de.solarisbank.identhub.contract.sign.ContractSigningFragmentInjector;
import de.solarisbank.identhub.data.Mapper;
import de.solarisbank.identhub.data.contract.ContractSignApi;
import de.solarisbank.identhub.data.contract.ContractSignLocalDataSource;
import de.solarisbank.identhub.data.contract.ContractSignModule;
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource;
import de.solarisbank.identhub.data.contract.factory.ContractSignLocalDataSourceFactory;
import de.solarisbank.identhub.data.contract.factory.ContractSignNetworkDataSourceFactory;
import de.solarisbank.identhub.data.contract.factory.ProvideContractSignApiFactory;
import de.solarisbank.identhub.data.contract.factory.ProvideContractSignRepositoryFactory;
import de.solarisbank.identhub.data.dao.DocumentDao;
import de.solarisbank.identhub.data.dao.IdentificationDao;
import de.solarisbank.identhub.data.entity.IdentificationWithDocument;
import de.solarisbank.identhub.data.mapper.MapperModule;
import de.solarisbank.identhub.data.mapper.factory.IdentificationEntityMapperFactory;
import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor;
import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferencesFactory;
import de.solarisbank.identhub.data.room.IdentityRoomDatabase;
import de.solarisbank.identhub.data.session.SessionModule;
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource;
import de.solarisbank.identhub.data.session.factory.ProvideSessionUrlRepositoryFactory;
import de.solarisbank.identhub.data.session.factory.SessionUrlLocalDataSourceFactory;
import de.solarisbank.identhub.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.identhub.data.verification.bank.VerificationBankNetworkDataSource;
import de.solarisbank.identhub.data.verification.bank.factory.ProvideVerificationBankApiFactory;
import de.solarisbank.identhub.data.verification.bank.factory.ProvideVerificationBankRepositoryFactory;
import de.solarisbank.identhub.data.verification.bank.factory.VerificationBankLocalDataSourceFactory;
import de.solarisbank.identhub.data.verification.bank.factory.VerificationBankNetworkDataSourceFactory;
import de.solarisbank.identhub.data.verification.phone.VerificationPhoneApi;
import de.solarisbank.identhub.data.verification.phone.VerificationPhoneModule;
import de.solarisbank.identhub.data.verification.phone.VerificationPhoneNetworkDataSource;
import de.solarisbank.identhub.data.verification.phone.factory.ProvideVerificationPhoneApiFactory;
import de.solarisbank.identhub.data.verification.phone.factory.ProvideVerificationPhoneRepositoryFactory;
import de.solarisbank.identhub.data.verification.phone.factory.VerificationPhoneNetworkDataSourceFactory;
import de.solarisbank.identhub.di.database.DatabaseModule;
import de.solarisbank.identhub.di.database.DatabaseModuleProvideDocumentDaoFactory;
import de.solarisbank.identhub.di.database.DatabaseModuleProvideIdentificationDaoFactory;
import de.solarisbank.identhub.di.database.DatabaseModuleProvideRoomFactory;
import de.solarisbank.identhub.di.network.NetworkModule;
import de.solarisbank.identhub.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideHttpLoggingInterceptorFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideMoshiConverterFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideOkHttpClientFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideRetrofitFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideRxJavaCallAdapterFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideUserAgentInterceptorFactory;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCaseFactory;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCaseFactory;
import de.solarisbank.identhub.domain.contract.ContractSignRepository;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCaseFactory;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCaseFactory;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCaseFactory;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCaseFactory;
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase;
import de.solarisbank.identhub.domain.contract.GetPersonDataUseCaseFactory;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository;
import de.solarisbank.identhub.domain.session.IdentityInitializationRepositoryImpl;
import de.solarisbank.identhub.domain.session.IdentityInitializationSharedPrefsDataSource;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCaseFactory;
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase;
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCaseFactory;
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCaseFactory;
import de.solarisbank.identhub.domain.verification.bank.VerificationBankRepository;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCaseFactory;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCaseFactory;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCaseFactory;
import de.solarisbank.identhub.domain.verification.phone.VerificationPhoneRepository;
import de.solarisbank.identhub.file.FileController;
import de.solarisbank.identhub.file.FileControllerFactory;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.identity.IdentityActivityInjector;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;
import de.solarisbank.identhub.progress.ProgressIndicatorFragmentInjector;
import de.solarisbank.identhub.session.data.identification.IdentificationApi;
import de.solarisbank.identhub.session.data.identification.IdentificationApiFactory;
import de.solarisbank.identhub.session.data.identification.IdentificationModule;
import de.solarisbank.identhub.session.data.identification.IdentificationRepository;
import de.solarisbank.identhub.session.data.identification.IdentificationRepositoryFactory;
import de.solarisbank.identhub.session.data.identification.IdentificationRetrofitDataSource;
import de.solarisbank.identhub.session.data.identification.IdentificationRetrofitDataSourceFactory;
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource;
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSourceFactory;
import de.solarisbank.identhub.session.data.mobile.number.MobileNumberApi;
import de.solarisbank.identhub.session.data.mobile.number.MobileNumberApiFactory;
import de.solarisbank.identhub.session.data.mobile.number.MobileNumberDataSource;
import de.solarisbank.identhub.session.data.mobile.number.MobileNumberDataSourceFactory;
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase;
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCaseFactory;
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity;
import de.solarisbank.identhub.verfication.bank.VerificationBankActivityInjector;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragmentInjector;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanFragment;
import de.solarisbank.identhub.verfication.bank.VerificationBankIntroFragment;
import de.solarisbank.identhub.verfication.bank.VerificationBankIntroFragmentInjector;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragmentInjector;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragmentInjector;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragmentInjector;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragmentInjector;
import de.solarisbank.sdk.core.di.CoreActivityComponent;
import de.solarisbank.sdk.core.di.LibraryComponent;
import de.solarisbank.sdk.core.di.internal.DoubleCheck;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Factory2;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class IdenthubComponent {

    private static final Object lock = new Object();
    private static IdenthubComponent identhubComponent = null;

    private final VerificationBankModule verficationBankModule;
    private final ContractModule contractModule;
    private final IdentityModule identityModule;
    private final ActivitySubModule activitySubModule;
    private final NetworkModule networkModule;
    private final DatabaseModule databaseModule;
    private final IdentificationModule identificationModule;

    private Provider<Context> applicationContextProvider;
    private Provider<IdentityRoomDatabase> identityRoomDatabaseProvider;
    private Provider<IdentificationDao> identificationDaoProvider;
    private Provider<DocumentDao> documentDaoProvider;

    private Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider;
    private Provider<CallAdapter.Factory> rxJavaCallAdapterFactoryProvider;
    private Provider<MoshiConverterFactory> moshiConverterFactoryProvider;
    private Provider<HttpLoggingInterceptor> httpLoggingInterceptorProvider;
    private Provider<OkHttpClient> okHttpClientProvider;
    private Provider<UserAgentInterceptor> userAgentInterceptorProvider;
    private Provider<Retrofit> retrofitProvider;

    private Provider<ContractSignApi> contractSignApiProvider;
    private Provider<ContractSignNetworkDataSource> contractSignNetworkDataSourceProvider;
    private Provider<ContractSignRepository> contractSignRepositoryProvider;
    private Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider;
    private Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider;
    private Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider;

    private Provider<VerificationPhoneApi> verificationPhoneApiProvider;
    private Provider<VerificationPhoneNetworkDataSource> verificationPhoneNetworkDataSourceProvider;
    private Provider<VerificationBankLocalDataSource> verificationBankLocalDataSourceProvider;
    private Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider;
    private Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider;
    private Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider;

    private Provider<SessionUrlLocalDataSource> sessionUrlLocalDataSourceProvider;
    private Provider<SessionUrlRepository> sessionUrlRepositoryProvider;

    private Provider<VerificationBankApi> verificationBankApiProvider;
    private Provider<VerificationBankNetworkDataSource> verificationBankNetworkDataSourceProvider;
    private Provider<VerificationBankRepository> verificationBankRepositoryProvider;
    private Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider;
    private Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider;
    private Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private Provider<ContractSignLocalDataSource> contractSignLocalDataSourceProvider;
    private Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationEntityMapperProvider;

    private Provider<IdentificationApi> identificationApiProvider;
    private Provider<IdentificationRetrofitDataSource> identificationRetrofitDataSourceProvider;
    private Provider<MobileNumberApi> mobileNumberApiProvider;
    private Provider<MobileNumberDataSource> mobileNumberDataSourceProvider;
    private Provider<IdentificationRoomDataSource> identificationRoomDataSourceProvider;
    private Provider<IdentificationRepository> identificationRepositoryProvider;
    private Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider;
    private Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider;
    private Provider<SharedPreferences> sharedPreferencesProvider;
    private Provider<IdentityInitializationSharedPrefsDataSource> identityInitializationSharedPrefsDataSourceProvider;
    private Provider<IdentityInitializationRepository> identityInitializationRepositoryProvider;


    private IdenthubComponent(
            LibraryComponent libraryComponent,
            IdentityModule identityModule,
            ActivitySubModule activitySubModule,
            NetworkModule networkModule,
            DatabaseModule databaseModule,
            ContractSignModule contractSignModule,
            MapperModule mapperModule,
            VerificationPhoneModule verificationPhoneModule,
            SessionModule sessionModule,
            VerificationBankDataModule verificationBankDataModule,
            VerificationBankModule verficationBankModule,
            ContractModule contractModule,
            IdentificationModule identificationModule) {
        this.identityModule = identityModule;
        this.activitySubModule = activitySubModule;
        this.networkModule = networkModule;
        this.databaseModule = databaseModule;
        this.verficationBankModule = verficationBankModule;
        this.contractModule = contractModule;
        this.identificationModule = identificationModule;
        initializeMapper(mapperModule);
        initialize(libraryComponent, contractSignModule, sessionModule, verificationBankDataModule, verificationPhoneModule);
    }

    @NotNull
    public static IdenthubComponent getInstance(LibraryComponent libraryComponent) {
        synchronized (lock) {
            if (identhubComponent == null) {
                identhubComponent = new IdenthubComponent.Builder()
                        .setLibraryComponent(libraryComponent)
                        .setIdentityModule(new IdentityModule())
                        .setActivitySubModule(new ActivitySubModule())
                        .setNetworkModule(new NetworkModule())
                        .setDatabaseModule(new DatabaseModule())
                        .build();
            }
            return identhubComponent;
        }
    }

    private void initializeMapper(MapperModule mapperModule) {
        identificationEntityMapperProvider = DoubleCheck.provider(IdentificationEntityMapperFactory.create(mapperModule));
    }

    private void initialize(LibraryComponent libraryComponent, ContractSignModule contractSignModule, SessionModule sessionModule, VerificationBankDataModule verificationBankDataModule, VerificationPhoneModule verificationPhoneModule) {
        applicationContextProvider = new ApplicationContextProvider(libraryComponent);

        identityRoomDatabaseProvider = DoubleCheck.provider(DatabaseModuleProvideRoomFactory.create(databaseModule, applicationContextProvider));
        identificationDaoProvider = DoubleCheck.provider(DatabaseModuleProvideIdentificationDaoFactory.create(databaseModule, identityRoomDatabaseProvider));
        documentDaoProvider = DoubleCheck.provider(DatabaseModuleProvideDocumentDaoFactory.create(databaseModule, identityRoomDatabaseProvider));

        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(SessionUrlLocalDataSourceFactory.create(sessionModule));
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider));
        sharedPreferencesProvider = DoubleCheck.provider((Factory<SharedPreferences>) () -> applicationContextProvider.get().getSharedPreferences("identhub", Context.MODE_PRIVATE));
        identityInitializationSharedPrefsDataSourceProvider = DoubleCheck.provider((Factory<IdentityInitializationSharedPrefsDataSource>) () -> new IdentityInitializationSharedPrefsDataSource(sharedPreferencesProvider.get()));
        identityInitializationRepositoryProvider = DoubleCheck.provider((Factory<IdentityInitializationRepository>) () -> new IdentityInitializationRepositoryImpl(identityInitializationSharedPrefsDataSourceProvider.get()));

        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideDynamicUrlInterceptorFactory.create(networkModule, sessionUrlRepositoryProvider));
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule));
        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule));
        userAgentInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideUserAgentInterceptorFactory.create(networkModule));
        httpLoggingInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule));
        verificationPhoneApiProvider = DoubleCheck.provider(ProvideVerificationPhoneApiFactory.create(verificationPhoneModule, retrofitProvider));
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(networkModule, dynamicBaseUrlInterceptorProvider, userAgentInterceptorProvider, httpLoggingInterceptorProvider));
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(networkModule, moshiConverterFactoryProvider, okHttpClientProvider, rxJavaCallAdapterFactoryProvider));
        verificationPhoneNetworkDataSourceProvider = VerificationPhoneNetworkDataSourceFactory.create(verificationPhoneModule, verificationPhoneApiProvider);
        verificationPhoneRepositoryProvider = DoubleCheck.provider(ProvideVerificationPhoneRepositoryFactory.create(verificationPhoneModule, verificationPhoneNetworkDataSourceProvider));

        contractSignApiProvider = DoubleCheck.provider(ProvideContractSignApiFactory.create(contractSignModule, retrofitProvider));
        contractSignNetworkDataSourceProvider = ContractSignNetworkDataSourceFactory.create(contractSignModule, contractSignApiProvider);
        contractSignLocalDataSourceProvider = ContractSignLocalDataSourceFactory.create(contractSignModule, documentDaoProvider, identificationDaoProvider);
        contractSignRepositoryProvider = DoubleCheck.provider(ProvideContractSignRepositoryFactory.create(contractSignModule, contractSignNetworkDataSourceProvider, contractSignLocalDataSourceProvider, identificationEntityMapperProvider));

        verificationBankApiProvider = DoubleCheck.provider(ProvideVerificationBankApiFactory.create(verificationBankDataModule, retrofitProvider));
        verificationBankNetworkDataSourceProvider = VerificationBankNetworkDataSourceFactory.create(verificationBankDataModule, verificationBankApiProvider);
        verificationBankLocalDataSourceProvider = VerificationBankLocalDataSourceFactory.create(documentDaoProvider, identificationDaoProvider, verificationBankDataModule);
        verificationBankRepositoryProvider = DoubleCheck.provider(ProvideVerificationBankRepositoryFactory.create(identificationEntityMapperProvider, verificationBankDataModule, verificationBankNetworkDataSourceProvider, verificationBankLocalDataSourceProvider));

        authorizeContractSignUseCaseProvider = AuthorizeContractSignUseCaseFactory.create(contractSignRepositoryProvider);
        confirmContractSignUseCaseProvider = ConfirmContractSignUseCaseFactory.create(contractSignRepositoryProvider);
        deleteAllLocalStorageUseCaseProvider = DeleteAllLocalStorageUseCaseFactory.create(contractSignRepositoryProvider);

        authorizeVerificationPhoneUseCaseProvider = AuthorizeVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider);
        confirmVerificationPhoneUseCaseProvider = ConfirmVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider);
        fetchingAuthorizedIBanStatusUseCaseProvider = FetchingAuthorizedIBanStatusUseCaseFactory.create(identificationEntityMapperProvider, verificationBankRepositoryProvider);
        getDocumentsUseCaseProvider = GetDocumentsUseCaseFactory.create(contractSignRepositoryProvider);
        getIdentificationUseCaseProvider = GetIdentificationUseCaseFactory.create(contractSignRepositoryProvider, identityInitializationRepositoryProvider);
        verifyIBanUseCaseProvider = VerifyIBanUseCaseFactory.create(getIdentificationUseCaseProvider, verificationBankRepositoryProvider, identityInitializationRepositoryProvider);
    }


    public IdentHubActivitySubcomponent.Factory activitySubcomponent() {
        return new ActivitySubcomponentFactory();
    }

    private static class Builder {
        private IdentityModule identityModule;
        private LibraryComponent libraryComponent;
        private ActivitySubModule activitySubModule;
        private NetworkModule networkModule;
        private DatabaseModule databaseModule;

        private Builder() {
        }

        Builder setIdentityModule(IdentityModule identityModule) {
            this.identityModule = identityModule;
            return this;
        }

        Builder setLibraryComponent(LibraryComponent libraryComponent) {
            this.libraryComponent = libraryComponent;
            return this;
        }

        Builder setActivitySubModule(ActivitySubModule activitySubModule) {
            this.activitySubModule = activitySubModule;
            return this;
        }

        Builder setNetworkModule(NetworkModule networkModule) {
            this.networkModule = networkModule;
            return this;
        }

        public Builder setDatabaseModule(DatabaseModule databaseModule) {
            this.databaseModule = databaseModule;
            return this;
        }

        public IdenthubComponent build() {
            return new IdenthubComponent(
                    libraryComponent,
                    identityModule,
                    activitySubModule,
                    networkModule,
                    databaseModule,
                    new ContractSignModule(),
                    new MapperModule(),
                    new VerificationPhoneModule(),
                    new SessionModule(),
                    new VerificationBankDataModule(),
                    new VerificationBankModule(),
                    new ContractModule(),
                    new IdentificationModule()
            );
        }
    }

    static class ApplicationContextProvider implements Provider<Context> {

        private final LibraryComponent libraryComponent;

        ApplicationContextProvider(LibraryComponent libraryComponent) {
            this.libraryComponent = libraryComponent;
        }

        @Override
        public Context get() {
            return libraryComponent.applicationContext();
        }
    }

    public class ActivitySubcomponentFactory implements IdentHubActivitySubcomponent.Factory {

        @NotNull
        @Override
        public IdentHubActivitySubcomponent create(@NotNull CoreActivityComponent activityComponent) {
            return new IdentHubActivitySubcomponentImp(activityComponent, activitySubModule);
        }
    }

    private final class IdentHubActivitySubcomponentImp implements IdentHubActivitySubcomponent {

        private Provider<Context> contextProvider;
        private Provider<SharedPreferences> sharedPreferencesProvider;
        private Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
        private Provider<AssistedViewModelFactory> assistedViewModelFactoryProvider;
        private Provider<FileController> fileControllerProvider;
        private Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
        private Provider<BankIdPostUseCase> bankIdPostUseCaseProvider;
        Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider;

        private Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider;
        private Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> saveStateViewModelMapProvider;

        public IdentHubActivitySubcomponentImp(CoreActivityComponent activityComponent, ActivitySubModule activitySubModule) {
            initialize(activityComponent, activitySubModule);
        }

        private void initialize(CoreActivityComponent activityComponent, ActivitySubModule identHubModule) {
            this.contextProvider = new ContextProvider(activityComponent);
            this.sharedPreferencesProvider = new SharedPreferencesProvider(activityComponent);
            this.identificationStepPreferencesProvider = DoubleCheck.provider(IdentificationStepPreferencesFactory.create(identHubModule, sharedPreferencesProvider));

            this.fileControllerProvider = DoubleCheck.provider(FileControllerFactory.create(contextProvider));
            this.fetchPdfUseCaseProvider = FetchPdfUseCaseFactory.create(contractSignRepositoryProvider, fileControllerProvider);

            identificationApiProvider = DoubleCheck.provider(IdentificationApiFactory.create(identificationModule, retrofitProvider.get()));
            identificationRetrofitDataSourceProvider = DoubleCheck.provider(IdentificationRetrofitDataSourceFactory.create(identificationModule, identificationApiProvider.get()));
            identificationRoomDataSourceProvider = DoubleCheck.provider(IdentificationRoomDataSourceFactory.create(identificationModule, identificationDaoProvider.get()));
            mobileNumberApiProvider = MobileNumberApiFactory.create(retrofitProvider.get());
            mobileNumberDataSourceProvider = MobileNumberDataSourceFactory.create(mobileNumberApiProvider.get());
            identificationRepositoryProvider = DoubleCheck.provider(IdentificationRepositoryFactory.create(
                    identificationRoomDataSourceProvider.get(),
                    identificationRetrofitDataSourceProvider.get(),
                    mobileNumberDataSourceProvider.get()
            ));
            getMobileNumberUseCaseProvider = GetPersonDataUseCaseFactory.create(identificationRepositoryProvider);
            identificationPollingStatusUseCaseProvider = DoubleCheck.provider(IdentificationPollingStatusUseCaseFactory.create(identificationRepositoryProvider.get(), identityInitializationRepositoryProvider.get()));
            bankIdPostUseCaseProvider = BankIdPostUseCaseFactory.Companion.create(verificationBankRepositoryProvider, identityInitializationRepositoryProvider);
            processingVerificationUseCaseProvider = ProcessingVerificationUseCaseFactory.Companion.create(identificationPollingStatusUseCaseProvider, bankIdPostUseCaseProvider, identityInitializationRepositoryProvider);
            this.mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider.create(
                    identityModule,
                    verficationBankModule,
                    contractModule,
                    IdenthubComponent.this.authorizeVerificationPhoneUseCaseProvider,
                    IdenthubComponent.this.confirmVerificationPhoneUseCaseProvider,
                    IdenthubComponent.this.getDocumentsUseCaseProvider,
                    IdenthubComponent.this.getIdentificationUseCaseProvider,
                    IdenthubComponent.this.fetchingAuthorizedIBanStatusUseCaseProvider,
                    fetchPdfUseCaseProvider,
                    identificationStepPreferencesProvider,
                    IdenthubComponent.this.verifyIBanUseCaseProvider,
                    identificationPollingStatusUseCaseProvider,
                    bankIdPostUseCaseProvider,
                    processingVerificationUseCaseProvider
            );
            this.saveStateViewModelMapProvider = SaveStateViewModelMapProvider.create(
                    IdenthubComponent.this.authorizeContractSignUseCaseProvider,
                    IdenthubComponent.this.confirmContractSignUseCaseProvider,
                    deleteAllLocalStorageUseCaseProvider,
                    fetchPdfUseCaseProvider,
                    IdenthubComponent.this.getDocumentsUseCaseProvider,
                    IdenthubComponent.this.getIdentificationUseCaseProvider,
                    IdenthubComponent.this.identificationPollingStatusUseCaseProvider,
                    IdenthubComponent.this.fetchingAuthorizedIBanStatusUseCaseProvider,
                    IdenthubComponent.this.identityModule,
                    identificationStepPreferencesProvider,
                    IdenthubComponent.this.sessionUrlRepositoryProvider,
                    IdenthubComponent.this.verficationBankModule,
                    IdenthubComponent.this.contractModule,
                    getMobileNumberUseCaseProvider
            );
            this.assistedViewModelFactoryProvider = DoubleCheck.provider(ActivitySubModuleAssistedViewModelFactory.create(IdenthubComponent.this.activitySubModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider));



        }

        @Override
        public void inject(@NotNull VerificationBankActivity verificationBankActivity) {
            VerificationBankActivityInjector.injectAssistedViewModelFactory(verificationBankActivity, assistedViewModelFactoryProvider.get());
        }

        @Override
        public void inject(@NotNull ContractActivity contractActivity) {
            ContractActivityInjector.injectAssistedViewModelFactory(contractActivity, assistedViewModelFactoryProvider.get());
        }

        @Override
        public void inject(IdentityActivity identityActivity) {
            IdentityActivityInjector.injectAssistedViewModelFactory(identityActivity, assistedViewModelFactoryProvider.get());
        }

        @NotNull
        @Override
        public FragmentComponent.Factory fragmentComponent() {
            return new FragmentComponentFactory();
        }

        class ContextProvider implements Provider<Context> {

            private final CoreActivityComponent activityComponent;

            ContextProvider(CoreActivityComponent libraryComponent) {
                this.activityComponent = libraryComponent;
            }

            @Override
            public Context get() {
                return activityComponent.context();
            }
        }

        class SharedPreferencesProvider implements Provider<SharedPreferences> {

            private final CoreActivityComponent activityComponent;

            SharedPreferencesProvider(CoreActivityComponent libraryComponent) {
                this.activityComponent = libraryComponent;
            }

            @Override
            public SharedPreferences get() {
                return activityComponent.sharedPreferences();
            }
        }

        class FragmentComponentFactory implements FragmentComponent.Factory {

            @NotNull
            @Override
            public FragmentComponent create() {
                return new FragmentComponentImp();
            }
        }

        private final class FragmentComponentImp implements FragmentComponent {

            private final Provider<AssistedViewModelFactory> fragmentAssistedViewModelFactoryProvider;

            private FragmentComponentImp() {
                this.fragmentAssistedViewModelFactoryProvider = DoubleCheck.provider(ActivitySubModuleAssistedViewModelFactory.create(IdenthubComponent.this.activitySubModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider));
            }

            @Override
            public void inject(VerificationPhoneFragment verificationPhoneFragment) {
                VerificationPhoneFragmentInjector.injectAssistedViewModelFactory(verificationPhoneFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationPhoneSuccessMessageFragment successMessageFragment) {
                VerificationPhoneSuccessMessageFragmentInjector.injectAssistedViewModelFactory(successMessageFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationPhoneErrorMessageFragment errorMessageFragment) {
                VerificationPhoneErrorMessageFragmentInjector.injectAssistedViewModelFactory(errorMessageFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankIbanFragment verificationBankIbanFragment) {
                VerificationBankFragmentInjector.injectAssistedViewModelFactory(verificationBankIbanFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankExternalGatewayFragment verificationBankExternalGatewayFragment) {
                VerificationBankExternalGatewayFragmentInjector.injectAssistedViewModelFactory(verificationBankExternalGatewayFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(ContractSigningFragment contractSigningFragment) {
                ContractSigningFragmentInjector.injectAssistedViewModelFactory(contractSigningFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(ContractSigningPreviewFragment contractSigningPreviewFragment) {
                ContractSigningPreviewFragmentInjector.injectAssistedViewModelFactory(contractSigningPreviewFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(ProgressIndicatorFragment progressIndicatorFragment) {
                ProgressIndicatorFragmentInjector.injectAssistedViewModelFactory(progressIndicatorFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankIntroFragment verificationBankIntroFragment) {
                VerificationBankIntroFragmentInjector.injectAssistedViewModelFactory(verificationBankIntroFragment, fragmentAssistedViewModelFactoryProvider.get());
            }
        }
    }
}
