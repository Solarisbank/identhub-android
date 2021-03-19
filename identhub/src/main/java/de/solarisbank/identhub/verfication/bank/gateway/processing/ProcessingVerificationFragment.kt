package de.solarisbank.identhub.verfication.bank.gateway.processing

import android.os.Bundle
import android.view.View
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.viewModels
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.shared.result.Event

class ProcessingVerificationFragment : ProgressIndicatorFragment() {
    private val processingVerificationViewModel: ProcessingVerificationViewModel by lazy { viewModels() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    private fun observeScreenState() {
        processingVerificationViewModel.processingVerificationEvent.observe(viewLifecycleOwner, { onProcessingVerificationEvent(it) })
    }

    private fun onProcessingVerificationEvent(event: Event<Any>) {
        sharedViewModel.moveToPaymentVerificationSuccessful()
    }

    override fun getTitleResource(): Int {
        return R.string.progress_indicator_precessing_verification_message
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }
}