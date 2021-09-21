package de.solarisbank.identhub.identity;

import static de.solarisbank.identhub.domain.navigation.router.RouterKt.COMPLETED_STEP_KEY;
import static de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY;
import static de.solarisbank.sdk.data.entity.Identification.VERIFICATION_BANK_URL_KEY;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.navigation.router.COMPLETED_STEP;
import de.solarisbank.identhub.session.IdentHub;
import de.solarisbank.sdk.core.result.Event;
import de.solarisbank.sdk.core.result.Result;
import de.solarisbank.sdk.data.entity.Identification;
import de.solarisbank.sdk.domain.navigation.NaviDirection;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;
import timber.log.Timber;

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
        navigationActionId.postValue(new Event<>(new NaviDirection(actionId, bundle)));
    }

    private void navigateTo(int actionId) {
        navigateTo(actionId, null);
    }

    public void navigateToIBanVerification() {
        navigateTo(R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment);
    }

    public void navigateToContractSigningPreview() {
        if (IdentHub.INSTANCE.isPaymentResultAvailable()) {
            compositeDisposable.add(getIdentificationUseCase.execute(Unit.INSTANCE).subscribe(result -> {
                if (result instanceof Result.Success) {
                    Identification identification = ((Result.Success<Identification>) result).getData();
                    Bundle bundle = new Bundle();
                    bundle.putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.VERIFICATION_BANK.getIndex());
                    bundle.putString(IDENTIFICATION_ID_KEY, identification.getId());

                    navigateTo(ACTION_STOP_WITH_RESULT, bundle);
                }
            }, throwable -> Timber.e(throwable, "Cannot load identification data")));

        } else {
            navigateTo(R.id.action_processingVerificationFragment_to_contractSigningPreviewFragment);
        }
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

    public void doOnNavigationChanged(int actionId) {
        if (IdentHub.INSTANCE.isPaymentResultAvailable() && actionId == ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.VERIFICATION_BANK);
        } else if (actionId == ACTION_SUMMARY_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.CONTRACT_SIGNING);
        }
    }

    public COMPLETED_STEP getLastCompletedStep() {
        return identificationStepPreferences.get();
    }
}
