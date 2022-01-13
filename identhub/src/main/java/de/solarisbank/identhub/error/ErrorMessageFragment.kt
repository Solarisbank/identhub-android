package de.solarisbank.identhub.error

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.sdk.core.activityViewModels

abstract class ErrorMessageFragment : IdentHubFragment() {
    private lateinit var alertDialog: AlertDialog
    protected val sharedViewModel: IdentityActivityViewModel by lazy<IdentityActivityViewModel> { activityViewModels() }

    protected lateinit var title: TextView
    protected lateinit var description: TextView
    protected lateinit var cancelButton: Button
    protected lateinit var submitButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_error_message, container, false)
                .also {
                    title = it.findViewById(R.id.title)
                    description = it.findViewById(R.id.description)
                    cancelButton = it.findViewById(R.id.cancelButton)
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDialog()
    }

    private fun initDialog() {
        alertDialog = getDialog()
    }

    open fun getDialog(): AlertDialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.identhub_identity_dialog_quit_process_title)
                .setMessage(R.string.identhub_identity_dialog_quit_process_message)
                .setPositiveButton(R.string.identhub_identity_dialog_quit_process_positive_button) { _, _ -> sharedViewModel.quitIdentity() }
                .setNegativeButton(R.string.identhub_identity_dialog_quit_process_negative_button) { _, _ -> }
                .create()
    }

    protected open fun initViews() {
        title.setText(this@ErrorMessageFragment.titleLabel)
        description.setText(messageLabel)
        cancelButton.setText(cancelButtonLabel)
        submitButton.setText(submitButtonLabel)
        cancelButton.setOnClickListener { alertDialog.show() }
    }

    @get:StringRes
    protected abstract val cancelButtonLabel: Int

    @get:StringRes
    protected abstract val messageLabel: Int

    @get:StringRes
    protected abstract val titleLabel: Int

    @get:StringRes
    protected abstract val submitButtonLabel: Int

}