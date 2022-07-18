@file:Suppress("unused")

package de.solarisbank.sdk.logger

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.solarisbank.sdk.logger.domain.model.LogContent
import de.solarisbank.sdk.logger.domain.model.LogJson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class IdLogger private constructor(
) {

    enum class LogLevel(val value: Int) {
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        FAULT(5)
    }

    sealed class Category(val name: String) {
        object Default: Category("")
        object Nav: Category("Nav")
        object Api: Category("API")
        class Other(name: String) : Category(name)
    }

    companion object {
        private const val BUNDLER_DELAY = 5000L
        private const val TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        @Volatile
        private var localList = RWLockList(ArrayList())

        @Volatile
        private lateinit var instance: IdLogger

        private var loggerUseCaseInstance: LoggerUseCase? = null
        private val loggerDelayedHandler = Handler(Looper.getMainLooper())
        private var loggerRunnable: Runnable? = null
        private var shouldUploadLog = false
        private var localLogLevel = LogLevel.INFO
        private var remoteLogLevel = LogLevel.WARN
        private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }


        /**
         * will be called on the Start of the SDK to setup exception handler and basic logger.
         */
        fun setupLogger(
        ): IdLogger {
            instance = IdLogger()

            logEvent(LogLevel.INFO, getDeviceInfo())

            return instance
        }

        /**
         * will be called on the exit of the SDK to restore teh exception handler.
         */
        fun cleanLogger() {
            uploadLogs()
        }

        fun setLocalLogLevel(logLevel: LogLevel) {
            localLogLevel = logLevel
        }

        fun setRemoteLogLevel(logLevel: LogLevel) {
            remoteLogLevel = logLevel
        }

        private val logSuccess = { _: Boolean ->

        }

        private val logError = { _: Throwable ->

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
                "Identhub (${category.name})"
            } else {
                "Identhub"
            }

            when(logLevel) {
                LogLevel.DEBUG -> Log.d(tag, log)
                LogLevel.INFO -> Log.i(tag, log)
                LogLevel.WARN -> Log.w(tag, log)
                LogLevel.ERROR, LogLevel.FAULT -> Log.e(tag, log)
            }
        }

        private fun remoteLog(logLevel: LogLevel, log: String, category: Category) {
            if (logLevel.value < remoteLogLevel.value)
                return

            // TODO: Check timestamp and timezone
            val jsonLog = LogJson(
                detail = log,
                type = logLevel.name,
                category = category.name,
                timestamp = timeFormatter.format(Date())
            )

            if (!shouldUploadLog) {
                localList.add(jsonLog)

                loggerUseCaseInstance?.let {
                    spawnHandler()
                }
            }
        }

        private fun spawnHandler() {
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
            if (!shouldUploadLog)
                return

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

        fun inject(loggerUseCase: LoggerUseCase?) {
            if (loggerUseCase != null) {
                this.loggerUseCaseInstance = loggerUseCase
            }
        }
    }
}
