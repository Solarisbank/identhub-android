package de.solarisbank.identhub.di;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import de.solarisbank.identhub.AssistedViewModelFactory;
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
import de.solarisbank.identhub.data.dto.IdentificationDto;
import de.solarisbank.identhub.data.entity.IdentificationWithDocument;
import de.solarisbank.identhub.data.mapper.MapperModule;
import de.solarisbank.identhub.data.mapper.factory.IdentificationEntityMapperFactory;
import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferencesFactory;
import de.solarisbank.identhub.data.room.IdentityRoomDatabase;
import de.solarisbank.identhub.data.session.SessionModule;
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource;
import de.solarisbank.identhub.data.session.factory.ProvideSessionUrlRepositoryFactory;
import de.solarisbank.identhub.data.session.factory.SessionUrlLocalDataSourceFactory;
import de.solarisbank.identhub.data.verification.bank.VerificationBankApi;
import de.solarisbank.identhub.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.identhub.data.verification.bank.VerificationBankModule;
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
import de.solarisbank.identhub.di.internal.DoubleCheck;
import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.di.network.NetworkModule;
import de.solarisbank.identhub.di.network.NetworkModuleProvideDynamicUrlInterceptorFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideMoshiConverterFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideOkHttpClientFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideRetrofitFactory;
import de.solarisbank.identhub.di.network.NetworkModuleProvideRxJavaCallAdapterFactory;
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
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCaseFactory;
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
import de.solarisbank.identhub.fourthline.FourthlineModule;
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsFragment;
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsInjector;
import de.solarisbank.identhub.identity.IdentityActivity;
import de.solarisbank.identhub.identity.IdentityActivityInjector;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivityInjector;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragment;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragmentInjector;
import de.solarisbank.identhub.intro.IntroActivity;
import de.solarisbank.identhub.intro.IntroActivityInjector;
import de.solarisbank.identhub.intro.IntroFragment;
import de.solarisbank.identhub.intro.IntroFragmentInjector;
import de.solarisbank.identhub.intro.IntroModule;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;
import de.solarisbank.identhub.progress.ProgressIndicatorFragmentInjector;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragment;
import de.solarisbank.identhub.verfication.bank.VerificationBankFragmentInjector;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragment;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragmentInjector;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragmentInjector;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragment;
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragmentInjector;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragmentInjector;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragmentInjector;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragmentInjector;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class LibraryComponent {

    private static final Object lock = new Object();
    private static LibraryComponent libraryComponent = null;

    private final IdentityModule identityModule;
    private final IntroModule introModule;
    private final LibraryModule libraryModule;
    private final NetworkModule networkModule;
    private final DatabaseModule databaseModule;
    private final FourthlineModule fourthlineModule;

    private Provider<Context> applicationContextProvider;
    private Provider<IdentityRoomDatabase> identityRoomDatabaseProvider;
    private Provider<IdentificationDao> identificationDaoProvider;
    private Provider<DocumentDao> documentDaoProvider;

    private Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptorProvider;
    private Provider<CallAdapter.Factory> rxJavaCallAdapterFactoryProvider;
    private Provider<MoshiConverterFactory> moshiConverterFactoryProvider;
    private Provider<OkHttpClient> okHttpClientProvider;
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

    private LibraryComponent(
            IdentityModule identityModule,
            IntroModule introModule,
            FourthlineModule fourthlineModule,
            LibraryModule libraryModule,
            NetworkModule networkModule,
            DatabaseModule databaseModule,
            ContractSignModule contractSignModule,
            MapperModule mapperModule,
            VerificationPhoneModule verificationPhoneModule,
            SessionModule sessionModule,
            VerificationBankModule verificationBankModule) {
        this.identityModule = identityModule;
        this.introModule = introModule;
        this.libraryModule = libraryModule;
        this.fourthlineModule = fourthlineModule;
        this.networkModule = networkModule;
        this.databaseModule = databaseModule;
        initializeMapper(mapperModule);
        initialize(contractSignModule, sessionModule, verificationBankModule, verificationPhoneModule);
    }

    @NotNull
    public static LibraryComponent getInstance(@NotNull Application application) {
        synchronized (lock) {
            if (libraryComponent == null) {
                libraryComponent = new LibraryComponent.Builder()
                        .setIdentityModule(new IdentityModule())
                        .setIntroModule(new IntroModule())
                        .setLibraryModule(new LibraryModule(application))
                        .setNetworkModule(new NetworkModule())
                        .setDatabaseModule(new DatabaseModule())
                        .build();
            }
            return libraryComponent;
        }
    }

    private void initializeMapper(MapperModule mapperModule) {
        identificationEntityMapperProvider = DoubleCheck.provider(IdentificationEntityMapperFactory.create(mapperModule));
    }

    private void initialize(ContractSignModule contractSignModule, SessionModule sessionModule, VerificationBankModule verificationBankModule, VerificationPhoneModule verificationPhoneModule) {
        applicationContextProvider = DoubleCheck.provider(LibraryModuleContextFactory.create(libraryModule));

        identityRoomDatabaseProvider = DoubleCheck.provider(DatabaseModuleProvideRoomFactory.create(databaseModule, applicationContextProvider));
        identificationDaoProvider = DoubleCheck.provider(DatabaseModuleProvideIdentificationDaoFactory.create(databaseModule, identityRoomDatabaseProvider));
        documentDaoProvider = DoubleCheck.provider(DatabaseModuleProvideDocumentDaoFactory.create(databaseModule, identityRoomDatabaseProvider));

        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(SessionUrlLocalDataSourceFactory.create(sessionModule));
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider));

        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideDynamicUrlInterceptorFactory.create(networkModule, sessionUrlRepositoryProvider));
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule));
        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule));
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(networkModule, dynamicBaseUrlInterceptorProvider));
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(networkModule, moshiConverterFactoryProvider, okHttpClientProvider, rxJavaCallAdapterFactoryProvider));
        verificationPhoneApiProvider = DoubleCheck.provider(ProvideVerificationPhoneApiFactory.create(verificationPhoneModule, retrofitProvider));
        verificationPhoneNetworkDataSourceProvider = VerificationPhoneNetworkDataSourceFactory.create(verificationPhoneModule, verificationPhoneApiProvider);
        verificationPhoneRepositoryProvider = DoubleCheck.provider(ProvideVerificationPhoneRepositoryFactory.create(verificationPhoneModule, verificationPhoneNetworkDataSourceProvider));

        contractSignApiProvider = DoubleCheck.provider(ProvideContractSignApiFactory.create(contractSignModule, retrofitProvider));
        contractSignNetworkDataSourceProvider = ContractSignNetworkDataSourceFactory.create(contractSignModule, contractSignApiProvider);
        contractSignLocalDataSourceProvider = ContractSignLocalDataSourceFactory.create(contractSignModule, documentDaoProvider, identificationDaoProvider);
        contractSignRepositoryProvider = DoubleCheck.provider(ProvideContractSignRepositoryFactory.create(contractSignModule, contractSignNetworkDataSourceProvider, contractSignLocalDataSourceProvider, identificationEntityMapperProvider));

        verificationBankApiProvider = DoubleCheck.provider(ProvideVerificationBankApiFactory.create(verificationBankModule, retrofitProvider));
        verificationBankNetworkDataSourceProvider = VerificationBankNetworkDataSourceFactory.create(verificationBankModule, verificationBankApiProvider);
        verificationBankLocalDataSourceProvider = VerificationBankLocalDataSourceFactory.create(documentDaoProvider, identificationDaoProvider, verificationBankModule);
        verificationBankRepositoryProvider = DoubleCheck.provider(ProvideVerificationBankRepositoryFactory.create(identificationEntityMapperProvider, verificationBankModule, verificationBankNetworkDataSourceProvider, verificationBankLocalDataSourceProvider));

        authorizeContractSignUseCaseProvider = AuthorizeContractSignUseCaseFactory.create(contractSignRepositoryProvider);
        confirmContractSignUseCaseProvider = ConfirmContractSignUseCaseFactory.create(contractSignRepositoryProvider);
        deleteAllLocalStorageUseCaseProvider = DeleteAllLocalStorageUseCaseFactory.create(contractSignRepositoryProvider);

        authorizeVerificationPhoneUseCaseProvider = AuthorizeVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider);
        confirmVerificationPhoneUseCaseProvider = ConfirmVerificationPhoneUseCaseFactory.create(verificationPhoneRepositoryProvider);
        fetchingAuthorizedIBanStatusUseCaseProvider = FetchingAuthorizedIBanStatusUseCaseFactory.create(identificationEntityMapperProvider, verificationBankRepositoryProvider);
        getDocumentsUseCaseProvider = GetDocumentsUseCaseFactory.create(contractSignRepositoryProvider);
        getIdentificationUseCaseProvider = GetIdentificationUseCaseFactory.create(contractSignRepositoryProvider);
        verifyIBanUseCaseProvider = VerifyIBanUseCaseFactory.create(getIdentificationUseCaseProvider, verificationBankRepositoryProvider);
    }

    public ActivityComponent plus(ActivityModule activityModule) {
        return new ActivityComponentImp(activityModule);
    }

    private static class Builder {
        private IdentityModule identityModule;
        private IntroModule introModule;
        private LibraryModule libraryModule;
        private NetworkModule networkModule;
        private DatabaseModule databaseModule;

        private Builder() {
        }

        Builder setIdentityModule(IdentityModule identityModule) {
            this.identityModule = identityModule;
            return this;
        }

        Builder setIntroModule(IntroModule introModule) {
            this.introModule = introModule;
            return this;
        }

        Builder setLibraryModule(LibraryModule libraryModule) {
            this.libraryModule = libraryModule;
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

        public LibraryComponent build() {
            return new LibraryComponent(
                    identityModule,
                    introModule,
                    new FourthlineModule(),
                    libraryModule,
                    networkModule,
                    databaseModule,
                    new ContractSignModule(),
                    new MapperModule(),
                    new VerificationPhoneModule(),
                    new SessionModule(),
                    new VerificationBankModule());
        }
    }

    private final class ActivityComponentImp implements ActivityComponent {

        private Provider<Context> contextProvider;
        private Provider<SharedPreferences> sharedPreferencesProvider;
        private Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
        private Provider<AssistedViewModelFactory> assistedViewModelFactoryProvider;
        private Provider<FileController> fileControllerProvider;
        private Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;

        private Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> mapOfClassOfAndProviderOfViewModelProvider;
        private Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> saveStateViewModelMapProvider;

        public ActivityComponentImp(ActivityModule activityModule) {
            initialize(activityModule);
        }

        private void initialize(ActivityModule activityModule) {
            this.contextProvider = DoubleCheck.provider(ActivityModuleContextFactory.create(activityModule));
            this.sharedPreferencesProvider = DoubleCheck.provider(SharedPreferencesFactory.create(activityModule, contextProvider));
            this.identificationStepPreferencesProvider = DoubleCheck.provider(IdentificationStepPreferencesFactory.create(activityModule, sharedPreferencesProvider));

            this.fileControllerProvider = DoubleCheck.provider(FileControllerFactory.create(contextProvider));
            this.fetchPdfUseCaseProvider = FetchPdfUseCaseFactory.create(contractSignRepositoryProvider, fileControllerProvider);
            this.mapOfClassOfAndProviderOfViewModelProvider = ViewModelMapProvider.create(identityModule,
                    LibraryComponent.this.authorizeVerificationPhoneUseCaseProvider,
                    LibraryComponent.this.confirmVerificationPhoneUseCaseProvider,
                    LibraryComponent.this.getDocumentsUseCaseProvider,
                    LibraryComponent.this.getIdentificationUseCaseProvider,
                    fetchPdfUseCaseProvider,
                    identificationStepPreferencesProvider,
                    LibraryComponent.this.verifyIBanUseCaseProvider
            );
            this.saveStateViewModelMapProvider = SaveStateViewModelMapProvider.create(
                    LibraryComponent.this.authorizeContractSignUseCaseProvider,
                    LibraryComponent.this.confirmContractSignUseCaseProvider,
                    deleteAllLocalStorageUseCaseProvider,
                    fetchPdfUseCaseProvider,
                    LibraryComponent.this.getDocumentsUseCaseProvider,
                    LibraryComponent.this.getIdentificationUseCaseProvider,
                    LibraryComponent.this.fetchingAuthorizedIBanStatusUseCaseProvider,
                    LibraryComponent.this.identityModule,
                    identificationStepPreferencesProvider,
                    LibraryComponent.this.sessionUrlRepositoryProvider,
                    LibraryComponent.this.introModule,
                    LibraryComponent.this.fourthlineModule
            );
            this.assistedViewModelFactoryProvider = DoubleCheck.provider(LibraryModuleAssistedViewModelFactory.create(LibraryComponent.this.libraryModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider));
        }

        @Override
        public void inject(IntroActivity introActivity) {
            IntroActivityInjector.injectAssistedViewModelFactory(introActivity, assistedViewModelFactoryProvider.get());
        }

        @Override
        public void inject(IdentityActivity identityActivity) {
            IdentityActivityInjector.injectAssistedViewModelFactory(identityActivity, assistedViewModelFactoryProvider.get());
        }

        @Override
        public void inject(IdentitySummaryActivity identitySummaryActivity) {
            IdentitySummaryActivityInjector.injectAssistedViewModelFactory(identitySummaryActivity, assistedViewModelFactoryProvider.get());
        }

        @Override
        public FragmentComponent plus() {
            return new FragmentComponentImp();
        }

        private final class FragmentComponentImp implements FragmentComponent {

            private final Provider<AssistedViewModelFactory> fragmentAssistedViewModelFactoryProvider;

            private FragmentComponentImp() {
                this.fragmentAssistedViewModelFactoryProvider = DoubleCheck.provider(LibraryModuleAssistedViewModelFactory.create(LibraryComponent.this.libraryModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider));
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
            public void inject(VerificationBankFragment verificationBankFragment) {
                VerificationBankFragmentInjector.injectAssistedViewModelFactory(verificationBankFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankExternalGatewayFragment verificationBankExternalGatewayFragment) {
                VerificationBankExternalGatewayFragmentInjector.injectAssistedViewModelFactory(verificationBankExternalGatewayFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankSuccessMessageFragment verificationBankSuccessMessageFragment) {
                VerificationBankSuccessMessageFragmentInjector.injectAssistedViewModelFactory(verificationBankSuccessMessageFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(VerificationBankErrorMessageFragment verificationBankErrorMessageFragment) {
                VerificationBankErrorMessageFragmentInjector.injectAssistedViewModelFactory(verificationBankErrorMessageFragment, fragmentAssistedViewModelFactoryProvider.get());
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
            public void inject(IdentitySummaryFragment identitySummaryFragment) {
                IdentitySummaryFragmentInjector.injectAssistedViewModelFactory(identitySummaryFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(ProgressIndicatorFragment progressIndicatorFragment) {
                ProgressIndicatorFragmentInjector.injectAssistedViewModelFactory(progressIndicatorFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(IntroFragment introFragment) {
                IntroFragmentInjector.injectAssistedViewModelFactory(introFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

            @Override
            public void inject(TermsAndConditionsFragment termsAndConditionsFragment) {
                TermsAndConditionsInjector.injectAssistedViewModelFactory(termsAndConditionsFragment, fragmentAssistedViewModelFactoryProvider.get());
            }

        }
    }
}
