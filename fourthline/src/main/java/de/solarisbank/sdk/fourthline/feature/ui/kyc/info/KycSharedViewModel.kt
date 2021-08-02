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
import de.solarisbank.identhub.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.fourthline.domain.appliedDocuments
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.URI
import java.util.*


class KycSharedViewModel(
        private val savedStateHandle: SavedStateHandle,
        private val personDataUseCase: PersonDataUseCase,
        private val kycInfoUseCase: KycInfoUseCase,
        private val locationUseCase: LocationUseCase,
        private val ipObtainingUseCase: IpObtainingUseCase
        ): ViewModel() {

    var kycURI: URI? = null

    private val _personDataStateLiveData = MutableLiveData<PersonDataStateDto>()
    val passingPossibilityLiveData = _personDataStateLiveData as LiveData<PersonDataStateDto>
    private val _supportedDocLiveData = MutableLiveData<PersonDataStateDto>()
    val supportedDocLiveData = _supportedDocLiveData as LiveData<PersonDataStateDto>

    private val compositeDisposable = CompositeDisposable()

    fun getKycDocument(): Document {
        return kycInfoUseCase.getKycDocument()
    }

    fun fetchPersonDataAndIp(sessionId: String) {
        Timber.d("fetchPersonDataAndIp() 0")
        _supportedDocLiveData.value = PersonDataStateDto.UPLOADING
        compositeDisposable.add(
                Single.zip(personDataUseCase.execute(sessionId),
                        ipObtainingUseCase.execute(Unit), {personData, ip -> personData to ip})
                        .doOnSuccess { pair ->
                            Timber.d("fetchPersonDataAndIp() 1, pair : $pair")
                            if(
                                    pair.first.succeeded
                                    && pair.second.succeeded
                                    && pair.first.data != null
                                    && pair.second.data != null
                            ) {

                                    val supportedDocs = pair.first.data!!.appliedDocuments()
                                    if (!supportedDocs.isNullOrEmpty()) {
                                        Timber.d("fetchPersonDataAndIp() 2")
                                        kycInfoUseCase.updateWithPersonDataDto(pair.first.data!!)
                                        kycInfoUseCase.updateIpAddress(pair.second.data!!)
                                        _personDataStateLiveData.postValue(
                                                PersonDataStateDto.SUCCEEDED(supportedDocs)
                                        )
                                    } else {
                                        Timber.d("fetchPersonDataAndIp() 3")
                                        _personDataStateLiveData.postValue(
                                                PersonDataStateDto.EMPTY_DOCS_LIST_ERROR
                                        )
                                    }
                            } else {
                                Timber.d("fetchPersonDataAndIp() 4")
                                _personDataStateLiveData.postValue(
                                        PersonDataStateDto.GENERIC_ERROR
                                )
                            }
                        }
                        .doOnError {
                            Timber.e(it, "fetchPersonDataAndIp() 5")
                            _personDataStateLiveData.value = PersonDataStateDto.GENERIC_ERROR
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
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

    fun createKycZip(applicationContext: Context): ZipCreationStateDto {
        return kycInfoUseCase.createKycZip(applicationContext)
    }

    fun updateIssueDate(issueDate: Date) {
        kycInfoUseCase.updateIssueDate(issueDate)
    }

    fun updateExpireDate(expireDate: Date) {
        kycInfoUseCase.updateExpireDate(expireDate)
    }

    fun updateDocumentNumber(number: String) {
        kycInfoUseCase.updateDocumentNumber(number)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}