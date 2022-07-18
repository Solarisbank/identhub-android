package de.solarisbank.sdk.feature.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core_ui.data.dto.Customization
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.showAlertFragment
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.logger.IdLogger

abstract class BaseFragment : Fragment() {
    lateinit var assistedViewModelFactory: AssistedViewModelFactory
    lateinit var customizationRepository: CustomizationRepository
    val customization: Customization by lazy { customizationRepository.get() }

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val alertViewModel: AlertViewModel by lazy { activityViewModels() }

    companion object {
        const val privacyLink = "https://www.solarisbank.com/en/privacy-policy/"
        const val termsLink = "https://www.solarisbank.com/en/customer-information/"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IdLogger.nav("Fragment OnCreate ${this::class.java}")
    }

    protected open fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    private var alertDialogFragment: DialogFragment? = null

    fun showAlertFragment(
        title: String,
        message: String,
        positiveLabel: String = "Ok",
        negativeLabel: String? = null,
        positiveAction: () -> Unit,
        negativeAction: (() -> Unit)? = null,
        cancelAction: (() -> Unit)? = null,
        tag: String = AlertDialogFragment.TAG
    ) {
        alertDialogFragment = showAlertFragment(
            title,
            message,
            positiveLabel,
            negativeLabel,
            positiveAction,
            negativeAction,
            cancelAction,
            tag,
            alertViewModel,
            viewLifecycleOwner,
            fragmentManager
        )
    }

    fun showGenericAlertFragment(action: () -> Unit) {
        showAlertFragment(
                title = getString(R.string.identhub_generic_error_title),
                message = getString(R.string.identhub_generic_error_message),
                positiveAction = action
        )
    }

    override fun onDestroyView() {
        alertDialogFragment?.dismissAllowingStateLoss()
        alertDialogFragment = null
        IdLogger.nav("Fragment OnDestroy ${this::class.java}")
        super.onDestroyView()
    }
}