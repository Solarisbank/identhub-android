package de.solarisbank.identhub.session.feature.base

import de.solarisbank.identhub.session.feature.di.IdentHubSessionReceiver
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.logger.IdLogger
import timber.log.Timber

open class SessionNavigationActivity : BaseActivity() {

    private var identHubSessionReceiver: IdentHubSessionReceiver =
        IdentHubSessionViewModel.INSTANCE!!

    fun quit(sessionStepResult: SessionStepResult) {
        Timber.d("quit, state : $sessionStepResult")
        identHubSessionReceiver.setSessionResult(sessionStepResult)
        IdLogger.nav("IdentHub SDK was closed. $sessionStepResult")
        finish()
    }

    private fun initBackButtonBehavior() {
        showAlertFragment(
            title = getString(R.string.identhub_identity_dialog_quit_process_title),
            message = getString(R.string.identhub_identity_dialog_quit_process_message),
            positiveLabel = getString(R.string.identhub_identity_dialog_quit_process_positive_button),
            negativeLabel = getString(R.string.identhub_identity_dialog_quit_process_negative_button),
            positiveAction = {
                //todo foresee intenttypes and avoid null bundle
                Timber.d("Quit IdentHub SDK after back button pressed")
                quit(NaviDirection.VerificationFailureStepResult())
            },
            tag = "BackButtonAlert")
    }

    override fun onBackPressed() {
        initBackButtonBehavior()
    }
}