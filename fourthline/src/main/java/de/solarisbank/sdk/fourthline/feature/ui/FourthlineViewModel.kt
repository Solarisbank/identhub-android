package de.solarisbank.sdk.fourthline.feature.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_IDENTIFICATION_ERROR
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_IDENTIFICATION_SUCCESSFULL
import de.solarisbank.sdk.fourthline.feature.ui.webview.WebViewFragment.Companion.WEB_VIEW_URL_KEY
import timber.log.Timber
import java.util.*

class FourthlineViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

//    val lastStep = MutableLiveData<>()

    private val _navigationActionId = MutableLiveData<Event<NaviDirection>>()
    val navigationActionId = _navigationActionId as LiveData<Event<NaviDirection>>

    fun navigateFromPassingPossibilityToTcFragment() {
        navigateTo(R.id.action_passingPossibilityFragment_to_termsAndConditionsFragment)
    }

    fun navigateFromTcToWelcomeContainerFragment() {
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

    fun navigateBackToDocTypeSelectionFragment(args: Bundle) {
        navigateTo(R.id.action_documentScanFragment_to_documentTypeSelectionFragment, args)
    }

    fun navigateToDocScanFragment(bundle: Bundle) {
        navigateTo(R.id.action_documentTypeSelectionFragment_to_documentScanFragment, bundle)
    }

    fun navigateToDocScanResultFragment() {
        navigateTo(R.id.action_documentScanFragment_to_documentResultFragment)
    }

    fun navigateToKycUploadFragemnt() {
        navigateTo(R.id.action_documentResultFragment_to_kycUploadFragment)
    }

    fun navigateToUploadResultFragment(nextStep: String? = null, identificationId: String? = null) {
        navigateTo(
            R.id.action_kycUploadFragment_to_uploadResultFragment,
            Bundle().apply {
                putString(NEXT_STEP_ARG, nextStep)
                putString(IDENTIFICATION_ID, identificationId)
            }
        )
    }

    fun navigateToWeViewFragment(url: String) {
        val bundle = Bundle().apply { putString(WEB_VIEW_URL_KEY, url) }
        navigateTo(R.id.action_termsAndConditionsFragment_to_webViewFragment, bundle)
    }

    fun resetFlowToPassingPossibility(args: Bundle? = null) {
        navigateTo(R.id.action_reset_to_passingPossibilityFragment, args)
    }

    fun resetFlowToWelcomeScreen(args: Bundle? = null) {
        navigateTo(R.id.action_reset_to_welcome_screen, args)
    }

    fun setFourthlineIdentificationSuccessful(identificationId: String) {
        Timber.d("setFourthlineIdentificationSuccessful, identificationId : $identificationId")
        val bundle = Bundle().apply {
            putString(IdentHub.IDENTIFICATION_ID_KEY, identificationId)
            putString("uuid", UUID.randomUUID().toString())
        }
        navigateTo(FOURTHLINE_IDENTIFICATION_SUCCESSFULL, bundle)
    }

    fun setFourthlineIdentificationFailure() {
        navigateTo(FOURTHLINE_IDENTIFICATION_ERROR, null)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle? = null) {
        _navigationActionId.value = Event(NaviDirection(actionId, bundle))
    }

    fun postDynamicNavigationNextStep(arguments: Bundle) {
        _navigationActionId.value = Event<NaviDirection>(NaviDirection(IdentHubSession.ACTION_NEXT_STEP, arguments))
    }

    companion object {
        const val NEXT_STEP_ARG = "nextStepArg"
        const val IDENTIFICATION_ID = "identificationId"
    }

}