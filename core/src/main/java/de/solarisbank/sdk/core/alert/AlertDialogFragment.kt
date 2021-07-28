package de.solarisbank.sdk.core.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.result.Event

class AlertDialogFragment : DialogFragment() {
    private lateinit var alertViewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertViewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val tag = tag ?: TAG
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(args.getString(KEY_TITLE))
            .setMessage(args.getString(KEY_MESSAGE))
            .setPositiveButton(args.getString(KEY_POSITIVE_LABEL)) { _, _ ->
                alertViewModel.sendEvent(AlertEvent.Positive(tag))
            }
        args.getString(KEY_NEGATIVE_LABEL)?.let {
            builder.setNegativeButton(it) { _, _ ->
                alertViewModel.sendEvent(AlertEvent.Negative(tag))
            }
        }

        isCancelable = false
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    companion object {
        const val TAG = "AlertDialog"

        fun newInstance(
            title: String,
            message: String,
            positiveLabel: String,
            negativeLabel: String? = null,
        ): DialogFragment {
            return AlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_MESSAGE, message)
                    putString(KEY_POSITIVE_LABEL, positiveLabel)
                    putString(KEY_NEGATIVE_LABEL, negativeLabel)
                }
            }
        }

        private const val KEY_TITLE = "key_title"
        private const val KEY_MESSAGE = "key_message"
        private const val KEY_POSITIVE_LABEL = "key_positive_label"
        private const val KEY_NEGATIVE_LABEL = "key_negative_label"
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        alertViewModel.sendEvent(AlertEvent.Cancel(tag ?: TAG))
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
    tag: String = AlertDialogFragment.TAG,
    alertViewModel: AlertViewModel,
    lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager?
): DialogFragment? {
    var dialog: DialogFragment? = null

    alertViewModel.events.observe(lifecycleOwner, object: Observer<Event<AlertEvent>> {
        override fun onChanged(event: Event<AlertEvent>?) {
            event?.content?.let {
                if (it.tag != tag) {
                    return
                }
                when (it) {
                    is AlertEvent.Positive -> positiveAction.invoke()
                    is AlertEvent.Negative -> negativeAction?.invoke()
                    is AlertEvent.Cancel -> cancelAction?.invoke()
                }
                alertViewModel.events.removeObserver(this)
            }
        }
    })

    fragmentManager?.let {
        dialog = AlertDialogFragment.newInstance(
            title = title,
            message = message,
            positiveLabel = positiveLabel,
            negativeLabel = negativeLabel
        )
        dialog?.show(it, tag)
    }
    return dialog
}