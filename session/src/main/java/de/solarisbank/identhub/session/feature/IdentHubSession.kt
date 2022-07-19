package de.solarisbank.identhub.session.feature

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.session.feature.di.IdentHubActivityComponent
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.*
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.logger.LoggerUseCase
import timber.log.Timber

/**
 * Due to compatibility of previous api and reducing of required logic implementation this class
 * encapsulates interaction with activity callbacks.
 *
 * This class has lifecycle as sdk initialization activity
 */
class IdentHubSession : ViewModelFactoryContainer {

    private var identificationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var identificationErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    private var lastCompetedStep: COMPLETED_STEP? = null

    private var confirmationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null

    private var paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null
    private var paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null

    private lateinit var activity: FragmentActivity

    private lateinit var activityComponent: IdentHubActivityComponent
    override lateinit var viewModelFactory: (FragmentActivity) -> ViewModelProvider.Factory
    private lateinit var viewModel: IdentHubSessionViewModel
    private var loggerUseCase: LoggerUseCase? = null

    private val isPaymentProcessAvailable: Boolean
        get() = paymentErrorCallback != null || paymentSuccessCallback != null

    internal var sessionUrl: String? = null

    /**
     * Sets required callbacks for indication sdk results and logic that requires
     * context
     */
    fun onCompletionCallback(
        fragmentActivity: FragmentActivity,
        successCallback: ((IdentHubSessionResult) -> Unit),
        errorCallback: ((IdentHubSessionFailure) -> Unit),
        confirmationSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null,
    ) {
        loadAppName(fragmentActivity)
        this.identificationErrorCallback = errorCallback
        this.identificationSuccessCallback = successCallback
        this.confirmationSuccessCallback = confirmationSuccessCallback
        initMainProcess(fragmentActivity)
    }

    fun enableRemoteLogging(logLevel: IdLogger.LogLevel) {
        IdLogger.setRemoteLogLevel(logLevel)
    }

    fun setLocalLoggingLevel(logLevel: IdLogger.LogLevel) {
        IdLogger.setLocalLogLevel(logLevel)
    }

    private fun initMainProcess(activity: FragmentActivity) {
        Timber.d("initMainProcess, fragmentActivity : $activity, this : $this")
        this.activity = activity
        activityComponent = IdentHubActivityComponent(this.activity)
        activityComponent.inject(this)

        viewModel = viewModelFactory.invoke(activity).create(IdentHubSessionViewModel::class.java)
        IdentHubSessionViewModel.INSTANCE!!.saveSessionId(sessionUrl)
        loggerUseCase = viewModel.getLoggerUseCase()
        IdLogger.inject(loggerUseCase)
        viewModel.initializationStateLiveData
            .observe(this.activity) { processInitializationStateResult(it) }
        viewModel.sessionStepResultLiveData.observe(activity) { processSessionResult(it) }
    }

    private fun processInitializationStateResult(result: Result<NavigationalResult<String>>) {
        Timber.d("processInitializationStateResult, result: $result ")

        if (result.isSuccess) {
            val navResult = result.getOrNull()!!
            if (navResult.data == FIRST_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 1")
                activity.startActivity(
                    toFirstStep(activity, navResult.nextStep!!, sessionUrl)
                )
            } else if (navResult.data == NEXT_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 2")
                val nextStep = toNextStep(activity, navResult.nextStep!!, sessionUrl)
                if (nextStep != null) {
                    activity.startActivity(nextStep)
                } else {
                    identificationErrorCallback!!.invoke(
                        IdentHubSessionFailure(message = "Session aborted", step = null)
                    )
                }
            } else {
                Timber.d("processInitializationStateResult 4")
            }
        } else {
            identificationErrorCallback!!.invoke(
                IdentHubSessionFailure(
                    //todo clear how fourthline simplified should be shown
                    message = "", null
                )
            )
        }
    }

    private fun processSessionResult(sessionStepResult: SessionStepResult) {
        //todo move logic to usecase, confirm both platform step result contract
        Timber.d("setSessionResult 0 : $sessionStepResult")
        IdLogger.debug("SessionResult: $sessionStepResult")
        when (sessionStepResult) {
            is NaviDirection.NextStepStepResult -> {
                Timber.d("setSessionResult 1")
                executeNextStepResult(
                    sessionStepResult.nextStep,
                    sessionStepResult.completedStep
                )
            }
            //todo check on usecase
            is NaviDirection.PaymentSuccessfulStepResult -> {
                if (isPaymentProcessAvailable) {
                    onResultSuccess(sessionStepResult)
                } else {
                    executeNextStepResult(
                        sessionStepResult.nextStep,
                        null
                    )
                }
            }
            is NaviDirection.VerificationSuccessfulStepResult -> {
                Timber.d("setSessionResult 2")
                onResultSuccess(sessionStepResult)
            }
            is NaviDirection.ConfirmationSuccessfulStepResult -> {
                Timber.d("setSessionResult 2.1")
                onResultSuccess(sessionStepResult)
            }

            is NaviDirection.VerificationFailureStepResult -> {
                Timber.d("setSessionResult 3")
                onResultFailure(IdentHubSessionFailure(
                    step = sessionStepResult.completedStep?.let {
                        COMPLETED_STEP.getEnum(sessionStepResult.completedStep)
                    }
                ))
            }
        }
    }

