package de.solarisbank.identhub.session

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import de.solarisbank.identhub.router.COMPLETED_STEP
import timber.log.Timber

class IdentHubSession(private val sessionUrl: String) {
    private var identificationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var identificationErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    private var lastCompetedStep: COMPLETED_STEP? = null

    private var paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null

    val isPaymentProcessAvailable: Boolean
        get() = paymentErrorCallback != null || paymentSuccessCallback != null

    fun onCompletionCallback(
            fragmentActivity: FragmentActivity,
            successCallback: ((IdentHubSessionResult) -> Unit),
            errorCallback: ((IdentHubSessionFailure) -> Unit)
    ) {
        loadAppName(fragmentActivity)

        this.identificationErrorCallback = errorCallback
        this.identificationSuccessCallback = successCallback

        initMainProcess(fragmentActivity)
        fragmentActivity.lifecycle.addObserver(MAIN_PROCESS!!)
    }

    private fun initMainProcess(fragmentActivity: FragmentActivity) {
        synchronized(this) {
            if (MAIN_PROCESS == null) {
                MAIN_PROCESS = IdentHubSessionObserver(
                    fragmentActivity,
                    ::onResultSuccess,
                    ::onResultFailure,
                    sessionUrl
                )
            }
        }
    }

    fun onPaymentCallback(
            paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null,
            paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    ) {

        this.paymentErrorCallback = paymentErrorCallback
        this.paymentSuccessCallback = paymentSuccessCallback
    }

    private fun onResultSuccess(identHubSessionResult: IdentHubSessionResult) {
        Timber.d("onResultSuccess")
        lastCompetedStep = identHubSessionResult.step
        val paymentSuccessCallback = this.paymentSuccessCallback
        val identificationSuccessCallback = this.identificationSuccessCallback
        if (paymentSuccessCallback != null && (identHubSessionResult.step == COMPLETED_STEP.VERIFICATION_BANK)) {
            Timber.d("onResultSuccess 1")
            paymentSuccessCallback(identHubSessionResult)
        } else if (identificationSuccessCallback != null) {
            Timber.d("onResultSuccess 2")
            MAIN_PROCESS?.clearDataOnCompletion()
            identificationSuccessCallback(identHubSessionResult)
        }
    }

    private fun onResultFailure(identHubSessionFailure: IdentHubSessionFailure) {
        Timber.d("onResultFailure")
        lastCompetedStep = identHubSessionFailure.step
        val paymentErrorCallback = this.paymentErrorCallback
        val identificationErrorCallback = this.identificationErrorCallback
        if (paymentErrorCallback != null && identHubSessionFailure.step == COMPLETED_STEP.VERIFICATION_BANK) {
            Timber.d("onResultFailure 1")
            paymentErrorCallback(identHubSessionFailure)
        } else if (identificationErrorCallback != null) {
            Timber.d("onResultFailure 2")
            MAIN_PROCESS?.clearDataOnCompletion()
            identificationErrorCallback(identHubSessionFailure)
        }
    }

    fun start() {
        Timber.d("start")
        if (MAIN_PROCESS == null) {
            throw NullPointerException("You need to call create method first")
        }

        synchronized(this) {
            if (!STARTED) {
                MAIN_PROCESS?.obtainLocalIdentificationState()
                STARTED = true
            }
        }
    }

    fun resume() {
        Timber.d("resume")
        if (MAIN_PROCESS == null) {
            throw NullPointerException("You cannot resume the flow if the session is not started")
        }

        synchronized(this) {
            if (STARTED && !RESUMED) {
                MAIN_PROCESS?.obtainLocalIdentificationState()
                RESUMED = true
            }
        }
    }

    fun stop() {
        MAIN_PROCESS = null
    }

    private fun loadAppName(context: Context) {
        val packageManager = context.packageManager
        var appInfo: ApplicationInfo? = null

        try {
            appInfo = packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.tag("IdentHub").w(e, "Could not read application name")
        }

        if (appInfo != null) {
            appName =  packageManager.getApplicationLabel(appInfo).toString()
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        val ACTION_NEXT_STEP: Int = 99

        @kotlin.jvm.JvmField
        var hasPhoneVerification: Boolean = false

        @kotlin.jvm.JvmField
        var appName: String = "Unknown"

        @Volatile
        private var MAIN_PROCESS: IdentHubSessionObserver? = null

        private var STARTED: Boolean = false

        private var RESUMED: Boolean = false
    }
}

data class IdentHubSessionResult(val identificationId: String, val step: COMPLETED_STEP?)
data class IdentHubSessionFailure(val message: String? = null, val step: COMPLETED_STEP?)
