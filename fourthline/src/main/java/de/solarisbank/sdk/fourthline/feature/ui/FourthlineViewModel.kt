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

    fun navigateFromPassingPossibilityToTc() {
        navigateTo(R.id.action_passingPossibilityFragment_to_termsAndConditionsFragment)
    }

    fun navigateFromTcToDocTypeSelection() {
        navigateTo(R.id.action_termsAndConditionsFragment_to_documentTypeSelectionFragment)
    }

    fun navigateFromSelfieInstructionsToSelfie() {
        navigateTo(R.id.action_selfieInstructionsFragment_to_selfieFragment)
    }

    fun navigateFromSelfieToSelfieResult() {
        navigateTo(R.id.action_selfieFragment_to_selfieResultFragment)
    }

    fun navigateFromSelfieResultToKycUploadFragment() {
        navigateTo(R.id.action_selfieResultFragment_to_kycUploadFragment)
    }

    fun navigateFromDocScanToDocTypeSelection(args: Bundle) {
        navigateTo(R.id.action_documentScanFragment_to_documentTypeSelectionFragment, args)
    }

    fun navigateFromDocTypeSelectionToDocScan(bundle: Bundle) {
        navigateTo(R.id.action_documentTypeSelectionFragment_to_documentScanFragment, bundle)
    }

    fun navigateFromDocScanToDocResult() {
        navigateTo(R.id.action_documentScanFragment_to_documentResultFragment)
    }

    fun navigateFromDocResultToSelfieInstructions() {
        navigateTo(R.id.action_documentResultFragment_to_selfieInstructionsFragment)
    }

    fun navigateFromKycUploadToUploadResult(nextStep: String? = null, identificationId: String? = null) {
        navigateTo(
            R.id.action_kycUploadFragment_to_uploadResultFragment,
            Bundle().apply {
                putString(NEXT_STEP_ARG, nextStep)
                putString(IDENTIFICATION_ID, identificationId)
            }
        )
    }

    fun navigateFromSelfieResultToSelfieInstructions() {
        val args = Bundle().apply {
            putString(
                FourthlineActivity.KEY_CODE,
                FourthlineActivity.FOURTHLINE_SELFIE_RETAKE
            )
        }
        resetFlowToSelfieInstructions(args)
    }

    fun navigateFromSelfieToSelfieInstructions(scanErrorMessage: String) {
        val args = Bundle().apply {
            putString(FourthlineActivity.KEY_CODE, FourthlineActivity.FOURTHLINE_SCAN_FAILED)
            putString(FourthlineActivity.KEY_MESSAGE, scanErrorMessage)
        }
        resetFlowToSelfieInstructions(args)
    }

    fun navigateFromKycUploadToPassingPossibility() {
        navigateTo(R.id.action_reset_to_passingPossibilityFragment)
    }

    fun navigateToWebViewFragment(url: String) {
        val bundle = Bundle().apply { putString(WEB_VIEW_URL_KEY, url) }
        navigateTo(R.id.action_termsAndConditionsFragment_to_webViewFragment, bundle)
    }

    private fun resetFlowToSelfieInstructions(args: Bundle? = null) {
        navigateTo(R.id.action_reset_to_selfie_instructions, args)
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