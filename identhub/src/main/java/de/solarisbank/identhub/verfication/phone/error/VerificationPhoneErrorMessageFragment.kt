package de.solarisbank.identhub.verfication.phone.error

import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.error.ErrorMessageFragment

class VerificationPhoneErrorMessageFragment : ErrorMessageFragment() {

    override val cancelButtonLabel: Int
        get() = R.string.verification_phone_action_quit
    override val messageLabel: Int
        get() = R.string.verification_phone_error_default_message
    override val titleLabel: Int
        get() = R.string.verification_phone_error_title
    override val submitButtonLabel: Int
        get() = R.string.verification_phone_action_retry

    override fun initViews() {
        super.initViews()
        submitButton.setOnClickListener { sharedViewModel.retryPhoneVerification() }
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }
}