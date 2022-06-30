package de.solarisbank.identhub.verfication.bank

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.R
import de.solarisbank.identhub.session.IdentHub.Companion.SESSION_URL_KEY
import de.solarisbank.identhub.session.IdentHub.Companion.VERIFICATION_BANK_URL_KEY
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val sessionUrlRepository: SessionUrlRepository,
    private val initializationInfoRepository: InitializationInfoRepository
    ) : ViewModel() {


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

    fun isPhoneVerified(): Boolean {
        return initializationInfoRepository.isPhoneVerified()
    }

    fun callOnPaymentResult(identificationId: String, nextStep: String) {
        Timber.d("callOnPaymentResult")
        navigationActionId.value = Event(NaviDirection.PaymentSuccessfulStepResult(identificationId, nextStep))
    }

    fun postDynamicNavigationNextStep(nextStep: String?) {
        Timber.d("postDynamicNavigationNextStep, nextStep : $nextStep")
        navigationActionId.value =
            Event<NaviDirection>(NaviDirection.NextStepStepResult(nextStep))
    }

    fun moveToEstablishSecureConnection(bankIdentificationUrl: String?, nextStep: String? = null) {
        val bundle = Bundle()
        bundle.putString(VERIFICATION_BANK_URL_KEY, bankIdentificationUrl)
        navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment, bundle, nextStep)
    }

    fun moveToExternalGate() {
        navigateTo(R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment)
    }

    fun callOnFailure() {
        Timber.d("callOnFailure")
        navigationActionId.value = Event(NaviDirection.VerificationFailureStepResult(COMPLETED_STEP.VERIFICATION_BANK.index))
    }

    fun navigateToIBanVerification() {
        navigateTo(R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment)
    }

    fun navigateToPhoneVerificationSuccess() {
        navigateTo(R.id.action_phoneVerificationFragment_to_verificationPhoneSuccessMessageFragment)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?, nextStep: String? = null) {
        if (nextStep != null) {
            Timber.d("navigateTo nextStep")
            navigationActionId.postValue(Event(NaviDirection.NextStepStepResult(nextStep)))
        } else {
            Timber.d("navigateTo fragmentDirection")
            navigationActionId.postValue(Event(NaviDirection.FragmentDirection(actionId, bundle)))
        }
    }

    fun navigateToProcessingVerification(nextStep: String?) {
        navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment, Bundle(), nextStep)
    }

    private fun navigateTo(actionId: Int, nextStep: String? = null) {
        navigateTo(actionId, null, nextStep)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
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