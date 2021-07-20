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
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.identhub.session.IdentHub.isPaymentResultAvailable
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankViewModel(private val savedStateHandle: SavedStateHandle, private val identificationStepPreferences: IdentificationStepPreferences, private val getIdentificationUseCase: GetIdentificationUseCase, private val sessionUrlRepository: SessionUrlRepository) : ViewModel() {


    val cancelState = MutableLiveData<Boolean>()
    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()
    private val compositeDisposable = CompositeDisposable()
    var iban: String?
        get() = savedStateHandle.get(KEY_IBAN)
        set(value) {
           savedStateHandle[KEY_IBAN] = value
        }


    init {
        if (savedStateHandle.contains(SESSION_URL_KEY)) {
            sessionUrlRepository.save(savedStateHandle.get<String>(SESSION_URL_KEY))
        }
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun postDynamicNavigationNextStep(nextStep: String?) {
        Timber.d("postDynamicNavigationNextStep, nextStep : $nextStep")
        navigationActionId.value = Event<NaviDirection>(NaviDirection(IdentHubSession.ACTION_NEXT_STEP, Bundle().apply { putString(NEXT_STEP_KEY, nextStep) } ))
    }

    fun moveToEstablishSecureConnection(bankIdentificationUrl: String?, nextStep: String? = null) {
        val bundle = Bundle()
        bundle.putString(Identification.VERIFICATION_BANK_URL_KEY, bankIdentificationUrl)
        navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment, bundle, nextStep)
    }

    fun moveToExternalGate() {
        navigateTo(R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment)
    }

    fun callOnPaymentResult(bundle: Bundle) {
        navigationActionId.value = Event<NaviDirection>(NaviDirection(actionId = IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, bundle))
    }

    fun callOnFailure() {
        navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT)
    }

    fun navigateToIBanVerification() {
        navigateTo(R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?, nextStep: String? = null) {
        if (nextStep != null) {
            bundle?.putString(NEXT_STEP_KEY, nextStep)
            navigationActionId.postValue(Event(NaviDirection(IdentHubSession.ACTION_NEXT_STEP, bundle)))
        } else {
            navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
        }
    }

    fun navigateToProcessingVerification(nextStep: String?) {
        navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment, Bundle(), nextStep)
    }

    private fun navigateTo(actionId: Int, nextStep: String? = null) {
        navigateTo(actionId, null, nextStep)
    }

    fun navigateToVerficationBankIban() {
        navigateTo(R.id.action_verificationBankIntroFragment_to_verificationBankIbanFragment)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun doOnNavigationChanged(actionId: Int) {
        if (isPaymentResultAvailable && actionId == ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.VERIFICATION_BANK)
        } else if (actionId == ACTION_SUMMARY_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.CONTRACT_SIGNING)
        }
    }

    fun getLastCompletedStep(): COMPLETED_STEP? {
        return identificationStepPreferences.get()
    }

    fun cancelIdentification() {
        cancelState.value = true
    }

    companion object {
        const val ACTION_QUIT = -1
        const val ACTION_STOP_WITH_RESULT = 1
        const val ACTION_SUMMARY_WITH_RESULT = 2
        const val KEY_IBAN = "key_iban"
    }
}