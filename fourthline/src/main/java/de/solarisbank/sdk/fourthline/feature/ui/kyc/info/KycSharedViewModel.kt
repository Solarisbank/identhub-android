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
import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import de.solarisbank.sdk.fourthline.domain.appliedDocuments
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.domain.toPersonDataStateDto
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.net.URI
import java.util.*


class KycSharedViewModel(
        private val savedStateHandle: SavedStateHandle,
        private val personDataUseCase: PersonDataUseCase,
        private val kycInfoUseCase: KycInfoUseCase,
        private val locationUseCase: LocationUseCase,
        private val ipObtainingUseCase: IpObtainingUseCase,
        private val deleteKycInfoUseCase: DeleteKycInfoUseCase
        ): ViewModel() {

    var kycURI: URI? = null

    private val _personDataStateLiveData = MutableLiveData<PersonDataStateDto>()
    val passingPossibilityLiveData = _personDataStateLiveData as LiveData<PersonDataStateDto>
    private val _supportedDocLiveData = MutableLiveData<PersonDataStateDto>()
    val supportedDocLiveData = _supportedDocLiveData as LiveData<PersonDataStateDto>
    private val locationBehaviorSubject = BehaviorSubject.create<Unit>()

    private val compositeDisposable = CompositeDisposable()

    init {
        subscribeLocationStates()
    }

    fun getKycDocument(): Document {
        return kycInfoUseCase.getKycDocument()
    }

    fun clearPersonDataCaches() {
        deleteKycInfoUseCase.clearPersonDataCaches()
    }

    fun fetchPersonDataAndIp(sessionId: String) {
        Timber.d("fetchPersonDataAndIp() 0")
        deleteKycInfoUseCase.clearPersonDataCaches()
        _supportedDocLiveData.value = PersonDataStateDto.UPLOADING
        compositeDisposable.add(
                Single.zip(
                    personDataUseCase.execute(sessionId).subscribeOn(Schedulers.io()),
                    ipObtainingUseCase.execute(Unit).subscribeOn(Schedulers.io()),
                    {personData, ip -> personData to ip})
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

    private fun subscribeLocationStates() {
        compositeDisposable.add(
            locationBehaviorSubject.switchMap {
                locationUseCase.getLocation().subscribeOn(Schedulers.io()).toObservable()
                    .doOnNext {
                        Timber.d("subscribeLocationStates() 0, doOnNext")
                        when (it) {
                            is LocationDto.SUCCESS -> {
                                Timber.d("fetchPersonDataAndLocation() 1")
                                kycInfoUseCase.updateKycLocation(it.location)
                                _supportedDocLiveData.postValue(_personDataStateLiveData.value)
                            }
                            else -> {
                                Timber.d("fetchPersonDataAndLocation() 2")
                                _supportedDocLiveData.postValue(it.toPersonDataStateDto())
                            }
                        }
                    }
                    .doOnError {
                        Timber.d("fetchPersonDataAndLocation() 3")
                        _supportedDocLiveData.postValue(PersonDataStateDto.LOCATION_FETCHING_ERROR)
                    }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }


    fun fetchPersonDataAndLocation() {
        Timber.d("fetchPersonDataAndLocation() 0")
        locationBehaviorSubject.onNext(Unit)
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
        Timber.d("onCleared()")
        compositeDisposable.clear()
        deleteKycInfoUseCase.clearPersonDataCaches()
        Timber.d("onCleared() 2")
        super.onCleared()
    }
}