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
import de.solarisbank.identhub.bank.BankModule
import de.solarisbank.identhub.bank.R
import de.solarisbank.identhub.bank.feature.VerificationBankViewModel
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize


abstract class ProgressIndicatorFragment : NewBaseFragment() {

    private val NO_RESOURCE = -1

    protected val sharedViewModel: VerificationBankViewModel by navGraphViewModels(BankModule.navigationId)

    protected var progressBar: ProgressBar? = null
    protected var title: TextView? = null
    protected var description: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(
            R.layout.identhub_fragment_verification_progress_indicator,
            container,
            false
        )
        progressBar = root.findViewById(R.id.icon)
        title = root.findViewById(R.id.title)
        description = root.findViewById(R.id.description)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    protected open fun initViews() {
        title?.setText(getTitleResource())
        val messageResource: Int = getMessageResource()
        if (messageResource != NO_RESOURCE) {
            description!!.setText(messageResource)
            description!!.visibility = View.VISIBLE
        }
        customizeUI()
    }

    protected fun customizeUI() {
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