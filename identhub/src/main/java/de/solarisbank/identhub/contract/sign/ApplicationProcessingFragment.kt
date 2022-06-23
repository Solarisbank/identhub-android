package de.solarisbank.identhub.contract.sign

import android.view.View
import de.solarisbank.identhub.R
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.progress.ProgressIndicatorFragment

class ApplicationProcessingFragment : ProgressIndicatorFragment() {

    override fun getTitleResource(): Int {
        return R.string.identhub_progress_indicator_precessing_application_title
    }

    override fun getMessageResource(): Int {
        return R.string.identhub_progress_indicator_precessing_application_message
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun initViews() {
        super.initViews()
        progressBar!!.setOnClickListener { v: View? -> }
    }

}