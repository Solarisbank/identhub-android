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
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHub.isPaymentResultAvailable
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event

class ContractViewModel(savedStateHandle: SavedStateHandle, private val identificationStepPreferences: IdentificationStepPreferences, sessionUrlRepository: SessionUrlRepository) : ViewModel() {
    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()

    init {
        check(savedStateHandle.contains(IdentHub.SESSION_URL_KEY)) { "You have to initialize SDK with partner token" }
        sessionUrlRepository.save(savedStateHandle.get<String>(IdentHub.SESSION_URL_KEY))
    }

    fun getLastCompletedStep(): IdentHubSession.Step? {
        return identificationStepPreferences.get()
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun doOnNavigationChanged(actionId: Int) {
        if (isPaymentResultAvailable && actionId == IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(IdentHubSession.Step.VERIFICATION_BANK)
        } else if (actionId == IdentityActivityViewModel.ACTION_SUMMARY_WITH_RESULT) {
            identificationStepPreferences.save(IdentHubSession.Step.CONTRACT_SIGNING)
        }
    }

    fun navigateToContractSigningProcess() {
        navigateTo(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment)
    }

    fun navigateToSummary() {
        navigateTo(IdentityActivityViewModel.ACTION_SUMMARY_WITH_RESULT)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?) {
        navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
    }

    private fun navigateTo(actionId: Int) {
        navigateTo(actionId, null)
    }
}