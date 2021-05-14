package de.solarisbank.sdk.fourthline.feature.service.kyc.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.sdk.core.di.DiLibraryComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.net.URI

class KycUploadService : Service() {

    internal lateinit var kycUploadUseCase: KycUploadUseCase

    private val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    private val kycUploadServiceComponent: KycUploadServiceComponent by lazy {
        KycUploadServiceComponent.getInstance(libraryComponent)
    }

    private val binder = KycUploadServiceBinder()
    private lateinit var currentNotificationBuilder: NotificationCompat.Builder
    private val compositeDisposable = CompositeDisposable()
    private var isBound: Boolean = false

    override fun onCreate() {
        Timber.d("onCreate")
        super.onCreate()
        kycUploadServiceComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")
        startForegroundService()
        intent.getStringExtra(SESSION_URL_KEY)?.let {
            if (!it.endsWith("/")) {
                kycUploadUseCase.setBaseUrl("$it/")
            } else {
                kycUploadUseCase.setBaseUrl(it)
            }
        }
        (intent.getSerializableExtra(KYC_ZIPPER_URI) as? URI)?.let {
            Timber.d("uri : $it")
            var uploadableFile = File(it)
            Timber.d("uploadableFile.name: ${uploadableFile.name}")
            Timber.d("uploadableFile size = ${ uploadableFile.length() / 1024 / 1024 } mb")
            uploadKyc(uploadableFile)
        }?:run {
            binder.uploadingStatus.value = KycUploadFragment.UPLOAD_STATE.FAIL
        }
        Timber.d("onStartCommand2")
        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        Timber.d("TrackingService started.")
        initNotificationBuilder()
        initNotificationChanel()
        showUploadingNotification()
    }

    private fun initNotificationBuilder() {
        currentNotificationBuilder = NotificationCompat.Builder(this)
                .setContentTitle("Upload data")
                .setContentText("Sending personal data")
                .setSmallIcon(R.drawable.ic_baseline_upload_24)
                //todo implement intent processing in FourthlineActivity
//                .setContentIntent(PendingIntent.getActivity(
//                        this,
//                        0,
//                        Intent(this, FourthlineActivity::class.java)
//                        .apply { action = KycUploadServiceBinder.Companion.UPLOADING_STATUS.UPLOADING.name },
//                        0)
//                )
//                .setPriority(PRIORITY_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .setOngoing(true)
//                .setTicker("Ticker")
    }

    private fun initNotificationChanel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
            currentNotificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            currentNotificationBuilder.setCategory(NOTIFICATION_CHANNEL_ID)
        }
    }

    private fun showUploadingNotification() {
        val notification = currentNotificationBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun uploadKyc(uploadableFile: File) {
        isRunning = true
        binder.uploadingStatus.value = KycUploadFragment.UPLOAD_STATE.UPLOADING
        Timber.d("uploadKyc()")
        compositeDisposable.add(
                kycUploadUseCase
                        .uploadKyc(uploadableFile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ t1 ->
                            Timber.d("uploadKyc(), upload successful")
                            pollKycProcessingResult()
                        },{ t2 ->
                            isRunning = false
                            if (t2 != null) {
                                Timber.d("uploadKyc(), upload failed ${t2.message}")
                                binder.uploadingStatus.value =
                                        KycUploadFragment.UPLOAD_STATE.FAIL
                                killService()
                        }}
        ))
    }

    private fun pollKycProcessingResult() {
        compositeDisposable.add(kycUploadUseCase.pollIdentification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ identificationDto ->
                    isRunning = false
                    Timber.d("pollKycProcessingResult(), identificationDto : $identificationDto ")
                    if (identificationDto.status == "successful") {
                        binder.uploadingStatus.value =
                                KycUploadFragment.UPLOAD_STATE.SUCCESSFUL
                    } else {
                        binder.uploadingStatus.value =
                                KycUploadFragment.UPLOAD_STATE.FAIL
                    }
                    killService()
                },{ throwable ->
                    isRunning = false
                    if (throwable != null) {
                        Timber.d("pollKycProcessingResult(), error ${throwable.message}")
                        binder.uploadingStatus.value =
                                KycUploadFragment.UPLOAD_STATE.FAIL
                        killService()
                    }}
                ))
    }

    private fun killService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder {
        isBound = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    companion object {
        const val KYC_ZIPPER_URI = "KYC_ZIPPER_URI"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "KYC_UPLOAD_SERVICE_NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_CHANNEL_NAME = "KYC_UPLOAD_SERVICE_NOTIFICATION_CHANNEL_NAME"
        private var isRunning: Boolean = false
            get() = isRunning
    }

}

class KycUploadServiceBinder : Binder() {
    val uploadingStatus = MutableLiveData<KycUploadFragment.UPLOAD_STATE>()
}