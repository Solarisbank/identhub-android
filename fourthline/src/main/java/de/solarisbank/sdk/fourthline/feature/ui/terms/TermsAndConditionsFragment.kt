package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class TermsAndConditionsFragment : NewBaseFragment() {

    private var submitButton: Button? = null
    private var checkBox: AppCompatCheckBox? = null
    private var termsAndConditionsTextView: TextView? = null
    private var privacyStatementTextView: TextView? = null
    private var condition1ImageView: ImageView? = null
    private var condition2ImageView: ImageView? = null

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_terms_and_condition, container, false)
                .also {
                    submitButton = it.findViewById(R.id.submitButton)
                    checkBox = it.findViewById(R.id.namirialTermsCheckBox)
                    checkBox?.setOnCheckedChangeListener { _, _ -> updateSubmitButtonState()}
                    termsAndConditionsTextView = it.findViewById(R.id.namirialTermsDescription)
                    termsAndConditionsTextView?.movementMethod = LinkMovementMethod.getInstance()
                    privacyStatementTextView = it.findViewById(R.id.fourthlinePrivacyDescription)
                    privacyStatementTextView?.movementMethod = LinkMovementMethod.getInstance()
                    condition1ImageView = it.findViewById(R.id.condition1ImageView)
                    condition2ImageView = it.findViewById(R.id.condition2ImageView)
                    customizeUI()
                    initView()
                }
    }

    private fun customizeUI() {
        condition1ImageView?.customize(customization)
        condition2ImageView?.customize(customization)
        submitButton?.customize(customization)
        checkBox?.customize(customization)
    }

    private fun updateSubmitButtonState() {
        submitButton!!.isEnabled = checkBox!!.isChecked
        submitButton?.buttonDisabled(!submitButton!!.isEnabled)
    }

    private fun initView() {
        updateSubmitButtonState()
        submitButton?.setOnClickListener { activityViewModel.onTermsOutcome() }
    }

    override fun onDestroyView() {
        submitButton = null
        checkBox = null
        termsAndConditionsTextView = null
        privacyStatementTextView = null
        condition1ImageView = null
        condition2ImageView = null
        super.onDestroyView()
    }
}
