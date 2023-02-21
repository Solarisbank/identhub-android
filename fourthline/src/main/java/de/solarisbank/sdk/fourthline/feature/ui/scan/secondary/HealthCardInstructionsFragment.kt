package de.solarisbank.sdk.fourthline.feature.ui.scan.secondary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R

class HealthCardInstructionsFragment : NewBaseFragment() {

    private var startbutton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_health_card_scan_instructions, container, false)
            .also {
                startbutton = it.findViewById(R.id.btnStartSecondaryScan)
                customizeUI()
            }
    }

    private fun customizeUI() {
        startbutton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        startbutton!!.setOnClickListener {  }
    }

    override fun onDestroyView() {
        startbutton = null
        super.onDestroyView()
    }
}