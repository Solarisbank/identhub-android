package de.solarisbank.sdk.fourthline.feature.ui.kyc.info

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.fourthline.base.FourthlineBaseViewModel
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.domain.appliedDocuments
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.URI


class KycSharedViewModel(
        private val savedStateHandle: SavedStateHandle,
        private val personDataUseCase: PersonDataUseCase,
        private val kycInfoUseCase: KycInfoUseCase
        ): FourthlineBaseViewModel() {

    private val _documentTypesLiveData = MutableLiveData<List<AppliedDocument>>()
    val documentTypesLiveData = _documentTypesLiveData as LiveData<List<AppliedDocument>>

    private val compositeDisposable = CompositeDisposable()

    fun getKycDocument(): Document {
        return kycInfoUseCase.getKycDocument()
    }

    var type: Int? = null

    fun fetchPersonData(intent: Intent) {
        compositeDisposable.add(personDataUseCase.execute(intent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Timber.d("${it.toString()}")
                            it.data?.let { dto ->
                                kycInfoUseCase.updateWithPersonDataDto(dto)
                                _documentTypesLiveData.value = dto.appliedDocuments()
                            }?:run{
                                _errorLiveData.value = "Identification failed"
                                Timber.e("fetchPersonData() dto is null")
                            }
                        },
                        {
                            Timber.e("fetchPersonData() ${it.message}")
                            _errorLiveData.value = "Identification failed"
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

    fun updateKycLocation(location: Location){
        kycInfoUseCase.updateKycLocation(location)
    }

    fun getKycUriZip(applicationContext: Context): URI? {
        return kycInfoUseCase.getKycUriZip(applicationContext)
    }

    fun sendCompletedResult(activity: FragmentActivity) {
        compositeDisposable.add(
                personDataUseCase
                        .getIdentificationId()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    val bundle = Bundle()
                                    bundle.putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.CONTRACT_SIGNING.index)
                                    bundle.putString(IdentHub.IDENTIFICATION_ID_KEY, it)
                                    activity.setResult(AppCompatActivity.RESULT_OK, Intent().apply { putExtras(bundle) })
                                    activity.finish()
                                },
                                {
                                    _errorLiveData.value = "Failed to send activity result"
                                }
                        )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}