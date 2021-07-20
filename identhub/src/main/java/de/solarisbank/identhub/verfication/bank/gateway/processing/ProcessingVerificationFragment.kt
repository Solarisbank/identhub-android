package de.solarisbank.identhub.verfication.bank.gateway.processing

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.feature.model.ErrorState
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
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
                sharedViewModel.callOnPaymentResult(Bundle().apply {
                    putString(IDENTIFICATION_ID_KEY, result.id)
                    putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.VERIFICATION_BANK.index)
                })
            }
            is ErrorState -> {
                showAlert(result)
            }
        }
    }

    private fun showAlert(state: ErrorState) {
        when (state) {

            is ProcessingVerificationResult.PaymentInitAuthPersonError -> {
                Timber.d("showAlert 1")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                        cancelAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
                )
            }

            is ProcessingVerificationResult.PaymentInitFailedError -> {
                Timber.d("showAlert 2")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                        cancelAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
                )
            }

            is ProcessingVerificationResult.PaymentInitExpiredError -> {
                Timber.d("showAlert 3")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) },
                        cancelAction = { sharedViewModel.postDynamicNavigationNextStep(state.nextStep) }
                )
            }
            is ProcessingVerificationResult.GenericError -> {
                Timber.d("showAlert 4")
                showAlertFragment(
                        title = state.dialogTitle.getStringRes(),
                        message = state.dialogMessage.getStringRes(),
                        positiveLabel = state.dialogPositiveLabel.getStringRes(),
                        positiveAction = { sharedViewModel.callOnFailure() },
                        cancelAction = { sharedViewModel.callOnFailure() }
                )
            }

        }
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
            override val dialogTitle = "payment_init_auth_person_title"
            override val dialogMessage = "payment_init_auth_person_message"
            override val dialogPositiveLabel = "ok_button"
            override val dialogNegativeLabel = null
        }

        class PaymentInitFailedError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {//  9. payment init failed
            override val dialogTitle = "payment_init_failed_title"
            override val dialogMessage = "payment_init_failed_message"
            override val dialogPositiveLabel = "ok_button"
            override val dialogNegativeLabel = null
        }

        class PaymentInitExpiredError(val nextStep: String)
            : ProcessingVerificationResult(), ErrorState {// 10. payment init expired
            override val dialogTitle = "payment_init_expired_title"
            override val dialogMessage = "payment_init_expired_message"
            override val dialogPositiveLabel = "ok_button"
            override val dialogNegativeLabel = "invalid_iban_retry_button"
        }

        object GenericError : ProcessingVerificationResult(), ErrorState {
            override val dialogTitle = "generic_error_title"
            override val dialogMessage = "generic_error_message"
            override val dialogPositiveLabel = "ok_button"
            override val dialogNegativeLabel = null
        }
    }
}