package de.solarisbank.identhub.session.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.GeneralCustomizer
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import org.koin.core.component.inject

abstract class BaseFragment: Fragment(), IdenthubKoinComponent {
    private val customizationRepository by inject<CustomizationRepository>()
    val customization by lazy(LazyThreadSafetyMode.NONE) { customizationRepository.get() }

    val navigator: Navigator?
    get() {
        return activity?.let {
            ViewModelProvider(it)[MainViewModel::class.java].mainCoordinator
        }
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = createView(inflater, container, savedInstanceState)
        customizeView(view)
        getKoin().getAll<GeneralCustomizer>().forEach {
            it.customize(javaClass.name, view, viewLifecycleOwner)
        }
        return view
    }
    abstract fun createView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View

    open fun customizeView(view: View) {}

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