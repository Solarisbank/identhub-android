package de.solarisbank.identhub.progress

import android.os.CountDownTimer

class DefaultCountDownTimer(
        millisInFuture: Long, countDownInterval: Long
) : CountDownTimer(millisInFuture, countDownInterval) {

    private var currentMillisUntilFinished: Long = 0
    private val listeners: ArrayList<OnTickListener> = arrayListOf()

    fun addListener(onTickListener: OnTickListener) {
        listeners.add(onTickListener)
    }

    fun removeListener(onTickListener: OnTickListener) {
        listeners.remove(onTickListener)
    }

    override fun onTick(millisUntilFinished: Long) {
        listeners.forEach { it.onTick(millisUntilFinished) }
        currentMillisUntilFinished = millisUntilFinished
    }

    override fun onFinish() {
        listeners.forEach { it.onFinish(currentMillisUntilFinished) }
    }

    interface OnTickListener {
        fun onTick(millisUntilFinished: Long)
        fun onFinish(millisUntilFinished: Long)
    }
}