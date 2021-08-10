package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class KycUploadViewModel(
    private val kycUploadUseCase: KycUploadUseCase
    ) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _uploadingStatus = MutableLiveData<KycUploadStatusDto>()
    val uploadingStatus = _uploadingStatus as LiveData<KycUploadStatusDto>

    fun uploadKyc(uploadableFile: File) {
        Timber.d("uploadKyc()")
        compositeDisposable.add(
                kycUploadUseCase
                        .uploadKyc(uploadableFile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ t1 ->
                            Timber.d("uploadKyc(), upload successful")
                            _uploadingStatus.value = t1
                        },{ t2 ->
                            if (t2 != null) {
                                Timber.e(t2,"uploadKyc(), upload failed")
                                _uploadingStatus.value =
                                        KycUploadStatusDto.GenericError
                        }}
        ))
    }



    companion object {
        const val KYC_ZIPPER_URI = "KYC_ZIPPER_URI"
    }
}
