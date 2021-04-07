package de.solarisbank.identhub.verfication.bank.error

import androidx.fragment.app.Fragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.error.ErrorMessageFragment

class VerificationBankErrorMessageFragment : ErrorMessageFragment() {

    override val cancelButtonLabel: Int
        get() = R.string.verification_phone_action_quit
    override val messageLabel: Int
        get() = R.string.verification_bank_error_message
    override val titleLabel: Int
        get() = R.string.verification_bank_error_title
    override val submitButtonLabel: Int
        get() = R.string.verification_bank_action_retry

    override fun initViews() {
        super.initViews()
        submitButton.setOnClickListener { sharedViewModel.retryBankVerification() }
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    companion object {
        fun newInstance(): Fragment {
            return VerificationBankErrorMessageFragment()
        }
    }
}