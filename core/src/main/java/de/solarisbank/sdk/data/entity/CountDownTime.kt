package de.solarisbank.sdk.data.entity

import java.util.*
import java.util.concurrent.TimeUnit

class CountDownTime @JvmOverloads constructor(
        val currentValue: Long = 0,
        val isFinish: Boolean = false
)

fun CountDownTime.format(): String {
    return formatMinutesAndSeconds(currentValue)
}

fun formatMinutesAndSeconds(milliSeconds: Long): String {
    return String.format(Locale.getDefault(), "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60,
        TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60)
}