package de.solarisbank.sdk.fourthline.feature.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.main.Navigator
import de.solarisbank.identhub.session.module.ModuleOutcome
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.data.FourthlineStorage
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument
import de.solarisbank.sdk.fourthline.feature.ui.kyc.result.UploadResultOutcome
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadOutcome
import de.solarisbank.sdk.fourthline.feature.ui.passing.possibility.PassingPossibilityOutcome
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanFragmentArgs
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanResult
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocTypeSelectionOutcome
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieOutcome
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultOutcome
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieInstructionsOutcome
import de.solarisbank.sdk.fourthline.feature.ui.intro.IntroOutcome
import de.solarisbank.sdk.fourthline.feature.ui.orca.OrcaOutcome

class FourthlineViewModel (
        private val initialConfigStorage: InitialConfigStorage,
        private val fourthlineStorage: FourthlineStorage,
    ) : ViewModel() {

    var navigator: Navigator? = null

    private val orcaEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        orcaEnabledLiveData.value = initialConfigStorage.get().isOrcaEnabled
    }

    fun getOrcaEnabledLiveData(): LiveData<Boolean> {
        return orcaEnabledLiveData
    }

    fun onPassingPossibilityOutcome(outcome: PassingPossibilityOutcome) {
        when (outcome) {
            is PassingPossibilityOutcome.Success -> {
                navigateTo(R.id.action_passingPossibilityFragment_to_fourthlineIntroFragment)
            }
            is PassingPossibilityOutcome.Failed -> {
                setFourthlineIdentificationFailure(outcome.message)
            }
        }
    }

    fun onIntroOutcome(outcome: IntroOutcome) {
        when (outcome) {
            is IntroOutcome.Success -> {
                if (initialConfigStorage.get().isOrcaEnabled) {
                    navigateTo(R.id.action_fourthlineIntroFragment_to_orcaFragment)
                } else {
                    navigateTo(R.id.action_fourthlineIntroFragment_to_documentTypeSelectionFragment)
                }
            }
            is IntroOutcome.Failure -> {
                setFourthlineIdentificationFailure(outcome.message)
            }
        }
    }

    fun onOrcaOutcome(outcome: OrcaOutcome) {
        when (outcome) {
            is OrcaOutcome.Success -> {
                navigateTo(R.id.action_orcaFragment_to_kycUploadFragment)
            }
            is OrcaOutcome.Failure -> {
                setFourthlineIdentificationFailure(outcome.message)
            }
        }
    }

    fun onSelfieInstructionsOutcome(outcome: SelfieInstructionsOutcome) {
        when(outcome) {
            is SelfieInstructionsOutcome.Success -> {
                navigateTo(R.id.action_selfieInstructionsFragment_to_selfieFragment)
            }
            is SelfieInstructionsOutcome.Failed -> {
                setFourthlineIdentificationFailure(outcome.message)
            }
        }
    }

    fun onSelfieOutcome(outcome: SelfieOutcome) {
        when (outcome) {
            is SelfieOutcome.Success -> {
                navigateTo(R.id.action_selfieFragment_to_selfieResultFragment)
            }
            is SelfieOutcome.Failed -> {
                val args = Bundle().apply {
                    putString(KEY_CODE, FOURTHLINE_SCAN_FAILED)
                    putString(KEY_MESSAGE, outcome.errorMessage)
                }
                resetFlowToSelfieInstructions(args)
            }
        }
    }

    fun onSelfieResultOutcome(outcome: SelfieResultOutcome) {
        when (outcome) {
            is SelfieResultOutcome.Success -> {
                navigateTo(R.id.action_selfieResultFragment_to_kycUploadFragment)
            }
            is SelfieResultOutcome.Failed -> {
                resetFlowToSelfieInstructions(bundleOf(KEY_CODE to FOURTHLINE_SELFIE_RETAKE))
            }
        }
    }

    fun onDocScanOutcome(result: DocScanResult) {
        when (result) {
            is DocScanResult.Success -> {
                if (result.isSecondaryScan) {
                    navigateTo(R.id.identhub_action_secondaryDocScanFragment_to_selfieInstructionsFragment)
                } else {
                    navigateTo(R.id.action_documentScanFragment_to_documentResultFragment)
                }
            }
            is DocScanResult.ScanFailed -> {
                navigateTo(
                    R.id.action_documentScanFragment_to_documentTypeSelectionFragment,
                    result.bundle
                )
            }
        }
    }

    fun onHealthCardInstructionsOutcome() {
        navigateTo(
            R.id.identhub_action_healthCardInstructionsFragment_to_secondaryDocScanFragment,
            DocScanFragmentArgs(AppliedDocument.TIN_DOCUMENT, true).toBundle()
        )
    }

    fun onDocTypeSelectionOutcome(outcome: DocTypeSelectionOutcome) {
        when (outcome) {
            is DocTypeSelectionOutcome.Success -> {
                navigateTo(
                    R.id.action_documentTypeSelectionFragment_to_documentScanFragment,
                    DocScanFragmentArgs(outcome.docType, false).toBundle()
                )
            }
            is DocTypeSelectionOutcome.Failed -> setFourthlineIdentificationFailure(outcome.message)
        }
    }

    fun onDocResultOutcome() {
        val shouldScanSecondaryDocument = initialConfigStorage.get().isSecondaryDocScanRequired
                && !fourthlineStorage.isIdCardSelected
        when (shouldScanSecondaryDocument) {
            true -> navigateTo(R.id.action_documentResultFragment_to_healthCardInstructionsFragment)
            false -> navigateTo(R.id.action_documentResultFragment_to_selfieInstructionsFragment)
        }
    }

    fun onKycUploadOutcome(outcome: KycUploadOutcome) {
        when(outcome) {
            is KycUploadOutcome.Success -> {
                navigateTo(
                    R.id.action_kycUploadFragment_to_uploadResultFragment,
                    Bundle().apply {
                        putString(NEXT_STEP_ARG, outcome.nextStep)
                        putString(IDENTIFICATION_ID, outcome.identificationId)
                    }
                )
            }
            is KycUploadOutcome.RestartFlow -> restartFlow()
            is KycUploadOutcome.Failed -> setFourthlineIdentificationFailure(outcome.message)
        }

    }

    fun onUploadResultOutcome(outcome: UploadResultOutcome) {
        if (outcome.nextStep != null) {
            navigator?.onOutcome(ModuleOutcome.NextStepOutcome(outcome.nextStep))
        } else if (outcome.identificationId != null) {
            navigator?.onOutcome(ModuleOutcome.Finished(outcome.identificationId))
        }
    }

    fun restartFlow() {
        navigateTo(R.id.action_reset_to_passingPossibilityFragment)
    }

    private fun resetFlowToSelfieInstructions(args: Bundle? = null) {
        navigateTo(R.id.action_reset_to_selfie_instructions, args)
    }

    private fun setFourthlineIdentificationFailure(errorMessage: String) {
        navigator?.onOutcome(ModuleOutcome.Failure(errorMessage))
    }

    private fun navigateTo(actionId: Int, bundle: Bundle? = null) {
        navigator?.navigate(actionId, bundle)
    }

    companion object {
        const val NEXT_STEP_ARG = "nextStepArg"
        const val IDENTIFICATION_ID = "identificationId"
        const val KEY_CODE = "key_code"
        const val KEY_MESSAGE = "key_message"
        const val FOURTHLINE_SELFIE_RETAKE = "fourthline_selfie_retake"
        const val FOURTHLINE_SCAN_FAILED = "fourthline_scan_failed"
    }

}