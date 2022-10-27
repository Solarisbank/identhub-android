package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.net.URI

class KycUploadViewModel(
    private val kycUploadUseCase: KycUploadUseCase
    ) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _uploadingStatus = MutableLiveData<Event<KycUploadStatusDto>>()
    val uploadingStatus = _uploadingStatus as LiveData<Event<KycUploadStatusDto>>

    fun uploadKyc(fileUri: URI) {
        Timber.d("uploadKyc()")
        compositeDisposable.add(
                kycUploadUseCase
                        .uploadKyc(fileUri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ t1 ->
                            Timber.d("uploadKyc(), upload successful")
                            IdLogger.info("KYC upload result: ${t1::class.java.name}")
                            _uploadingStatus.value = Event(t1)
                        },{ t2 ->
                            if (t2 != null) {
                                Timber.e(t2,"uploadKyc(), upload failed")
                                IdLogger.error("KYC upload failed: ${t2.message}")
                                _uploadingStatus.value =
                                        Event(KycUploadStatusDto.GenericError)
                        }}
        ))
    }



    companion object {
        const val KYC_ZIPPER_URI = "KYC_ZIPPER_URI"
    }
}
