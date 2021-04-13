package de.solarisbank.identhub.session

import androidx.fragment.app.FragmentActivity

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

        mainProcess?.start(sessionUrl)
    }

    fun resume() {
        if (lastCompetedStep == null) {
            throw IllegalStateException("Identity flow is not started yet")
        }
        if (!isPaymentProcessAvailable) {
            throw IllegalStateException("Resuming identification flow is not available")
        }
        if (mainProcess == null) {
            throw NullPointerException("You cannot resume the flow if the session is not started")
        }

        mainProcess?.start(sessionUrl)
    }

    fun stop() {
        mainProcess = null
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
        @kotlin.jvm.JvmField
        var hasPhoneVerification: Boolean = false
    }
}

data class IdentHubSessionResult(val identificationId: String, val step: IdentHubSession.Step?)
data class IdentHubSessionFailure(val message: String? = null, val step: IdentHubSession.Step?)
