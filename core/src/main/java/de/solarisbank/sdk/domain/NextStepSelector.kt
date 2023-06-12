package de.solarisbank.sdk.domain

import android.annotation.SuppressLint
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import timber.log.Timber

/**
 * This class selects the next step based on partner settings.
 */

interface NextStepSelector {

    val initialConfigStorage: InitialConfigStorage

    @SuppressLint("BinaryOperationInTimber")
    fun selectNextStep(nextStep: String?, fallbackStep: String?): String? =
        selectNextStep(nextStep, fallbackStep, initialConfigStorage.get())
}

fun selectNextStep(nextStep: String?, fallbackStep: String?, initialConfig: IdenthubInitialConfig): String? {
    val defaultToFallbackStep =
        initialConfig
            .partnerSettings
            ?.defaultToFallbackStep
    return if (defaultToFallbackStep == true) {
        fallbackStep ?: nextStep
    } else {
        nextStep
    }
}