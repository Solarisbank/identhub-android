package de.solarisbank.sdk.fourthline.feature.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.data.dto.FourthlineStepParametersDto
import de.solarisbank.sdk.fourthline.domain.step.parameters.FourthlineStepParametersUseCase
import de.solarisbank.sdk.fourthline.feature.ui.webview.WebViewFragment.Companion.WEB_VIEW_URL_KEY
import timber.log.Timber

class FourthlineViewModel (
    private val savedStateHandle: SavedStateHandle,
    private val fourthlineStepParametersUseCase: FourthlineStepParametersUseCase
    ) : ViewModel() {

    private val _navigationActionId = MutableLiveData<Event<NaviDirection>>()
    val navigationActionId = _navigationActionId as LiveData<Event<NaviDirection>>

    fun saveFourthlineStepParameters(fourthlineStepParametersDto: FourthlineStepParametersDto) {
        fourthlineStepParametersUseCase.saveParameters(fourthlineStepParametersDto)
    }

    fun navigateFromPassingPossibilityToTcFragment() {
        navigateTo(R.id.action_passingPossibilityFragment_to_termsAndConditionsFragment)
    }

    fun navigateFromTcToWelcomeContainerFragment() {
        navigateTo(R.id.action_termsAndConditionsFragment_to_welcomeContainerFragment)
    }

    fun navigateToSelfieFragment() {
        navigateTo(R.id.action_welcomeContainerFragment_to_documentTypeSelectionFragment)
    }

    fun navigateToSelfieResultFragment() {
        navigateTo(R.id.action_selfieFragment_to_selfieResultFragment)
    }

    fun navigateToDocTypeSelectionFragment() {
        navigateTo(R.id.action_selfieResultFragment_to_kycUploadFragment)
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
        navigateTo(R.id.action_documentResultFragment_to_selfieFragment)
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

    fun navigateToWebViewFragment(url: String) {
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
        _navigationActionId.value = Event(NaviDirection.VerificationSuccessfulStepResult(identificationId, COMPLETED_STEP.VERIFICATION_BANK.index))

    }

    fun setFourthlineIdentificationFailure() {
        _navigationActionId.value = Event(NaviDirection.VerificationFailureStepResult())
    }

    private fun navigateTo(actionId: Int, bundle: Bundle? = null) {
        _navigationActionId.value = Event(NaviDirection.FragmentDirection(actionId, bundle))
    }

    fun postDynamicNavigationNextStep(nextStep: String) {
        Timber.d("postDynamicNavigationNextStep. nextStep : $nextStep")
        _navigationActionId.value =
            Event<NaviDirection>(NaviDirection.NextStepStepResult(
                nextStep,
                COMPLETED_STEP.VERIFICATION_BANK.index
            ))
    }

    companion object {
        const val NEXT_STEP_ARG = "nextStepArg"
        const val IDENTIFICATION_ID = "identificationId"
    }

}