package de.solarisbank.identhub.contract.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.sdk.core.Optional
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.result.throwable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class ContractSigningPreviewViewModel(
        private val getDocumentsUseCase: GetDocumentsUseCase,
        private val fetchPdfUseCase: FetchPdfUseCase,
        private val getIdentificationUseCase: GetIdentificationUseCase,
        private val fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val documentsResultLiveData: MutableLiveData<Result<List<Document>>> = MutableLiveData()
    private val identificationResultLiveData: MutableLiveData<Result<Identification>> = MutableLiveData()
    private val fetchPdfResultLiveData: MutableLiveData<Result<Optional<File>>> = MutableLiveData()
    private val fetchPdfFilesResultLiveData: MutableLiveData<Result<List<File>>> = MutableLiveData()

    fun refreshIdentificationData() {
        compositeDisposable.add(
            getIdentificationUseCase.execute(Unit)
                .flatMap{ result: Result<Identification> ->
                    Timber.d("refreshIdentificationData() 0, result : $result ")
                    identificationResultLiveData.postValue(result)
                    return@flatMap if (result.succeeded) {
                        Timber.d("refreshIdentificationData() 1, result.data!!.id : ${result.data!!.id} ")
                        fetchingAuthorizedIBanStatusUseCase
                            .execute(result.data!!.id)
                            .andThen(getDocumentsUseCase.execute(Unit))
                    } else {
                        Single.error(
                            if (result.throwable != null) result.throwable
                            else IllegalArgumentException("Couldn't refresh identification data")
                        )
                    }
                }
                .doOnSuccess {
                    Timber.d("refreshIdentificationData() 2, it : $it ")
                    documentsResultLiveData.postValue(it)
                }
                .doOnError{
                    Timber.d("refreshIdentificationData() 3, it : $it ")
                    documentsResultLiveData.postValue(Result.createUnknown(it))
                }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("refreshIdentificationData() 4, it : $it ")
            }, {
                Timber.e(it, "refreshIdentificationData() 5")
            })
        )
    }

    fun getIdentificationData(): LiveData<Result<Identification>> {
        return identificationResultLiveData
    }

    fun getDocumentsResultLiveData(): LiveData<Result<List<Document>>> {
        return documentsResultLiveData
    }

    fun getFetchPdfFilesResultLiveData(): LiveData<Result<List<File>>> {
        return fetchPdfFilesResultLiveData
    }

    fun getFetchPdfResultLiveData(): LiveData<Result<de.solarisbank.sdk.core.Optional<File>>> {
        return fetchPdfResultLiveData
    }

    fun onDocumentActionClicked(document: Document) {
        compositeDisposable.add(
            fetchPdfUseCase.execute(document)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    fetchPdfResultLiveData.postValue(it)
                }, {
                    fetchPdfResultLiveData.postValue(Result.createUnknown(it))
                }))
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}