package de.solarisbank.identhub.verfication.bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.core.activityViewModels

class VerificationBankIntroFragment : IdentHubFragment() {
    private val sharedViewModel: VerificationBankViewModel by lazy<VerificationBankViewModel> { activityViewModels() }
    private var startVerifyButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.identhub_fragment_verification_bank_intro, container, false)
        startVerifyButton = root.findViewById(R.id.startVerifyButton)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        startVerifyButton!!.setOnClickListener { sharedViewModel.navigateToVerficationBankIban() }
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        startVerifyButton = null
        super.onDestroyView()
    }
}
