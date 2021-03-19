package de.solarisbank.identhub.error

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.activityViewModels
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.databinding.FragmentErrorMessageBinding
import de.solarisbank.identhub.identity.IdentityActivityViewModel

abstract class ErrorMessageFragment : BaseFragment() {
    private lateinit var alertDialog: AlertDialog
    protected val binding: FragmentErrorMessageBinding by viewBinding(FragmentErrorMessageBinding::inflate)
    protected val sharedViewModel: IdentityActivityViewModel by lazy { activityViewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
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
                .setTitle(R.string.identity_dialog_quit_process_title)
                .setMessage(R.string.identity_dialog_quit_process_message)
                .setPositiveButton(R.string.identity_dialog_quit_process_positive_button) { _, _ -> sharedViewModel.quitIdentity() }
                .setNegativeButton(R.string.identity_dialog_quit_process_negative_button) { _, _ -> }
                .create()
    }

    protected open fun initViews() {
        binding.apply {
            title.setText(this@ErrorMessageFragment.title)
            description.setText(message)
            cancelButton.setText(cancelButtonLabel)
            submitButton.setText(submitButtonLabel)
            cancelButton.setOnClickListener { alertDialog.show() }
        }
    }

    @get:StringRes
    protected abstract val cancelButtonLabel: Int

    @get:StringRes
    protected abstract val message: Int

    @get:StringRes
    protected abstract val title: Int

    @get:StringRes
    protected abstract val submitButtonLabel: Int

}