package de.solarisbank.identhub.identity.summary;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory2;
import de.solarisbank.sdk.core.di.internal.Provider;

public class IdentitySummaryFragmentViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final IdentityModule identityModule;

    private final Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;

    public IdentitySummaryFragmentViewModelFactory(IdentityModule identityModule,
                                                   Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider,
                                                   Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
                                                   Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider,
                                                   Provider<IdentificationStepPreferences> identificationStepPreferencesProvider) {
        this.identityModule = identityModule;
        this.getDocumentsUseCaseProvider = getDocumentsUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
        this.deleteAllLocalStorageUseCaseProvider = deleteAllLocalStorageUseCaseProvider;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
    }

    public static IdentitySummaryFragmentViewModelFactory create(
            IdentityModule identityModule,
            Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider
    ) {
        return new IdentitySummaryFragmentViewModelFactory(identityModule, deleteAllLocalStorageUseCaseProvider, fetchPdfUseCaseProvider, getDocumentsUseCaseProvider, identificationStepPreferencesProvider);
    }

    @Override
    public ViewModel create(SavedStateHandle savedStateHandle) {
        return identityModule.provideIdentitySummaryFragmentViewModel(
                deleteAllLocalStorageUseCaseProvider.get(),
                getDocumentsUseCaseProvider.get(),
                fetchPdfUseCaseProvider.get(),
                identificationStepPreferencesProvider.get(),
                savedStateHandle
        );
    }
}
