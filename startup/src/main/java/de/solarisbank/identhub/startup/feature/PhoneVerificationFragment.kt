package de.solarisbank.identhub.startup.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.startup.PhoneFlow
import de.solarisbank.identhub.startup.R
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.feature.view.PhoneVerificationView
import de.solarisbank.sdk.feature.view.PhoneVerificationViewEvent
import de.solarisbank.sdk.feature.view.PhoneVerificationViewState
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneVerificationFragment: BaseFragment() {

    private var phoneVerificationView: PhoneVerificationView? = null
    private var submitButton: Button? = null

    private val viewModel: PhoneVerificationViewModel by viewModel()
    private val sharedViewModel: PhoneViewModel by koinNavGraphViewModel(PhoneFlow.navigationId)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_phone_verification, container, false)
            .also {
                phoneVerificationView = it.findViewById(R.id.phoneVerification)
                phoneVerificationView?.eventListener = { event -> handlePhoneVerificationViewEvent(event) }
                submitButton = it.findViewById(R.id.submitButton)
                submitButton?.setOnClickListener {
                    viewModel.onAction(PhoneVerificationAction.Submit(phoneVerificationView?.code))
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state().observe(viewLifecycleOwner) {
            handleVerifyResult(it.verifyResult)
            phoneVerificationView?.updateState(PhoneVerificationViewState(
                phoneNumber = it.phoneNumber,
                verifyResult = it.verifyResult,
                resendButtonVisible = it.shouldShowResend
            ))
            submitButton?.apply {
                isEnabled = it.submitEnabled
                buttonDisabled(!isEnabled)
            }
        }
        viewModel.events().observe(viewLifecycleOwner) {
            it.content?.let { event ->
                when (event) {
                    is PhoneVerificationEvent.CodeResent -> phoneVerificationView?.startTimer()
                }
            }
        }
        sharedViewModel.navigator = navigator
    }

    override fun customizeView(view: View) {
        submitButton?.customize(customization)
        phoneVerificationView?.customize(customization)
    }

    private fun handlePhoneVerificationViewEvent(event: PhoneVerificationViewEvent) {
        when (event) {
            is PhoneVerificationViewEvent.ResendTapped ->
                viewModel.onAction(PhoneVerificationAction.ResendCode)
            is PhoneVerificationViewEvent.TimerExpired ->
                viewModel.onAction(PhoneVerificationAction.TimerExpired)
            is PhoneVerificationViewEvent.CodeChanged ->
                viewModel.onAction(PhoneVerificationAction.CodeChanged(event.code))
        }
    }

    private fun handleVerifyResult(result: Result<*>?) {
        if (result is Result.Success) {
            sharedViewModel.onPhoneVerificationResult()
        }
    }

    override fun onDestroyView() {
        phoneVerificationView = null
        submitButton = null
        super.onDestroyView()
    }
}