package de.solarisbank.sdk.feature.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.DiLibraryComponent
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var activityComponent: CoreActivityComponent

    private val alertViewModel: AlertViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AlertViewModel::class.java)
    }

    private var alertDialogFragment: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectMe()
        super.onCreate(savedInstanceState)
        initViewModel()
        initBackButtonBehavior()
    }

    protected open fun injectMe() {
        activityComponent = libraryComponent.activityComponent().create(this)
    }

    protected fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, intent.extras)
    }

    protected fun quit(bundle: Bundle?) {
        Timber.d("quit, bundle : ${bundle}")
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(
                        Intent(IDENTHUB_STEP_ACTION)
                                .apply { bundle?.let { putExtras(it) } }
                )
        finish()
    }

    companion object {
        const val IDENTHUB_STEP_ACTION = "IDENTHUB_STEP_ACTION"
    }

    private fun initBackButtonBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            showAlertFragment(
                title = getString(R.string.identity_dialog_quit_process_title),
                message = getString(R.string.identity_dialog_quit_process_message),
                positiveLabel = getString(R.string.identity_dialog_quit_process_positive_button),
                negativeLabel = getString(R.string.identity_dialog_quit_process_negative_button),
                positiveAction = {
                    //todo foresee intenttypes and avoid null bundle
                    Timber.d("Quit IdentHub SDK after back button pressed")
                    quit(null)
                },
                tag = "BackButtonAlert"
            )
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
        alertDialogFragment = de.solarisbank.sdk.feature.alert.showAlertFragment(
            title,
            message,
            positiveLabel,
            negativeLabel,
            positiveAction,
            negativeAction,
            cancelAction,
            tag,
            alertViewModel,
            this,
            supportFragmentManager
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialogFragment?.dismissAllowingStateLoss()
        alertDialogFragment = null
    }

}