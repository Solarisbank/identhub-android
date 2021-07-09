package de.solarisbank.sdk.core

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.solarisbank.sdk.core.di.CoreActivityComponent
import de.solarisbank.sdk.core.di.DiLibraryComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var activityComponent: CoreActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        injectMe()
        super.onCreate(savedInstanceState)
        initViewModel()
        initBackButtonBehavior()
    }

    protected open fun injectMe() {
        activityComponent = libraryComponent.activityComponent().create(this)
    }

    protected open fun initViewModel() {
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
            AlertDialog.Builder(this@BaseActivity).apply {
                setTitle(R.string.identity_dialog_quit_process_title)
                setMessage(R.string.identity_dialog_quit_process_message)
                setPositiveButton(R.string.identity_dialog_quit_process_positive_button) { _, _ ->
                    Timber.d("Quit IdentHub SDK after back button pressed")
                    quit(null)
                }
                setNegativeButton(R.string.identity_dialog_quit_process_negative_button) { _, _ -> }
            }.show()

        }
    }

}