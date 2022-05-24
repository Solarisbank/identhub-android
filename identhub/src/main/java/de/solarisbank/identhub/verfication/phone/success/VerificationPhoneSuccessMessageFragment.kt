package de.solarisbank.identhub.verfication.phone.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.feature.customization.customize

class VerificationPhoneSuccessMessageFragment : IdentHubFragment()  {

    private var submitButton: Button? = null
    private val sharedViewModel: VerificationBankViewModel by lazy { activityViewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_phone_verification_success, container, false)
            .also {
                submitButton = it.findViewById(R.id.submitButton)
                submitButton?.setOnClickListener {  sharedViewModel.navigateToIBanVerification() }
                customizeUI()
            }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
    }
}