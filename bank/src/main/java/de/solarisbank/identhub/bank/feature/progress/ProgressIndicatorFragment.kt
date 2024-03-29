package de.solarisbank.identhub.bank.feature.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.navGraphViewModels
import de.solarisbank.identhub.bank.BankFlow
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.bank.feature.VerificationBankViewModel
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize


abstract class ProgressIndicatorFragment : BaseFragment() {

    private val NO_RESOURCE = -1

    protected val sharedViewModel: VerificationBankViewModel by navGraphViewModels(BankFlow.navigationId)

    protected var progressBar: ProgressBar? = null
    protected var title: TextView? = null
    protected var description: TextView? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.identhub_fragment_verification_progress_indicator,
            container,
            false
        )
        progressBar = root.findViewById(R.id.icon)
        title = root.findViewById(R.id.title)
        description = root.findViewById(R.id.description)
        initViews()
        return root
    }

    protected open fun initViews() {
        title?.setText(getTitleResource())
        val messageResource: Int = getMessageResource()
        if (messageResource != NO_RESOURCE) {
            description!!.setText(messageResource)
            description!!.visibility = View.VISIBLE
        }
    }

    override fun customizeView(view: View) {
        progressBar!!.customize(customization)
    }

    @StringRes
    protected open fun getMessageResource(): Int {
        return NO_RESOURCE
    }

    @StringRes
    protected abstract fun getTitleResource(): Int

    @DrawableRes
    protected open fun getIconResource(): Int {
        return NO_RESOURCE
    }
    
    override fun onDestroyView() {
        progressBar = null
        title = null
        description = null
        super.onDestroyView()
    }
    
}