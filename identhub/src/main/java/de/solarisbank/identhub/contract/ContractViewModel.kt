package de.solarisbank.identhub.contract

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.R
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHub.isPaymentResultAvailable
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event

class ContractViewModel(savedStateHandle: SavedStateHandle, private val identificationStepPreferences: IdentificationStepPreferences, sessionUrlRepository: SessionUrlRepository) : ViewModel() {
    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()

    init {
        if (savedStateHandle.contains(IdentHub.SESSION_URL_KEY)) {
            sessionUrlRepository.save(savedStateHandle.get<String>(IdentHub.SESSION_URL_KEY))
        }
    }

    fun getLastCompletedStep(): COMPLETED_STEP? {
        return identificationStepPreferences.get()
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun doOnNavigationChanged(actionId: Int) {
        if (isPaymentResultAvailable && actionId == IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.VERIFICATION_BANK)
        } else if (actionId == R.id.action_contractSigningFragment_to_identitySummaryFragment) {
            identificationStepPreferences.save(COMPLETED_STEP.CONTRACT_SIGNING)
        }
    }

    fun callOnFailureResult() {
        navigationActionId.value = Event<NaviDirection>(NaviDirection(actionId = IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, null))
    }

    fun navigateToContractSigningProcess() {
        navigateTo(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment)
    }

    fun navigateToSummary() {
        navigateTo(R.id.action_contractSigningFragment_to_identitySummaryFragment)
    }

    fun sendResult(bundle: Bundle?) {
        navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, bundle)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?) {
        navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
    }

    private fun navigateTo(actionId: Int) {
        navigateTo(actionId, null)
    }
}