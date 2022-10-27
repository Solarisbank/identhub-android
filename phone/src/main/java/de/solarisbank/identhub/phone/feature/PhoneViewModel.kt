package de.solarisbank.identhub.phone.feature

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.phone.R
import de.solarisbank.identhub.session.main.ModuleOutcome
import de.solarisbank.identhub.session.main.Navigator
import de.solarisbank.identhub.session.main.outcome.PhoneModuleOutcome

class PhoneViewModel: ViewModel() {
    var navigator: Navigator? = null

    fun onPhoneVerificationResult() {
        navigator?.navigate(R.id.action_phoneVerificationFragment_to_verificationPhoneSuccessMessageFragment)
    }

    fun onSuccessResult() {
        navigator?.onOutcome(ModuleOutcome.Other(PhoneModuleOutcome()))
    }
}