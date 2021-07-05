package de.solarisbank.identhub.verfication.bank.gateway.processing

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import de.solarisbank.identhub.R
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.viewModels
import timber.log.Timber

class ProcessingVerificationFragment : ProgressIndicatorFragment() {
    private val processingVerificationViewModel: ProcessingVerificationViewModel by lazy<ProcessingVerificationViewModel> { viewModels() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    private fun observeScreenState() {
        processingVerificationViewModel.processingVerificationEvent((activity as VerificationBankActivity).iban!!).observe(viewLifecycleOwner, Observer { onProcessingVerificationEvent(it) })
    }

    private fun onProcessingVerificationEvent(result: Result<IdentificationUiModel>) {
        Timber.d("onProcessingVerificationEvent result.data : ${result.data}")
        if (result.succeeded && result.data != null) {
            Timber.d("onProcessingVerificationEvent 1")
            val model = result.data
            if (model?.status == Status.AUTHORIZATION_REQUIRED.label || model?.status == Status.IDENTIFICATION_DATA_REQUIRED.label) {
                Timber.d("onProcessingVerificationEvent 2")
                sharedViewModel.callOnPaymentResult(Bundle().apply {
                   putString(IDENTIFICATION_ID_KEY, model.id)
                   putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.VERIFICATION_BANK.index)
                })
            } else if (model!!.nextStep != null) {
                Timber.d("onProcessingVerificationEvent 3")
                sharedViewModel.postDynamicNavigationNextStep(model.nextStep)
                //todo should be the error screen shown?
            } else {
                Timber.d("onProcessingVerificationEvent 4 pair.first.label: ${model!!.status}")
            }
        }
    }

    override fun getTitleResource(): Int {
        return R.string.progress_indicator_precessing_verification_message
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }
}