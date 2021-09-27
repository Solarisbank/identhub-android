package de.solarisbank.identhub.verfication.bank.gateway

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.sdk.domain.model.result.Event

class EstablishConnectionFragment : ProgressIndicatorFragment() {
    private var verificationBankExternalGateViewModel: VerificationBankExternalGateViewModel? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScreenState()
    }

    override fun getIconResource(): Int {
        return R.drawable.ic_shield_32_secondary
    }

    override fun getTitleResource(): Int {
        return R.string.progress_indicator_secure_connection_message
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun initViewModel() {
        super.initViewModel()
        verificationBankExternalGateViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(
                VerificationBankExternalGateViewModel::class.java
            )
    }

    private fun observeScreenState() {
        verificationBankExternalGateViewModel!!.establishSecureConnectionEvent.observe(
            viewLifecycleOwner, { event: Event<Any> -> onSecureConnectionEstablished(event) })
    }

    private fun onSecureConnectionEstablished(event: Event<Any>) {
        sharedViewModel.moveToExternalGate()
    }
}