package de.solarisbank.identhub.session.feature.base

import android.os.Bundle
import androidx.activity.addCallback
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.di.IdentHubSessionReceiver
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.feature.base.BaseActivity
import timber.log.Timber

open class SessionNavigationActivity : BaseActivity() {

    private var identHubSessionReceiver: IdentHubSessionReceiver = IdentHub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackButtonBehavior()
    }

    protected fun quit(sessionStepResult: SessionStepResult) {
        Timber.d("quit, state : ${sessionStepResult}")
        identHubSessionReceiver.setSessionResult(sessionStepResult)
        finish()
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
                    quit(NaviDirection.VerificationFailureStepResult(-1))
                },
                tag = "BackButtonAlert"
            )
        }
    }
}