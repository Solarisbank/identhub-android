package de.solarisbank.identhub.qes.contract

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.qes.R
import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.identhub.session.module.ModuleOutcome
import de.solarisbank.identhub.session.main.Navigator
import de.solarisbank.sdk.data.entity.Status

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
        navigator?.onOutcome(ModuleOutcome.Finished(identificationId, Status.SUCCESSFUL.label))
    }

    private fun handleConfirmedResult(identificationId: String) {
        navigator?.onOutcome(ModuleOutcome.Finished(identificationId, Status.CONFIRMED.label))
    }

    private fun handleFailedResult() {
        navigator?.onOutcome(ModuleOutcome.Failure())
    }
}