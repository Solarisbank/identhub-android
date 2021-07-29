package de.solarisbank.sdk.fourthline.feature.ui.kyc.info

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.fourthline.domain.appliedDocuments
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.URI


class KycSharedViewModel(
        private val savedStateHandle: SavedStateHandle,
        private val personDataUseCase: PersonDataUseCase,
        private val kycInfoUseCase: KycInfoUseCase,
        private val locationUseCase: LocationUseCase
        ): ViewModel() {

    private val _personDataStateLiveData = MutableLiveData<PersonDataStateDto>()
    val passingPossibilityLiveData = _personDataStateLiveData as LiveData<PersonDataStateDto>
    private val _supportedDocLiveData = MutableLiveData<PersonDataStateDto>()
    val supportedDocLiveData = _supportedDocLiveData as LiveData<PersonDataStateDto>

    private val compositeDisposable = CompositeDisposable()

    fun getKycDocument(): Document {
        return kycInfoUseCase.getKycDocument()
    }

    fun fetchPersonData(sessionId: String) {
        Timber.d("fetchPersonData() 0")
        _supportedDocLiveData.value = PersonDataStateDto.UPLOADING
        compositeDisposable.add(
                personDataUseCase.execute(sessionId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { dtoResult ->
                                    Timber.d("fetchPersonData() 1")
                                    dtoResult.data?.let { dto ->
                                        val supportedDocs = dto.appliedDocuments()
                                        if (!supportedDocs.isNullOrEmpty()) {
                                            Timber.d("fetchPersonData() 2")
                                            kycInfoUseCase.updateWithPersonDataDto(dto)
                                            _personDataStateLiveData.value =
                                                    PersonDataStateDto.SUCCEEDED(supportedDocs)
                                        } else {
                                            Timber.d("fetchPersonData() 3")
                                            _personDataStateLiveData.value =
                                                    PersonDataStateDto.EMPTY_DOCS_LIST_ERROR
                                        }
                                    }
                                },
                                {
                                    Timber.e(it,"fetchPersonData() 4")
                                    _personDataStateLiveData.value = PersonDataStateDto.GENERIC_ERROR
                                }
                        )
        )
    }

    fun fetchPersonDataAndLocation() {
        Timber.d("fetchPersonDataAndLocation() 0")
        compositeDisposable.add(
                locationUseCase.getLocation()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    Timber.d("fetchPersonDataAndLocation() 1")
                                    kycInfoUseCase.updateKycLocation(it)
                                    _supportedDocLiveData.value = _personDataStateLiveData.value
                                },
                                {
                                    Timber.d("fetchPersonDataAndLocation() 2")
                                    _supportedDocLiveData.value = PersonDataStateDto.GENERIC_ERROR
                                }
                        )
        )
    }

    fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        kycInfoUseCase.updateKycWithSelfieScannerResult(result)
    }

    fun getSelfieResultCroppedBitmapLiveData(): LiveData<Bitmap> {
        return kycInfoUseCase.selfieResultCroppedBitmapLiveData
    }

    fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        kycInfoUseCase.updateKycInfoWithDocumentScannerStepResult(docType, result)
    }

    fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        kycInfoUseCase.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    fun getKycUriZip(applicationContext: Context): URI? {
        return kycInfoUseCase.getKycUriZip(applicationContext)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}