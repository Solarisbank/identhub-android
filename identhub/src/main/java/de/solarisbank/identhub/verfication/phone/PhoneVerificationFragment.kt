package de.solarisbank.identhub.verfication.phone

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
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.view.PhoneVerificationView
import de.solarisbank.sdk.feature.view.PhoneVerificationViewEvent
import de.solarisbank.sdk.feature.view.PhoneVerificationViewState

class PhoneVerificationFragment: IdentHubFragment() {

    private var phoneVerificationView: PhoneVerificationView? = null
    private var submitButton: Button? = null

    private val viewModel: PhoneVerificationViewModel by lazy { viewModels() }
    private val sharedViewModel: VerificationBankViewModel by lazy { activityViewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_phone_verification, container, false)
            .also {
                phoneVerificationView = it.findViewById(R.id.phoneVerification)
                phoneVerificationView?.eventListener = { event -> handlePhoneVerificationViewEvent(event) }
                submitButton = it.findViewById(R.id.submitButton)
                submitButton?.setOnClickListener { viewModel.submit(phoneVerificationView?.code) }
                customizeUI()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) {
            handleVerifyResult(it.verifyResult)
            phoneVerificationView?.updateState(PhoneVerificationViewState(
                phoneNumber = it.phoneNumber,
                verifyResult = it.verifyResult,
                resendButtonVisible = it.showResendButton
            ))
            submitButton?.isEnabled = it.submitEnabled
        }
        viewModel.getEventLiveData().observe(viewLifecycleOwner) {
            it.content?.let { event ->
                when (event) {
                    is PhoneVerificationEvent.ResentVerification -> phoneVerificationView?.startTimer()
                }
            }
        }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
        phoneVerificationView?.customize(customization)
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    private fun handlePhoneVerificationViewEvent(event: PhoneVerificationViewEvent) {
        when (event) {
            PhoneVerificationViewEvent.ResendTapped -> viewModel.resendCode()
            PhoneVerificationViewEvent.TimerExpired -> viewModel.resendTimerExpired()
            is PhoneVerificationViewEvent.CodeChanged -> viewModel.codeChanged(event.code)
        }
    }

    private fun handleVerifyResult(result: Result<*>?) {
        if (result is Result.Success) {
            sharedViewModel.navigateToPhoneVerificationSuccess()
        }
    }

    override fun onDestroyView() {
        phoneVerificationView = null
        submitButton = null
        super.onDestroyView()
    }
}