package de.solarisbank.sdk.fourthline.feature.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_IDENTIFICATION_SUCCESSFULL
import de.solarisbank.sdk.fourthline.feature.ui.webview.WebViewFragment.Companion.WEB_VIEW_URL_KEY

class FourthlineViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

//    val lastStep = MutableLiveData<>()

    private val _navigationActionId = MutableLiveData<Event<NaviDirection>>()
    val navigationActionId = _navigationActionId as LiveData<Event<NaviDirection>>

    fun navigateToWelcomeContainerFragment() {
        navigateTo(R.id.action_termsAndConditionsFragment_to_welcomeContainerFragment)
    }

    fun navigateToSelfieFragment() {
        navigateTo(R.id.action_welcomeContainerFragment_to_selfieFragment)
    }

    fun navigateToSelfieResultFragment() {
        navigateTo(R.id.action_selfieFragment_to_selfieResultFragment)
    }

    fun navigateToDocTypeSelectionFragment() {
        navigateTo(R.id.action_selfieResultFragment_to_documentTypeSelectionFragment)
    }

    fun navigateToDocScanFragment(bundle: Bundle) {
        navigateTo(R.id.action_documentTypeSelectionFragment_to_documentScanFragment, bundle)
    }

    fun navigateToDocScanResultFragment() {
        navigateTo(R.id.action_documentScanFragment_to_documentResultFragment)
    }

    fun navigateToLocationAccessFragment() {
        navigateTo(R.id.action_documentResultFragment_to_locationAccessFragment)
    }

    fun navigateToKycUploadFragemnt() {
        navigateTo(R.id.action_locationAccessFragment_to_kycUploadFragment)
    }

    fun navigateToWeViewFragment(url: String) {
        val bundle = Bundle().apply { putString(WEB_VIEW_URL_KEY, url) }
        navigateTo(R.id.action_termsAndConditionsFragment_to_webViewFragment, bundle)
    }

    fun resetFourthlineFlow() {
        navigateTo(R.id.action_reset_to_welcome_screen)
    }

    fun setFourthlineIdentificationSuccessful(identificationId: String) {
        val bundle = Bundle().apply {  }
        navigateTo(FOURTHLINE_IDENTIFICATION_SUCCESSFULL, bundle)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle? = null) {
        _navigationActionId.value = Event(NaviDirection(actionId, bundle))
    }

}