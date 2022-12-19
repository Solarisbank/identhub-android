package de.solarisbank.identhub.bank.feature.processing

import android.os.Bundle
import android.view.View
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.bank.data.ErrorState
import de.solarisbank.identhub.bank.feature.progress.ProgressIndicatorFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProcessingVerificationFragment : ProgressIndicatorFragment() {
    private val processingVerificationViewModel: ProcessingVerificationViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    private fun observeScreenState() {
        processingVerificationViewModel.processingVerificationEvent(sharedViewModel.iban!!)
            .observe(viewLifecycleOwner) { setState(it) }
    }

    private fun setState(result: ProcessingVerificationResult) {
        Timber.d("setState: $result")

        when (result) {
            is ProcessingVerificationResult.VerificationSuccessful -> {
                sharedViewModel.callOnPaymentResult(result.id, result.nextStep)
            }
            is ErrorState -> {
                showAlert(result)
            }
            else -> { /* Ignore */ }
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
        return R.string.identhub_progress_indicator_precessing_verification_message
    }

    sealed class ProcessingVerificationResult {
        class VerificationSuccessful(val id: String, val nextStep: String) : ProcessingVerificationResult()

        class PaymentInitAuthPersonError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {// 8. payment init auth person
            override val dialogTitleId = R.string.identhub_payment_init_auth_person_title
            override val dialogMessageId = R.string.identhub_payment_init_auth_person_message
            override val dialogPositiveLabelId = R.string.identhub_ok_button
            override val dialogNegativeLabelId: Nothing? = null
        }

        class PaymentInitFailedError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {//  9. payment init failed
            override val dialogTitleId = R.string.identhub_payment_init_failed_title
            override val dialogMessageId = R.string.identhub_payment_init_failed_message
            override val dialogPositiveLabelId = R.string.identhub_ok_button
            override val dialogNegativeLabelId: Nothing? = null
        }

        class PaymentInitExpiredError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {// 10. payment init expired
            override val dialogTitleId = R.string.identhub_payment_init_expired_title
            override val dialogMessageId = R.string.identhub_payment_init_expired_message
            override val dialogPositiveLabelId = R.string.identhub_ok_button
            override val dialogNegativeLabelId = R.string.identhub_iban_verification_invalid_iban_retry_button
        }

        object GenericError : ProcessingVerificationResult(), ErrorState {
            override val dialogTitleId = R.string.identhub_generic_error_title
            override val dialogMessageId = R.string.identhub_generic_error_message
            override val dialogPositiveLabelId = R.string.identhub_ok_button
            override val dialogNegativeLabelId: Nothing? = null
        }
    }
}