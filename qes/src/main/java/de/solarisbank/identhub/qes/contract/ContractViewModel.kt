package de.solarisbank.identhub.qes.contract

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.qes.R
import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.identhub.session.main.Navigator

class ContractViewModel: ViewModel() {

    var navigator: Navigator? = null

    fun onContractSigningPreviewResult() {
        navigateToContractSigning()
    }

    fun onContractSigningResult(result: ContractSigningResult) {
        when(result) {
            is ContractSigningResult.Successful -> handleSuccessResult(result.identificationId)
            is ContractSigningResult.Confirmed -> handleConfirmedResult(result.identificationId)
            is ContractSigningResult.Failed -> handleFailedResult()
            is ContractSigningResult.GenericError -> { /* TODO What to do with generic error? */ }
        }
    }

    private fun navigateToContractSigning() {
        navigator?.navigate(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment)
    }

    private fun handleSuccessResult(identificationId: String) {
        setResult(
            NaviDirection.VerificationSuccessfulStepResult(identificationId, COMPLETED_STEP.CONTRACT_SIGNING.index)
        )
    }

    private fun handleConfirmedResult(identificationId: String) {
        setResult(NaviDirection.ConfirmationSuccessfulStepResult(identificationId))
    }

    private fun handleFailedResult() {
        setResult(NaviDirection.VerificationFailureStepResult())
    }

    private fun setResult(sessionStepResult: SessionStepResult) {
        IdentHubSessionViewModel.INSTANCE?.setSessionResult(sessionStepResult)
    }
}