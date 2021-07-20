package de.solarisbank.sdk.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.alert.AlertDialogFragment
import de.solarisbank.sdk.core.alert.AlertEvent
import de.solarisbank.sdk.core.alert.AlertViewModel
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import timber.log.Timber

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
            cancelAction: () -> Unit,
            tag: String = AlertDialogFragment.TAG
    ) {

        val observer: Observer<AlertEvent> = Observer<AlertEvent> { event ->
            Timber.d("onChanged, event : $event")
            when (event) {
                is AlertEvent.Positive -> positiveAction.invoke()
                is AlertEvent.Negative -> negativeAction?.invoke()
                is AlertEvent.Cancel -> cancelAction.invoke()
            }
        }

        alertViewModel.events.observe(viewLifecycleOwner, observer)
        fragmentManager?.let {
            alertDialogFragment = AlertDialogFragment.newInstance(
                    title = title,
                    message = message,
                    positiveLabel = positiveLabel,
                    negativeLabel = negativeLabel
            )
            alertDialogFragment?.show(it, tag)
        }

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