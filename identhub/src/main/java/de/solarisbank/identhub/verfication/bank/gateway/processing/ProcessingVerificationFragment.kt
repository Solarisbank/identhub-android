package de.solarisbank.identhub.verfication.bank.gateway.processing

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.feature.model.ErrorState
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.sdk.core.viewModels
import timber.log.Timber

class ProcessingVerificationFragment : ProgressIndicatorFragment() {
    private val processingVerificationViewModel: ProcessingVerificationViewModel by lazy<ProcessingVerificationViewModel> { viewModels() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    private fun observeScreenState() {
        processingVerificationViewModel.processingVerificationEvent((sharedViewModel).iban!!).observe(viewLifecycleOwner, Observer { setState(it) })
    }

    private fun setState(result: ProcessingVerificationResult) {
        Timber.d("setState: $result")

        when (result) {
            is ProcessingVerificationResult.VerificationSuccessful -> {
                sharedViewModel.callOnPaymentResult(result.id)
            }
            is ErrorState -> {
                showAlert(result)
            }
        }
    }

    private fun showAlert(state: ErrorState) {
        val action: () -> Unit
        when (state) {
            is ProcessingVerificationResult.PaymentInitAuthPersonError -> {
                Timber.d("showAlert 1")
                action = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
            }

            is ProcessingVerificationResult.PaymentInitFailedError -> {
                Timber.d("showAlert 2")
                action = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
            }

            is ProcessingVerificationResult.PaymentInitExpiredError -> {
                Timber.d("showAlert 3")
                action = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
            }
            is ProcessingVerificationResult.GenericError -> {
                Timber.d("showAlert 4")
                action = { sharedViewModel.callOnFailure() }
            }
            else -> return
        }

        showAlertFragment(
            title = getString(state.dialogTitleId),
            message = getString(state.dialogMessageId),
            positiveLabel = getString(state.dialogPositiveLabelId),
            positiveAction = action,
            cancelAction = action
        )
    }

    override fun getTitleResource(): Int {
        return R.string.progress_indicator_precessing_verification_message
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }



    sealed class ProcessingVerificationResult {
        class VerificationSuccessful(val id: String) : ProcessingVerificationResult()

        class PaymentInitAuthPersonError(val nextStep: String, val retryAvailable: Boolean = false)
            : ProcessingVerificationResult(), ErrorState {// 8. payment init auth person
            override val dialogTitleId = R.string.payment_init_auth_person_title
            override val dialogMessageId = R.string.payment_init_auth_person_message
            override val dialogPositiveLabelId = R.string.ok_button
            override val dialogNegativeLabelId = null
        }

        class PaymentInitFailedError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {//  9. payment init failed
            override val dialogTitleId = R.string.payment_init_failed_title
            override val dialogMessageId = R.string.payment_init_failed_message
            override val dialogPositiveLabelId = R.string.ok_button
            override val dialogNegativeLabelId: Nothing? = null
        }

        class PaymentInitExpiredError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {// 10. payment init expired
            override val dialogTitleId = R.string.payment_init_expired_title
            override val dialogMessageId = R.string.payment_init_expired_message
            override val dialogPositiveLabelId = R.string.ok_button
            override val dialogNegativeLabelId = R.string.invalid_iban_retry_button
        }

        object GenericError : ProcessingVerificationResult(), ErrorState {
            override val dialogTitleId = R.string.generic_error_title
            override val dialogMessageId = R.string.generic_error_message
            override val dialogPositiveLabelId = R.string.ok_button
            override val dialogNegativeLabelId = null
        }
    }
}