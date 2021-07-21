package de.solarisbank.sdk.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.alert.AlertDialogFragment
import de.solarisbank.sdk.core.alert.AlertViewModel
import de.solarisbank.sdk.core.alert.showAlertFragment
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

abstract class BaseFragment : Fragment() {
    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val alertViewModel: AlertViewModel by lazy {
        ViewModelProvider(requireActivity())[AlertViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
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

    protected fun String.getStringRes(): String{
        return requireContext().resources.getString(
                requireContext().resources.getIdentifier(
                        this, "string", requireContext().packageName
                )
        )
    }

    override fun onDestroyView() {
        alertDialogFragment?.dismissAllowingStateLoss()
        alertDialogFragment = null
        super.onDestroyView()
    }
}