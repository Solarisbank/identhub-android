package de.solarisbank.identhub.session.main

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import org.koin.core.component.inject

open class NewBaseFragment: Fragment(), IdenthubKoinComponent {
    private val customizationRepository by inject<CustomizationRepository>()
    val customization by lazy(LazyThreadSafetyMode.NONE) { customizationRepository.get() }

    val navigator: Navigator?
    get() {
        return activity?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).mainCoordinator
        }
    }

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
        (activity as? MainActivity)?.showAlertFragment(title, message, positiveLabel, negativeLabel,
            positiveAction, negativeAction, cancelAction, tag
        )
    }

    fun showGenericAlertFragment(action: () -> Unit) {
        showAlertFragment(
            title = getString(R.string.identhub_generic_error_title),
            message = getString(R.string.identhub_generic_error_message),
            positiveAction = action
        )
    }
}