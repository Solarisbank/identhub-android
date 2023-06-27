package de.solarisbank.sdk.feature.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.core.view.isVisible
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.data.entity.formatMinutesAndSeconds
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.customize
import java.util.concurrent.TimeUnit

class PhoneVerificationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val descriptionTextView: TextView
    private val codeProgress: ProgressBar
    private val resendTimerTextView: TextView
    private val resendButton: Button
    private val errorTextView: TextView
    private var pinView = PinView(context)

    private val countDownTimer = DefaultCountDownTimer(
        TimeUnit.SECONDS.toMillis(TIMER_DURATION_SECONDS),
        500
    )

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.identhub_view_phone_verification, this, true)

        descriptionTextView = findViewById(R.id.description)
        descriptionTextView.visibility = INVISIBLE
        pinView = findViewById(R.id.tanInput)
        pinView.setPinViewEventListener(object : PinView.PinViewEventListener {
            override fun onDataEntered(pinView: PinView?, fromUser: Boolean) {
                eventListener?.invoke(PhoneVerificationViewEvent.CodeChanged(code = pinView!!.value))
            }
        })
        resendTimerTextView = findViewById(R.id.resendTimer)
        resendButton = findViewById(R.id.resendButton)
        resendButton.setOnClickListener { eventListener?.invoke(PhoneVerificationViewEvent.ResendTapped) }
        errorTextView = findViewById(R.id.errorMessage)
        codeProgress = findViewById(R.id.codeProgress)

        startTimer()
    }

    private val timerListener = object: DefaultCountDownTimer.OnTickListener {
        override fun onTick(millisUntilFinished: Long) {
            val time = formatMinutesAndSeconds(millisUntilFinished)
            resendTimerTextView.text = context.getString(R.string.identhub_verification_phone_request_code, time)
        }

        override fun onFinish(millisUntilFinished: Long) {
            resendTimerTextView.text = ""
            eventListener?.invoke(PhoneVerificationViewEvent.TimerExpired)
        }
    }

    val code: String
        get() = pinView.value

    var eventListener: ((PhoneVerificationViewEvent) -> Unit)? = null

    fun updateState(state: PhoneVerificationViewState) {
        if (state.phoneNumber != null) {
            descriptionTextView.text = context.getString(
                R.string.identhub_contract_phone_description,
                state.phoneNumber
            )
            descriptionTextView.visibility = VISIBLE
        }
        when (state.verifyResult) {
            is Result.Loading -> {
                codeProgress.visibility = VISIBLE
                errorTextView.visibility = GONE
            }
            is Result.Error -> {
                codeProgress.visibility = GONE
                errorTextView.visibility = VISIBLE
            }
            else -> {
                codeProgress.visibility = GONE
                errorTextView.visibility = GONE
            }
        }
        resendButton.isVisible = state.resendButtonVisible
        resendTimerTextView.isVisible = !state.resendButtonVisible
    }

    fun startTimer() {
        countDownTimer.cancel()
        countDownTimer.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        countDownTimer.addListener(timerListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        countDownTimer.removeListener(timerListener)
    }

    fun customize(customization: Customization) {
        resendButton.customize(customization, style = ButtonStyle.SecondaryNoBackground)
        codeProgress.customize(customization)
    }

    companion object {
        const val TIMER_DURATION_SECONDS = 20L
    }
}

data class PhoneVerificationViewState(
    var phoneNumber: String? = null,
    var verifyResult: Result<*>? = null,
    var resendButtonVisible: Boolean = false
)

sealed class PhoneVerificationViewEvent {
    object ResendTapped: PhoneVerificationViewEvent()
    object TimerExpired: PhoneVerificationViewEvent()
    data class CodeChanged(val code: String): PhoneVerificationViewEvent()
}