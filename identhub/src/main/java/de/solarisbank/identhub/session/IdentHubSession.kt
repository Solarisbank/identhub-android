package de.solarisbank.identhub.session

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import de.solarisbank.identhub.router.COMPLETED_STEP
import timber.log.Timber

class IdentHubSession(private val sessionUrl: String) {
    private var mainProcess: IdentHubSessionObserver? = null
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

        mainProcess = IdentHubSessionObserver(fragmentActivity, ::onResultSuccess, ::onResultFailure, sessionUrl)
        fragmentActivity.lifecycle.addObserver(mainProcess!!)
    }

    fun onPaymentCallback(
            paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null,
            paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    ) {

        this.paymentErrorCallback = paymentErrorCallback
        this.paymentSuccessCallback = paymentSuccessCallback
    }

    private fun onResultSuccess(identHubSessionResult: IdentHubSessionResult) {
        lastCompetedStep = identHubSessionResult.step
        val paymentSuccessCallback = this.paymentSuccessCallback
        val identificationSuccessCallback = this.identificationSuccessCallback
        if (paymentSuccessCallback != null && (identHubSessionResult.step == COMPLETED_STEP.VERIFICATION_BANK)) {
            paymentSuccessCallback(identHubSessionResult)
        } else if (identificationSuccessCallback != null) {
            identificationSuccessCallback(identHubSessionResult)
        }
    }

    private fun onResultFailure(identHubSessionFailure: IdentHubSessionFailure) {
        lastCompetedStep = identHubSessionFailure.step
        val paymentErrorCallback = this.paymentErrorCallback
        val identificationErrorCallback = this.identificationErrorCallback
        if (paymentErrorCallback != null && identHubSessionFailure.step == COMPLETED_STEP.VERIFICATION_BANK) {
            paymentErrorCallback(identHubSessionFailure)
        } else if (identificationErrorCallback != null) {
            identificationErrorCallback(identHubSessionFailure)
        }
    }

    fun start() {
        if (mainProcess == null) {
            throw NullPointerException("You need to call create method first")
        }

        mainProcess?.obtainLocalIdentificationState()
    }

    fun resume() {
        if (mainProcess == null) {
            throw NullPointerException("You cannot resume the flow if the session is not started")
        }

        mainProcess?.obtainLocalIdentificationState()
    }

    fun stop() {
        mainProcess = null
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
    }
}

data class IdentHubSessionResult(val identificationId: String, val step: COMPLETED_STEP?)
data class IdentHubSessionFailure(val message: String? = null, val step: COMPLETED_STEP?)
