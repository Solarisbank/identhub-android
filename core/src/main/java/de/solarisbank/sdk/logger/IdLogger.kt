package de.solarisbank.sdk.logger

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.solarisbank.sdk.logger.domain.model.LogContent
import de.solarisbank.sdk.logger.domain.model.LogJson
import de.solarisbank.sdk.logger.domain.model.LogType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

class IdLogger private constructor(
) {
    companion object {
        @Volatile
        private var localList = ConcurrentLinkedQueue<LogJson>()
        private var appsDefaultExceptionHandler: Thread.UncaughtExceptionHandler? = null

        @Volatile
        private lateinit var instance: IdLogger
        private lateinit var timberLoggerTree: LoggerTree

        private var loggerUseCaseInstance: LoggerUseCase? = null
        private val IS_SEND_LOGS = false
        private val loggerDelayedHandler = Handler(Looper.getMainLooper())
        private const val bundlerDelay: Long = 5000
        private var loggerRunnable: Runnable? = null

        /**
         * will be called on the Start of the SDK to setup exception handler and basic logger.
         */
        @JvmStatic
        fun setupLogger(
        ): IdLogger {
            instance = IdLogger()
            appsDefaultExceptionHandler =
                Thread.getDefaultUncaughtExceptionHandler() //Save a instance of Host app's thread.
            timberLoggerTree = LoggerTree()
            Timber.plant(timberLoggerTree)
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                logJsonEvent(LogType.error, e.stackTrace.joinToString(separator = ","))

            }
            logJsonEvent(LogType.info, getDeviceInfo())

            return instance
        }

        /**
         * will be called on the exit of the SDK to restore teh exception handler.
         */
        @JvmStatic
        fun cleanLogger() {
            localList.also {
                if (it.isNotEmpty()) {
                    uploadLogs()
                    it.clear()
                }
            }
            appsDefaultExceptionHandler?.let { Thread.setDefaultUncaughtExceptionHandler(it) } //Setting the Host app's default thread back.
            Timber.uproot(timberLoggerTree)
        }

        private val logSuccess = { _: Boolean ->

        }

        private val logError = { _: Throwable ->

        }

        @SuppressLint("LogNotTimber") //using it for local logging, Can't use timber because , all timber logs are intercepted and logged here.
        @Synchronized
        private fun logJsonEvent(logType: String, log: String, category: String = "") {
            Log.d("IdLogger: Type->$logType", log)
            val jsonlog = LogJson(
                log,
                logType, category
            )

            localList.add(jsonlog)

            loggerUseCaseInstance?.let {
                spawnHandler()
            }
        }

        private fun spawnHandler() {
            if (loggerRunnable == null) {
                loggerRunnable = Runnable {
                    uploadLogs()
                    loggerRunnable = null;
                }

                loggerRunnable?.let {
                    loggerDelayedHandler.postDelayed(it, bundlerDelay)
                }
            }
        }


        @SuppressLint("CheckResult")
        private fun uploadLogs() {
            if (localList.isNotEmpty()) {
                val list = ArrayList<LogJson>()
                list.addAll(localList.toList()) //creating a new list form the Queue
                loggerUseCaseInstance?.invoke(LogContent(list))
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(logSuccess, logError)
                localList.clear()
            }
        }


        /**
         * @param log of type String
         * Log method for Navigation
         */
        fun logNav(log: String, category: String = "") {
            logJsonEvent(LogType.nav, log, category)
        }

        /**
         * @param log of type String
         * Log method for Event
         */
        fun logEvent(log: String, category: String = "") {
            logJsonEvent(LogType.event, log, category)
        }

        /**
         * @param log of type String
         * Log method for Action
         */
        fun logAction(log: String, category: String = "") {
            logJsonEvent(LogType.action, log, category)
        }

        /**
         * @param log of type String
         * Log method for Error
         */

        fun logError(log: String, category: String = "") {
            logJsonEvent(LogType.error, log, category)
        }

        /**
         * @param log of type String
         * Log method for Information
         */
        fun logInfo(log: String, category: String = "") {
            logJsonEvent(LogType.info, log, category)
        }

        /**
         * @param log of type String
         * Log method for Fault
         */
        fun logFault(log: String, category: String = "") {
            logJsonEvent(LogType.fault, log, category)
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




