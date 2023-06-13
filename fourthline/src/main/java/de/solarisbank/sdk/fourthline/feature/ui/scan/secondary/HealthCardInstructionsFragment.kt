package de.solarisbank.sdk.fourthline.feature.ui.scan.secondary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class HealthCardInstructionsFragment : BaseFragment() {

    private var continueButton: Button? = null

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_health_card_scan_instructions, container, false)
            .also {
                continueButton = it.findViewById(R.id.btnStartSecondaryScan)
            }
    }

    override fun customizeView(view: View) {
        continueButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        continueButton!!.setOnClickListener { moveToDocScanFragment() }
    }

    private fun moveToDocScanFragment() {
        activityViewModel.onHealthCardInstructionsOutcome()
    }

    override fun onDestroyView() {
        continueButton = null
        super.onDestroyView()
    }
}