package de.solarisbank.identhub.session

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import de.solarisbank.identhub.data.dto.InitializationDto
import de.solarisbank.identhub.data.initialization.InitializeIdentificationApi
import de.solarisbank.identhub.di.network.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class IdentHubSession(private val sessionUrl: String) {
    private var mainProcess: IdentHubSessionObserver? = null
    private var identificationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var identificationErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    private var lastCompetedStep: Step? = null

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

        mainProcess = IdentHubSessionObserver(fragmentActivity, ::onResultSuccess, ::onResultFailure)
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
        if (paymentSuccessCallback != null && identHubSessionResult.step == Step.VERIFICATION_BANK) {
            paymentSuccessCallback(identHubSessionResult)
        } else if (identificationSuccessCallback != null) {
            identificationSuccessCallback(identHubSessionResult)
        }
    }

    private fun onResultFailure(identHubSessionFailure: IdentHubSessionFailure) {
        lastCompetedStep = identHubSessionFailure.step
        val paymentErrorCallback = this.paymentErrorCallback
        val identificationErrorCallback = this.identificationErrorCallback
        if (paymentErrorCallback != null && identHubSessionFailure.step == Step.VERIFICATION_BANK) {
            paymentErrorCallback(identHubSessionFailure)
        } else if (identificationErrorCallback != null) {
            identificationErrorCallback(identHubSessionFailure)
        }
    }

    fun start() {
        if (mainProcess == null) {
            throw NullPointerException("You need to call create method first")
        }

        mainProcess?.start(sessionUrl, "bank/iban")
    }

    fun resume() {
        if (mainProcess == null) {
            throw NullPointerException("You cannot resume the flow if the session is not started")
        }

        mainProcess?.start(sessionUrl, "qes")
    }

    fun stop() {
        mainProcess = null
    }

    private fun withInitialization(onSuccess: (InitializationDto) -> Unit) {
        val retrofitFactory = NetworkModule.provideSimpleRetrofit(sessionUrl)
        val api = retrofitFactory.create(InitializeIdentificationApi::class.java)

        Single.defer { api.getInitialization() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, { t ->
                    Timber.e(t)
                })
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

    enum class Step(val index: Int) {
        VERIFICATION_PHONE(1), VERIFICATION_BANK(2), CONTRACT_SIGNING(3);

        companion object {
            fun getEnum(index: Int): Step? {
                for (step in Step.values()) {
                    if (step.index == index) {
                        return step
                    }
                }
                return null
            }
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        val ACTION_NEXT_STEP: Int = 99

        @kotlin.jvm.JvmStatic
        val NEXT_STEP: String = "NEXT_STEP"

        @kotlin.jvm.JvmField
        var hasPhoneVerification: Boolean = false

        @kotlin.jvm.JvmField
        var appName: String = "Unknown"
    }
}

data class IdentHubSessionResult(val identificationId: String, val step: IdentHubSession.Step?)
data class IdentHubSessionFailure(val message: String? = null, val step: IdentHubSession.Step?)
