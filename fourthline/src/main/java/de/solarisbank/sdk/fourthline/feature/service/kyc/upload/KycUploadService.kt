package de.solarisbank.sdk.fourthline.feature.service.kyc.upload

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.sdk.core.di.DiLibraryComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.net.URI

class KycUploadService : Service() {

    val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    private val kycUploadServiceComponent: KycUploadServiceComponent by lazy {
        KycUploadServiceComponent.getInstance(libraryComponent)
    }

    private val binder = KycUploadServiceBinder()
    private val compositeDisposable = CompositeDisposable()

    //todo change binder with FourthlinaViewModel
    internal lateinit var kycUploadUseCase: KycUploadUseCase

    override fun onCreate() {
        Timber.d("onCreate")
        super.onCreate()
        kycUploadServiceComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")
        //todo investigate possibility to change the Service to LifecylceService,
        //todo inject it to FourthlineActivityComponent and obtain non-context awared dependencies
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
        }
        Timber.d("onStartCommand2")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun uploadKyc(uploadableFile: File) {
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
                            if (t2 != null) {
                                Timber.d("uploadKyc(), upload failed ${t2.message}")
                                binder.uploadingStatus.value =
                                        KycUploadServiceBinder.Companion.UPLOADING_STATUS.ERROR
                                stopSelf()
                        }}
        ))
    }

    private fun pollKycProcessingResult() {
        compositeDisposable.add(kycUploadUseCase.pollIdentification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t1 ->
                    Timber.d("pollKycProcessingResult(), successful")
                    binder.uploadingStatus.value =
                            KycUploadServiceBinder.Companion.UPLOADING_STATUS.DONE
                    stopSelf()
                },{ t2 ->
                    if (t2 != null) {
                        Timber.d("pollKycProcessingResult(), error ${t2.message}")
                        binder.uploadingStatus.value =
                                KycUploadServiceBinder.Companion.UPLOADING_STATUS.ERROR
                        stopSelf()
                    }}
                ))
    }


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    companion object {
        const val KYC_ZIPPER_URI = "KYC_ZIPPER_URI"
    }

}

class KycUploadServiceBinder : Binder() {
     val uploadingStatus = MutableLiveData<UPLOADING_STATUS>()

    companion object {
        enum class UPLOADING_STATUS {
            UPLOADING, DONE, ERROR
        }
    }
}