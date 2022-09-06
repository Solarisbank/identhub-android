package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModelFactory;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModelFactory;
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource;
import de.solarisbank.sdk.data.repository.SessionUrlRepository;
import de.solarisbank.sdk.feature.config.InitializationInfoRepository;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;

@RestrictTo(RestrictTo.Scope.LIBRARY)
final class SaveStateViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {

    private final IdentityModule identityModule;
    private final VerificationBankModule verificationBankModule;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<SessionUrlRepository> sessionUrlRepositoryProvider;
    private final Provider<InitializationInfoRepository> initializationInfoRepositoryProvider;
    private final Provider<QesStepParametersUseCase> qesStepParametersUseCaseProvider;
    private final Provider<IdentificationLocalDataSource> identificationLocalDataSourceProvider;

    public SaveStateViewModelMapProvider(
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            Provider<InitializationInfoRepository> initializationInfoRepositoryProvider,
            IdentityModule identityModule,
            Provider<SessionUrlRepository> sessionUrlRepositoryProvider,
            VerificationBankModule verificationBankModule,
            Provider<QesStepParametersUseCase> qesStepParametersUseCaseProvider,
            Provider<IdentificationLocalDataSource> identificationLocalDataSourceProvider

    ) {
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.identityModule = identityModule;
        this.sessionUrlRepositoryProvider = sessionUrlRepositoryProvider;
        this.verificationBankModule = verificationBankModule;
        this.initializationInfoRepositoryProvider = initializationInfoRepositoryProvider;
        this.qesStepParametersUseCaseProvider = qesStepParametersUseCaseProvider;
        this.identificationLocalDataSourceProvider = identificationLocalDataSourceProvider;
    }

    @Override
    public Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> get() {
        Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> map = new LinkedHashMap<>(3);

        map.put(VerificationBankExternalGateViewModel.class, VerificationBankExternalGateViewModelFactory.create(
                verificationBankModule,
                fetchingAuthorizedIBanStatusUseCaseProvider,
                identificationLocalDataSourceProvider
        ));

        map.put(VerificationBankViewModel.class, new VerificationBankViewModelFactory(
                verificationBankModule,
                sessionUrlRepositoryProvider,
                initializationInfoRepositoryProvider
        ));

        return map;
    }
}
