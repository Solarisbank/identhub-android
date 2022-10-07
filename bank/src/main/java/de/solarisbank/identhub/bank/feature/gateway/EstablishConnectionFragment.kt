package de.solarisbank.identhub.bank.feature.gateway

import android.os.Bundle
import android.view.View
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.bank.feature.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.domain.model.result.Event
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EstablishConnectionFragment : ProgressIndicatorFragment() {
    private val verificationBankExternalGateViewModel: VerificationBankExternalGateViewModel
        by viewModel { parametersOf(verificationBankUrl) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    val verificationBankUrl: String?
    get() = arguments?.getString(IdentHub.VERIFICATION_BANK_URL_KEY)

    override fun getIconResource(): Int {
        return R.drawable.identhub_ic_shield_32_secondary
    }

    override fun getTitleResource(): Int {
        return R.string.identhub_progress_indicator_secure_connection_message
    }

    private fun observeScreenState() {
        verificationBankExternalGateViewModel.establishSecureConnectionEvent.observe(
            viewLifecycleOwner
        ) { event: Event<Any> -> onSecureConnectionEstablished(event) }
    }

    private fun onSecureConnectionEstablished(event: Event<Any>) {
        sharedViewModel.moveToExternalGate(verificationBankUrl)
    }
}