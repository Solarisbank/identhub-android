package de.solarisbank.identhub.identity;

import static de.solarisbank.identhub.session.IdentHub.VERIFICATION_BANK_URL_KEY;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.session.feature.navigation.NaviDirection;
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP;
import de.solarisbank.sdk.domain.model.result.Event;
import io.reactivex.disposables.CompositeDisposable;

//todo remove
public final class IdentityActivityViewModel extends ViewModel {

    public static final int ACTION_QUIT = -1;
    public static final int ACTION_STOP_WITH_RESULT = 1;
    public static final int ACTION_SUMMARY_WITH_RESULT = 2;

    private final MutableLiveData<Event<NaviDirection>> navigationActionId = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GetIdentificationUseCase getIdentificationUseCase;
    private final IdentificationStepPreferences identificationStepPreferences;

    public IdentityActivityViewModel(
            final GetIdentificationUseCase getIdentificationUseCase,
            final IdentificationStepPreferences identificationStepPreferences) {
        this.getIdentificationUseCase = getIdentificationUseCase;
        this.identificationStepPreferences = identificationStepPreferences;
    }

    public LiveData<Event<NaviDirection>> getNaviDirectionEvent() {
        return navigationActionId;
    }

    public void moveToEstablishSecureConnection(final String bankIdentificationUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(VERIFICATION_BANK_URL_KEY, bankIdentificationUrl);

        navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment, bundle);
    }

    public void moveToExternalGate() {
        navigateTo(R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment);
    }

    public void navigateToVerificationPhoneSuccess() {
        navigateTo(R.id.action_verificationPhoneFragment2_to_verificationPhoneSuccessMessageFragment);
    }

    private void navigateTo(int actionId, Bundle bundle) {
        navigationActionId.postValue(new Event<>(new NaviDirection.FragmentDirection(actionId, bundle)));
    }

    private void navigateTo(int actionId) {
        navigateTo(actionId, null);
    }

    public void navigateToIBanVerification() {
        navigateTo(R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment);
    }

    public void navigateToContractSigningProcess() {
        navigateTo(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment);
    }

    public void navigateToSummary() {
        navigateTo(ACTION_SUMMARY_WITH_RESULT);
    }

    public void navigateToProcessingVerification() {
        navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment);
    }

    public void retryPhoneVerification() {
        navigateTo(R.id.action_verificationPhoneErrorMessageFragment_to_verificationPhoneFragment2);
    }

    public void quitIdentity() {
        navigateTo(ACTION_QUIT);
    }

    public void retryBankVerification() {
        navigateTo(R.id.action_verificationBankErrorMessageFragment_to_verificationBankFragment);
    }

    public void navigateToVerificationPhoneError() {
        navigateTo(R.id.action_verificationPhoneFragment2_to_verificationPhoneErrorMessageFragment);
    }

    public void navigateToVerificationBankError() {
        navigateTo(R.id.action_verificationBankFragment_to_verificationBankErrorMessageFragment);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }

    public COMPLETED_STEP getLastCompletedStep() {
        return identificationStepPreferences.get();
    }
}
