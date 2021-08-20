package de.solarisbank.identhub.session

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity
import de.solarisbank.identhub.domain.navigation.router.COMPLETED_STEP
import timber.log.Timber
import java.lang.ref.WeakReference

class IdentHubSession {
    private var identificationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var identificationErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    private var lastCompetedStep: COMPLETED_STEP? = null

    private var paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null

    val isPaymentProcessAvailable: Boolean
        get() = paymentErrorCallback != null || paymentSuccessCallback != null

    internal var sessionUrl: String? = null
        set(value) {
            field = value
            MAIN_PROCESS?.sessionUrl = value
        }

    @MainThread
    @Synchronized
    fun onCompletionCallback(
            fragmentActivity: FragmentActivity,
            successCallback: ((IdentHubSessionResult) -> Unit),
            errorCallback: ((IdentHubSessionFailure) -> Unit)
    ) {
        loadAppName(fragmentActivity)

        this.identificationErrorCallback = errorCallback
        this.identificationSuccessCallback = successCallback

        initMainProcess(fragmentActivity)
    }

    private fun initMainProcess(fragmentActivity: FragmentActivity) {
        Timber.d("initMainProcess, fragmentActivity : $fragmentActivity, this : $this")

        if(Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("This method must be called on the main thread!")
        }

        if (MAIN_PROCESS == null) {
            MAIN_PROCESS = IdentHubSessionObserver(
                ::onResultSuccess,
                ::onResultFailure
            )
        }
        MAIN_PROCESS?.fragmentActivity = fragmentActivity
        MAIN_PROCESS?.sessionUrl = sessionUrl
    }

    @Synchronized
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
            reset()
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
            reset()
            identificationErrorCallback(identHubSessionFailure)
        }
    }

    @Synchronized
    fun start() {
        Timber.d("start, MAIN_PROCESS : ${MAIN_PROCESS}, this $this")
        if (MAIN_PROCESS == null) {
            throw NullPointerException("You need to call create method first")
        }
        Timber.d("STARTED : $STARTED; RESUMED : $RESUMED")
        if (!STARTED) {
            MAIN_PROCESS?.obtainLocalIdentificationState()
            STARTED = true
            if (paymentSuccessCallback == null) {
                RESUMED = true
            }
        }
    }

    @Synchronized
    fun resume() {
        Timber.d("resume, MAIN_PROCESS : ${MAIN_PROCESS}, this $this")
        if (MAIN_PROCESS == null) {
            throw NullPointerException("You cannot resume the flow if the session is not started")
        }
        Timber.d("STARTED : $STARTED; RESUMED : $RESUMED")
        if (STARTED && !RESUMED) {
            MAIN_PROCESS?.obtainLocalIdentificationState()
            RESUMED = true
        }
    }

    internal fun reset() {
        Timber.d("reset(), MAIN_PROCESS : ${MAIN_PROCESS}, this $this")
        MAIN_PROCESS?.clearDataOnCompletion()
        identificationErrorCallback = null
        paymentSuccessCallback = null
        paymentErrorCallback = null
        identificationSuccessCallback = null
        STARTED = false
        RESUMED = false
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
