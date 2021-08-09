package de.solarisbank.sdk.fourthline.feature.service.kyc.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.data.network.transformResult
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.di.DiLibraryComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.Type
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.net.URI

class KycUploadService : Service(), NextStepSelector {

    override lateinit var identityInitializationRepository: IdentityInitializationRepository
    internal lateinit var kycUploadUseCase: KycUploadUseCase
    internal lateinit var identificationPollingStatusUseCase: IdentificationPollingStatusUseCase

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
            binder.uploadingStatus.value = KycUploadStatusDto.GenericError
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
                                        KycUploadStatusDto.GenericError
                                killService()
                        }}
        ))
    }

    private fun pollKycProcessingResult() {
        compositeDisposable.add(identificationPollingStatusUseCase.pollIdentificationStatus()
            .map { NavigationalResult(it) }
            .transformResult()
            .doOnSuccess { result ->
                isRunning = false
                if(result.succeeded && result.data != null) {
                    Timber.d("pollKycProcessingResult(), 1 : $result ")
                    val data = result.data!!
                    val nextStep = selectNextStep(data.nextStep, data.fallbackStep)
                    if (data.status == Status.SUCCESSFUL.label) {
                        Timber.d("pollKycProcessingResult(), 2")
                        binder.uploadingStatus.postValue(
                            KycUploadStatusDto.FinishIdentSuccess(data.id)
                        )
                    } else if (data.status == Status.AUTHORIZATION_REQUIRED.label && nextStep != null) {
                        Timber.d("pollKycProcessingResult(), 3")
                        binder.uploadingStatus.postValue(
                            KycUploadStatusDto.ToNextStepSuccess(nextStep)
                        )
                    } else {
                        Timber.d("pollKycProcessingResult(), 4")
                        val providerStatusCode = data.providerStatusCode?.toIntOrNull()

                        if (providerStatusCode != null) {
                            Timber.d("pollKycProcessingResult(), 5")
                            if (providerStatusCode >= 4000) {
                                Timber.d("pollKycProcessingResult(), 6")
                                binder.uploadingStatus.postValue(KycUploadStatusDto.ProviderErrorFraud)
                            } else {
                                Timber.d("pollKycProcessingResult(), 7")
                                binder.uploadingStatus.postValue(
                                    KycUploadStatusDto.ProviderErrorNotFraud
                                )
                            }
                        } else {
                            Timber.d("pollKycProcessingResult(), 8")
                            binder.uploadingStatus.postValue(KycUploadStatusDto.GenericError)
                        }
                    }
                } else if(result is Result.Error){
                    Timber.d("pollKycProcessingResult(), 9 : ${result.type} ")
                    when (result.type) {
                        is Type.PreconditionFailed -> {
                            Timber.d("pollKycProcessingResult(), 10")
                            binder.uploadingStatus.postValue(KycUploadStatusDto.PreconditionsFailedError)
                        }
                        else -> {
                            //todo could be spread
                            Timber.d("pollKycProcessingResult(), 11")
                            binder.uploadingStatus.postValue(KycUploadStatusDto.GenericError)
                        }
                    }
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("pollKycProcessingResult(), 3")
                    isRunning = false
                    killService()
                },{ throwable ->
                    isRunning = false
                    Timber.e(throwable,"pollKycProcessingResult(), 4 : ${throwable.message}")
                    binder.uploadingStatus.value = KycUploadStatusDto.GenericError
                    killService()
                }
                ))
    }

    private fun killService() {
        Handler(Looper.getMainLooper()).postDelayed({
            stopForeground(true)
            stopSelf()
        }, 2000)
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
    val uploadingStatus = MutableLiveData<KycUploadStatusDto>()
}

