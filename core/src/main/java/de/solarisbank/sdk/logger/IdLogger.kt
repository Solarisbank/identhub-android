@file:Suppress("unused")

package de.solarisbank.sdk.logger

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.solarisbank.sdk.data.StartIdenthubConfig
import de.solarisbank.sdk.logger.domain.model.LogContent
import de.solarisbank.sdk.logger.domain.model.LogJson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class IdLogger private constructor(
) {

    enum class LogLevel(val value: Int) {
        DEBUG(10),
        INFO(20),
        WARN(30),
        ERROR(40),
        FAULT(50),
        NONE(1000)
    }

    sealed class Category(val name: String) {
        object Default: Category("")
        object Nav: Category("Nav")
        object Api: Category("API")
        object Fourthline: Category("Fourthline")
        class Other(name: String) : Category(name)
    }

    companion object {
        private const val BUNDLER_DELAY = 5000L
        private const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        private var localList = RWLockList(ArrayList())
        private var loggerUseCaseInstance: LoggerUseCase? = null
        private val loggerDelayedHandler = Handler(Looper.getMainLooper())
        private var loggerRunnable: Runnable? = null
        private var localLogLevel = LogLevel.INFO
        private var remoteLogLevel = LogLevel.INFO
        // remoteLoggingEnabled will be `null` until we fetch the config from server
        private var remoteLoggingEnabled: Boolean? = null
        private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        private val shouldStoreRemoteLogs: Boolean
        get() = remoteLoggingEnabled == null || remoteLoggingEnabled == true
        private val shouldUploadLogs: Boolean
        get() =  remoteLoggingEnabled == true && loggerUseCaseInstance != null


        /**
         * will be called on the Start of the SDK to setup exception handler and basic logger.
         */
        fun setupLogger() {
            logEvent(LogLevel.INFO, getDeviceInfo())
        }

        /**
         * will be called on the exit of the SDK
         */
        fun clearLogger() {
            uploadLogs()
        }

        fun setLocalLogLevel(logLevel: LogLevel) {
            localLogLevel = logLevel
        }

        fun setRemoteLogLevel(logLevel: LogLevel) {
            remoteLogLevel = logLevel
        }

        fun setRemoteLoggingEnabled(enabled: Boolean) {
            remoteLoggingEnabled = enabled
            if (enabled) {
                spawnHandler()
            }
        }

        private val logSuccess = { _: Boolean ->
            localLog(LogLevel.DEBUG, "Logs uploaded successfully", Category.Api)
        }

        private val logError = { t: Throwable ->
            localLog(LogLevel.DEBUG, "Logs upload failed. ${t.message}", Category.Api)
        }

        @SuppressLint("LogNotTimber") //using it for local logging, Can't use timber because , all timber logs are intercepted and logged here.
        @Synchronized
        private fun logEvent(logLevel: LogLevel, log: String, category: Category = Category.Default) {
            localLog(logLevel, log, category)
            remoteLog(logLevel, log, category)
        }

        @SuppressLint("LogNotTimber")
        private fun localLog(logLevel: LogLevel, log: String, category: Category) {
            if (logLevel.value < localLogLevel.value)
                return

            val tag = if (category != Category.Default) {
                "IdentHub (${category.name})"
            } else {
                "IdentHub"
            }

            when(logLevel) {
                LogLevel.DEBUG -> Log.d(tag, log)
                LogLevel.INFO -> Log.i(tag, log)
                LogLevel.WARN -> Log.w(tag, log)
                LogLevel.ERROR, LogLevel.FAULT -> Log.e(tag, log)
                LogLevel.NONE -> { /* Ignore */ }
            }
        }

        private fun remoteLog(logLevel: LogLevel, log: String, category: Category) {
            if (!shouldStoreRemoteLogs)
                return

            if (logLevel.value < remoteLogLevel.value)
                return

            val jsonLog = LogJson(
                detail = log,
                type = logLevel.name,
                category = category.name,
                timestamp = timeFormatter.format(Date())
            )

            localList.add(jsonLog)
            spawnHandler()
        }

        private fun spawnHandler() {
            if (!shouldUploadLogs)
                return

            if (loggerRunnable == null) {
                loggerRunnable = Runnable {
                    uploadLogs()
                    loggerRunnable = null
                }

                loggerRunnable?.let {
                    loggerDelayedHandler.postDelayed(it, BUNDLER_DELAY)
                }
            }
        }


        @SuppressLint("CheckResult")
        private fun uploadLogs() {
            val logs = localList.getAndClear()
            if (logs.isNotEmpty()) {
                loggerUseCaseInstance?.invoke(LogContent(logs))
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(logSuccess, logError)
            }
        }


        /**
         * @param message of type String
         * Log method for Navigation
         */
        fun nav(message: String) {
            logEvent(LogLevel.INFO, message, Category.Nav)
        }

        /**
         * @param message of type String
         * Log method for Debug
         */
        fun debug(message: String, category: Category = Category.Default) {
            logEvent(LogLevel.DEBUG, message, category)
        }

        /**
         * @param message of type String
         * Log method for Warn
         */
        fun warn(message: String, category: Category = Category.Default) {
            logEvent(LogLevel.WARN, message, category)
        }

        /**
         * @param message of type String
         * Log method for Error
         */

        fun error(message: String, category: Category = Category.Default) {
            logEvent(LogLevel.ERROR, message, category)
        }

        /**
         * @param message of type String
         * Log method for Information
         */
        fun info(message: String, category: Category = Category.Default) {
            logEvent(LogLevel.INFO, message, category)
        }

        /**
         * @param message of type String
         * Log method for Fault
         */
        fun fault(message: String, category: Category = Category.Default) {
            logEvent(LogLevel.FAULT, message, category)
        }

        // Fetch device information here
        private fun getDeviceInfo(): String {
            val manufacturer = Build.MANUFACTURER
            val device = Build.DEVICE
            val sdkVersion = Build.VERSION.SDK_INT
            val dateTime: Date = Calendar.getInstance().time
            return "Manufacturer: $manufacturer, " +
                    "\n Device: $device, " +
                    "\n SDK: $sdkVersion, " +
                    "\n Date&Time: $dateTime"
        }

        fun inject(loggerUseCase: LoggerUseCase?, startConfig: StartIdenthubConfig) {
            if (loggerUseCase != null) {
                this.loggerUseCaseInstance = loggerUseCase
            }
            localLogLevel = startConfig.localLogLevel
            remoteLogLevel = startConfig.remoteLogLevel
        }
    }
}
