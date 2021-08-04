package de.solarisbank.sdk.core.alert

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.core.result.Event

class AlertDialogFragment : BottomSheetDialogFragment() {
    private lateinit var alertViewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertViewModel = ViewModelProvider(requireActivity())[AlertViewModel::class.java]
        isCancelable = false

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_dialog, container, false)
        initView(view)
        return view
    }

    fun initView(view: View) {
        val args = requireArguments()
        val tag = tag ?: TAG

        view.findViewById<TextView>(R.id.title).apply { text = args.getString(KEY_TITLE) }
        view.findViewById<TextView>(R.id.message).apply { text = args.getString(KEY_MESSAGE) }
        view.findViewById<Button>(R.id.positiveButton).apply {
            text = args.getString(KEY_POSITIVE_LABEL)
            setOnClickListener {
                alertViewModel.sendEvent(AlertEvent.Positive(tag))
                dismiss()
            }
        }
        val negativeLabel = args.getString(KEY_NEGATIVE_LABEL)
        if (negativeLabel != null) {
            view.findViewById<Button>(R.id.negativeButton).apply {
                text = negativeLabel
                setOnClickListener {
                    alertViewModel.sendEvent(AlertEvent.Negative(tag))
                    dismiss()
                }
            }
        } else {
            view.findViewById<Button>(R.id.negativeButton).isVisible = false
            view.findViewById<View>(R.id.buttomSeparator).isVisible = false
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
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