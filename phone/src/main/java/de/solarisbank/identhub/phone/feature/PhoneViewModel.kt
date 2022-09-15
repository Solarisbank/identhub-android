package de.solarisbank.identhub.phone.feature

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.phone.R
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.identhub.session.main.Navigator

class PhoneViewModel: ViewModel() {
    var navigator: Navigator? = null

    fun onPhoneVerificationResult() {
        navigator?.navigate(R.id.action_phoneVerificationFragment_to_verificationPhoneSuccessMessageFragment)
    }

    fun onSuccessResult() {
        IdentHubSessionViewModel.INSTANCE?.phoneVerificationDone()
    }
}