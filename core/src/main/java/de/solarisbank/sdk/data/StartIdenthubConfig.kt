package de.solarisbank.sdk.data

import android.os.Parcelable
import de.solarisbank.sdk.logger.IdLogger
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartIdenthubConfig(
    val sessionUrl: String,
    val identificationId: String? = null,
    val localLogLevel: IdLogger.LogLevel = IdLogger.LogLevel.INFO,
    val remoteLogLevel: IdLogger.LogLevel = IdLogger.LogLevel.INFO
): Parcelable