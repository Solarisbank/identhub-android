package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel

class TermsAndConditionsFragment : FourthlineFragment() {

    private var submitButton: TextView? = null

    private val activityViewModel: FourthlineViewModel by lazy<FourthlineViewModel> {
        activityViewModels()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
                .also {
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
      submitButton!!.setOnClickListener { activityViewModel.navigateToWelcomeContainerFragment() }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        submitButton = null
        super.onDestroyView()
    }
}
