package de.solarisbank.identhub.bank.feature

import android.os.Bundle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.session.IdentHub.Companion.VERIFICATION_BANK_URL_KEY
import de.solarisbank.identhub.session.main.ModuleOutcome
import de.solarisbank.identhub.session.main.Navigator
import timber.log.Timber

class VerificationBankViewModel : ViewModel() {

    var iban: String? = null
    var navigator: Navigator? = null

    fun callOnPaymentResult(identificationId: String, nextStep: String) {
        Timber.d("callOnPaymentResult")
        navigator?.onOutcome(ModuleOutcome.NextStepOutcome(nextStep))
    }

    fun postDynamicNavigationNextStep(nextStep: String?) {
        Timber.d("postDynamicNavigationNextStep, nextStep : $nextStep")
        navigator?.onOutcome(ModuleOutcome.NextStepOutcome(nextStep))
    }

    fun moveToEstablishSecureConnection(bankIdentificationUrl: String?, nextStep: String? = null) {
        val bundle = Bundle()
        bundle.putString(VERIFICATION_BANK_URL_KEY, bankIdentificationUrl)
        navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment, bundle, nextStep)
    }

    fun moveToExternalGate(verificationBankUrl: String?) {
        val bundle = Bundle()
        bundle.putString(VERIFICATION_BANK_URL_KEY, verificationBankUrl)
        navigateTo(R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment, bundle)
    }

    fun callOnFailure() {
        Timber.d("callOnFailure")
        navigator?.onOutcome(ModuleOutcome.Failure())
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?, nextStep: String? = null) {
        if (nextStep != null) {
            Timber.d("navigateTo nextStep")
            navigator?.onOutcome(ModuleOutcome.NextStepOutcome(nextStep))
        } else {
            Timber.d("navigateTo fragmentDirection")
            navigator?.navigate(actionId, bundle)
        }
    }

    fun navigateToProcessingVerification(nextStep: String?) {
        navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment, Bundle(), nextStep)
    }

    fun cancelIdentification() {
        navigator?.onOutcome(ModuleOutcome.Failure("Identification cancelled"))
    }
}