    private fun executeNextStepResult(nextStep: String?, completedStep: Int?) {
        Timber.d(" executeNextStepResult, nextStep : $nextStep")
        nextStep?.let {
            toNextStep(activity, it, sessionUrl)?.let { nextStepIntent ->
                activity.startActivity(nextStepIntent)
            }
        }?:run {
            //todo better to check in sender
            identificationErrorCallback!!.invoke(
                IdentHubSessionFailure(
                    message = "Session aborted",
                    step = COMPLETED_STEP.getEnum(completedStep ?: -1)
                )
            )
        }
    }

    /**
     * Sets successfull and faile callbacks for optional intermediate step
     * for partners that use payment checking
     */
    fun onPaymentCallback(
        paymentSuccessCallback: ((IdentHubSessionResult) -> Unit)? = null,
        paymentErrorCallback: ((IdentHubSessionFailure) -> Unit)? = null
    ) {
        this.paymentErrorCallback = paymentErrorCallback
        this.paymentSuccessCallback = paymentSuccessCallback
    }

    private fun onResultSuccess(sessionStepResult: SessionStepResult) {
        Timber.d("onResultSuccess, sessionStepResult : $sessionStepResult")

        when (sessionStepResult) {
            is NaviDirection.PaymentSuccessfulStepResult -> {
                paymentSuccessCallback?.let {
                    Timber.d("onResultSuccess 1")
                    it.invoke(
                        IdentHubSessionResult(
                            sessionStepResult.identificationId,
                            COMPLETED_STEP.getEnum(sessionStepResult.completedStep)
                        )
                ) } ?:run {
                    Timber.e("Payment callback is not set")
                }
            }
            is NaviDirection.VerificationSuccessfulStepResult -> {
                identificationSuccessCallback?.let {
                    Timber.d("onResultSuccess 2")
                    reset()
                    it.invoke(
                        IdentHubSessionResult(
                            sessionStepResult.identificationId,
                            COMPLETED_STEP.getEnum(sessionStepResult.completedStep)
                        )
                    )
                }
            }

            is NaviDirection.ConfirmationSuccessfulStepResult -> {
                confirmationSuccessCallback?.let {
                    Timber.d("onResultSuccess 3")
                    reset()
                    it.invoke(
                        IdentHubSessionResult(
                            sessionStepResult.identificationId,
                            COMPLETED_STEP.getEnum(sessionStepResult.completedStep)
                        )
                    )
                }
            }
        }
    }

    private fun onResultFailure(identHubSessionFailure: IdentHubSessionFailure) {
        Timber.d("onResultFailure")
        lastCompetedStep = identHubSessionFailure.step
        val paymentErrorCallback = this.paymentErrorCallback
        val identificationErrorCallback = this.identificationErrorCallback
        if (
            paymentErrorCallback != null &&
            identHubSessionFailure.step == COMPLETED_STEP.VERIFICATION_BANK
        ) {
            Timber.d("onResultFailure 1")
            paymentErrorCallback(identHubSessionFailure)
        } else if (identificationErrorCallback != null) {
            Timber.d("onResultFailure 2")
            reset()
            identificationErrorCallback(identHubSessionFailure)
        }
    }

    /**
     * Starts identification process
     */
    fun start() {
        Timber.d("start, paymentSuccessCallback != null : ${ paymentSuccessCallback != null }")
        viewModel.startIdentificationProcess(paymentSuccessCallback != null)
        IdLogger.debug("SDK started")
    }

    /**
     * Uses for resuming identification process in case
     * of optional payment checking for some partners
     */
    fun resume() {
        Timber.d("resume")
        viewModel.resumeIdentificationProcess()
    }

    /**
     * Resets identification process
     */
    internal fun reset() {
        Timber.d("reset(), this $this")
        viewModel.resetIdentificationProcess()
        IdLogger.clearLogger()
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
        //todo move to a repository
        @kotlin.jvm.JvmField
        var hasPhoneVerification: Boolean = true

        @kotlin.jvm.JvmField
        var appName: String = "Unknown"
    }
}

data class IdentHubSessionResult(val identificationId: String, val step: COMPLETED_STEP?) {
    init {
        IdLogger.debug("SessionResultCreated: ${step?.index}")
    }
}

data class IdentHubSessionFailure(val message: String? = null, val step: COMPLETED_STEP?) {
    init {
        IdLogger.debug("SessionFailureCreated. Step: ${step?.index}, Message: $message")
    }
}
