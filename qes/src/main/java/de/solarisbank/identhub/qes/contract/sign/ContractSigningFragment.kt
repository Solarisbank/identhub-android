package de.solarisbank.identhub.qes.contract.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.solarisbank.identhub.qes.QESFlow
import de.solarisbank.identhub.qes.R
import de.solarisbank.identhub.qes.contract.ContractViewModel
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.sdk.feature.view.hideKeyboard
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.feature.view.PhoneVerificationView
import de.solarisbank.sdk.feature.view.PhoneVerificationViewEvent
import de.solarisbank.sdk.feature.view.PhoneVerificationViewState
import io.reactivex.disposables.Disposables
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ContractSigningFragment : BaseFragment() {
    private var disposable = Disposables.disposed()
    private val sharedViewModel: ContractViewModel by koinNavGraphViewModel(QESFlow.navigationId)
    private val viewModel: ContractSigningViewModel by viewModel()
    private var phoneVerificationView: PhoneVerificationView? = null
    private var transactionDescription: TextView? = null
    private var submitButton: Button? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.identhub_fragment_contract_signing, container, false)
            .also {
                phoneVerificationView = it.findViewById(R.id.phoneVerification)
                phoneVerificationView?.eventListener = { event ->
                    handlePhoneVerificationEvent(event)
                }
                transactionDescription = it.findViewById(R.id.transactionDescription)
                submitButton = it.findViewById(R.id.submitButton)
            }
    }

    override fun customizeView(view: View) {
        submitButton?.customize(customization)
        phoneVerificationView?.customize(customization)
    }

    private fun handlePhoneVerificationEvent(event: PhoneVerificationViewEvent) {
        when (event) {
            is PhoneVerificationViewEvent.ResendTapped -> { viewModel.onAction(ContractSigningAction.ResendCode) }
            is PhoneVerificationViewEvent.CodeChanged -> { updateSubmitButtonState() }
            is PhoneVerificationViewEvent.TimerExpired -> { viewModel.onAction(ContractSigningAction.TimerExpired)}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.state().observe(viewLifecycleOwner, ::stateUpdated)
        viewModel.events().observe(viewLifecycleOwner, ::onEvent)
    }

    private fun onEvent(event: Event<ContractSigningEvent>?) {
        val content = event?.content ?: return

        when (content) {
            is ContractSigningEvent.CodeResent -> {
                phoneVerificationView?.startTimer()
            }
        }
    }

    private fun initViews() {
        submitButton?.setOnClickListener {
            viewModel.onAction(ContractSigningAction.Submit(code = phoneVerificationView!!.code))
        }
        updateSubmitButtonState()
    }

    private fun stateUpdated(state: ContractSigningState) {
        phoneVerificationView?.updateState(PhoneVerificationViewState(
            state.phoneNumber,
            state.signingResult,
            state.shouldShowResend)
        )
        signingResultChanged(state.signingResult)
    }

    private fun signingResultChanged(result: Result<ContractSigningResult>?) {
        Timber.d("onIdentificationResultChanged, result: $result")
        when(result) {
            is Result.Loading -> {
                hideKeyboard()
                submitButton?.isEnabled = false
            }
            is Result.Success -> {
                val signingResult = result.data
                updateViewWithSigningResult(signingResult)
                sharedViewModel.onContractSigningResult(signingResult)
            }
            else -> {
                submitButton?.isEnabled = isSubmitButtonEnabled()
            }
        }
    }

    private fun updateViewWithSigningResult(result: ContractSigningResult) {
        submitButton?.isEnabled = false
        when (result) {
            is ContractSigningResult.Confirmed -> {
                showTransactionDescription(result.identificationId)
            }
            is ContractSigningResult.Failed -> {
                showTransactionDescription(result.identificationId)
            }
            else -> {
                submitButton?.isEnabled = isSubmitButtonEnabled()
            }
        }
    }

    private fun showTransactionDescription(identificationId: String) {
        transactionDescription!!.visibility = View.VISIBLE
        transactionDescription!!.text = String.format(
            getString(R.string.identhub_contract_signing_preview_transaction_info),
            identificationId
        )
    }

    private fun updateSubmitButtonState() {
        submitButton?.apply {
            isEnabled = isSubmitButtonEnabled()
            buttonDisabled(!isEnabled)
        }
    }

    private fun isSubmitButtonEnabled(): Boolean {
        val code = phoneVerificationView!!.code
        return code.isNotEmpty() && code.length == CODE_LENGTH

    }

    override fun onDestroyView() {
        disposable.dispose()
        phoneVerificationView = null
        transactionDescription = null
        submitButton = null
        super.onDestroyView()
    }

    companion object {
        const val CODE_LENGTH = 6
    }
}