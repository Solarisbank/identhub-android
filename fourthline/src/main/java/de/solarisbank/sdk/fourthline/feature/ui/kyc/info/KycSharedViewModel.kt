package de.solarisbank.sdk.fourthline.feature.ui.kyc.info

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.fourthline.data.FourthlineStorage
import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import de.solarisbank.sdk.fourthline.domain.appliedDocuments
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.domain.toPersonDataStateDto
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*


class KycSharedViewModel(
    private val personDataUseCase: PersonDataUseCase,
    private val kycInfoUseCase: KycInfoUseCase,
    private val locationUseCase: LocationUseCase,
    private val ipObtainingUseCase: IpObtainingUseCase,
    private val deleteKycInfoUseCase: DeleteKycInfoUseCase,
    private val fourthlineStorage: FourthlineStorage,
): ViewModel() {

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
        return runBlocking { kycInfoUseCase.getKycDocument() }
    }

    fun fetchPersonDataAndIp() {
        Timber.d("fetchPersonDataAndIp() 0")
        deleteKycInfoUseCase.clearPersonDataCaches()
        _supportedDocLiveData.value = PersonDataStateDto.UPLOADING
        compositeDisposable.add(
                Single.zip(
                    personDataUseCase.execute(Unit).subscribeOn(Schedulers.io()),
                    ipObtainingUseCase.execute(Unit).subscribeOn(Schedulers.io())
                ) { personData, ip -> personData to ip }
                    .doOnSuccess { pair ->
                            Timber.d("fetchPersonDataAndIp() 1, pair : $pair")
                            IdLogger.info("Person data and IP fetched.")
                            if(
                                    pair.first.succeeded
                                    && pair.second.succeeded
                                    && pair.first.data != null
                                    && pair.second.data != null
                            ) {

                                    val supportedDocs = pair.first.data!!.appliedDocuments()
                                    if (!supportedDocs.isNullOrEmpty()) {
                                        Timber.d("fetchPersonDataAndIp() 2")
                                        runBlocking { kycInfoUseCase.updateWithPersonDataDto(pair.first.data!!) }
                                        runBlocking { kycInfoUseCase.updateIpAddress(pair.second.data!!) }
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
                            IdLogger.error("Fetching Person data and IP Failed: $it")
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
                            is LocationResult.Success -> {
                                Timber.d("fetchPersonDataAndLocation() 1")
                                IdLogger.info("Fetching location success")
                                runBlocking { kycInfoUseCase.updateKycLocation(it.location) }
                                _supportedDocLiveData.postValue(_personDataStateLiveData.value)
                            }
                            else -> {
                                Timber.d("fetchPersonDataAndLocation() 2")
                                IdLogger.warn("Fetch location unsuccessful: ${it::class.java.name}")
                                _supportedDocLiveData.postValue(it.toPersonDataStateDto())
                            }
                        }
                    }
                    .doOnError {
                        Timber.d("fetchPersonDataAndLocation() 3")
                        IdLogger.error("Fetch location failed. ${it.message}")
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

    suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        kycInfoUseCase.updateKycWithSelfieScannerResult(result)
    }

    fun getSelfieResultCroppedBitmapLiveData(): LiveData<Bitmap> {
        return kycInfoUseCase.selfieResultCroppedBitmapLiveData
    }

    suspend fun updateKycInfoWithDocumentScannerStepResult(
            docType: DocumentType,
            result: DocumentScannerStepResult,
            isSecondaryDocument: Boolean
    ) {
        if (!isSecondaryDocument) {
            fourthlineStorage.isIdCardSelected = docType == DocumentType.ID_CARD
        }
        kycInfoUseCase.updateKycInfoWithDocumentScannerStepResult(docType, result, isSecondaryDocument)
    }

    suspend fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        kycInfoUseCase.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    fun createKycZip(applicationContext: Context): ZipCreationStateDto {
        return runBlocking { kycInfoUseCase.createKycZip(applicationContext) }
    }

    fun updateIssueDate(issueDate: Date) {
        runBlocking { kycInfoUseCase.updateIssueDate(issueDate) }
    }

    fun updateExpireDate(expireDate: Date) {
        runBlocking { kycInfoUseCase.updateExpireDate(expireDate) }
    }

    fun updateDocumentNumber(number: String) {
        runBlocking { kycInfoUseCase.updateDocumentNumber(number) }
    }

    override fun onCleared() {
        Timber.d("onCleared()")
        compositeDisposable.clear()
        deleteKycInfoUseCase.clearPersonDataCaches()
        Timber.d("onCleared() 2")
        IdLogger.info("KycSharedViewModel - Cleared")
        super.onCleared()
    }
}