package de.solarisbank.sdk.core.alert

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

class AlertDialogFragment: DialogFragment() {
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