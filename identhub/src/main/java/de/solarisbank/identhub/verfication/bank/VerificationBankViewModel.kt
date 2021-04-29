package de.solarisbank.identhub.verfication.bank

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.R
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.IdentHub.LAST_COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.identhub.session.IdentHub.isPaymentResultAvailable
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankViewModel(savedStateHandle: SavedStateHandle, private val identificationStepPreferences: IdentificationStepPreferences, private val getIdentificationUseCase: GetIdentificationUseCase, private val sessionUrlRepository: SessionUrlRepository) : ViewModel() {
    val ACTION_QUIT = -1
    val ACTION_STOP_WITH_RESULT = 1
    val ACTION_SUMMARY_WITH_RESULT = 2

    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        check(savedStateHandle.contains(SESSION_URL_KEY)) { "You have to initialize SDK with partner token" }
        sessionUrlRepository.save(savedStateHandle.get<String>(SESSION_URL_KEY))
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun moveToEstablishSecureConnection(bankIdentificationUrl: String?) {
        val bundle = Bundle()
        bundle.putString(Identification.VERIFICATION_BANK_URL_KEY, bankIdentificationUrl)
        navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment, bundle)
    }

    fun moveToExternalGate() {
        navigateTo(R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment)
    }

    fun moveToPaymentVerificationSuccessful() {
        navigateTo(R.id.action_processingVerificationFragment_to_verificationBankSuccessMessageFragment)
    }

    fun navigateToContractSigningPreview() {
        if (isPaymentResultAvailable) {
            compositeDisposable.add(getIdentificationUseCase.execute(Unit).subscribe({ result: Result<Identification>? ->
                if (result is Result.Success<*>) {
                    val (id) = (result as Result.Success<Identification>).data
                    val bundle = Bundle()
                    bundle.putInt(LAST_COMPLETED_STEP_KEY, IdentHubSession.Step.VERIFICATION_BANK.index)
                    bundle.putString(IDENTIFICATION_ID_KEY, id)
                    navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, bundle)
                }
            }, { throwable: Throwable? -> Timber.e(throwable, "Cannot load identification data") }))
        }
    }

    fun navigateToIBanVerification() {
        navigateTo(R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?) {
        navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
    }

    fun navigateToProcessingVerification() {
        navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment)
    }

    private fun navigateTo(actionId: Int) {
        navigateTo(actionId, null)
    }

    fun navigateToVerficationBankIban() {
        navigateTo(R.id.action_verificationBankIntroFragment_to_verificationBankIbanFragment)
    }

    fun navigateToVerificationBankError() {
        navigateTo(R.id.action_verificationBankFragment_to_verificationBankErrorMessageFragment)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun doOnNavigationChanged(actionId: Int) {
        if (isPaymentResultAvailable && actionId == ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(IdentHubSession.Step.VERIFICATION_BANK)
        } else if (actionId == ACTION_SUMMARY_WITH_RESULT) {
            identificationStepPreferences.save(IdentHubSession.Step.CONTRACT_SIGNING)
        }
    }

    fun getLastCompletedStep(): IdentHubSession.Step? {
        return identificationStepPreferences.get()
    }
}