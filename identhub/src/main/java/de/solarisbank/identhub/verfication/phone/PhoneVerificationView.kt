package de.solarisbank.identhub.verfication.phone

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import de.solarisbank.identhub.R
import de.solarisbank.identhub.progress.DefaultCountDownTimer
import de.solarisbank.sdk.core_ui.data.dto.Customization
import de.solarisbank.sdk.data.entity.formatMinutesAndSeconds
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.customize
import java.util.concurrent.TimeUnit

class PhoneVerificationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val descriptionTextView: TextView
    private val codeInput: EditText
    private val resendTimerTextView: TextView
    private val resendButton: Button
    private val errorTextView: TextView

    private val countDownTimer = DefaultCountDownTimer(
        TimeUnit.SECONDS.toMillis(TIMER_DURATION_SECONDS),
        500
    )

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.view_phone_verification, this, true)

        descriptionTextView = findViewById(R.id.description)
        descriptionTextView.visibility = INVISIBLE
        codeInput = findViewById(R.id.codeInput)
        codeInput.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            eventListener?.invoke(PhoneVerificationViewEvent.CodeChanged(code = text.toString()))
        })
        resendTimerTextView = findViewById(R.id.resendTimer)
        resendButton = findViewById(R.id.resendButton)
        resendButton.setOnClickListener { eventListener?.invoke(PhoneVerificationViewEvent.ResendTapped) }
        errorTextView = findViewById(R.id.errorMessage)

        startTimer()
    }

    private val timerListener = object: DefaultCountDownTimer.OnTickListener {
        override fun onTick(millisUntilFinished: Long) {
            val time = formatMinutesAndSeconds(millisUntilFinished)
            resendTimerTextView.text = context.getString(R.string.contract_signing_code_expires, time)
        }

        override fun onFinish(millisUntilFinished: Long) {
            resendTimerTextView.text = ""
            eventListener?.invoke(PhoneVerificationViewEvent.TimerExpired)
        }
    }

    val code: String
        get() = codeInput.text.toString()

    var eventListener: ((PhoneVerificationViewEvent) -> Unit)? = null

    fun updatePhoneNumber(number: String?) {
        if (number != null) {
            descriptionTextView.text = context.getString(R.string.contract_signing_description, number)
            descriptionTextView.visibility = VISIBLE
        }
    }

    fun updateResendState(isResendButtonVisible: Boolean) {
        resendButton.isVisible = isResendButtonVisible
        resendTimerTextView.isVisible = !isResendButtonVisible
    }

    fun updateErrorMessage(isVisible: Boolean) {
        errorTextView.isVisible = isVisible
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
    }

    companion object {
        const val TIMER_DURATION_SECONDS = 10L
    }
}

sealed class PhoneVerificationViewEvent {
    object ResendTapped: PhoneVerificationViewEvent()
    object TimerExpired: PhoneVerificationViewEvent()
    data class CodeChanged(val code: String): PhoneVerificationViewEvent()
}