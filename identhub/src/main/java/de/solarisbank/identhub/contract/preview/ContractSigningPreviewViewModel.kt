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
import io.reactivex.disposables.CompositeDisposable
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
        compositeDisposable.add(getIdentificationUseCase.execute(Unit)
            .flatMapCompletable { result: Result<Identification> ->
                identificationResultLiveData.postValue(result)
                if (result.succeeded) {
                    val identification = result.data!!
                    fetchingAuthorizedIBanStatusUseCase.execute(identification.id)
                } else {
                    throw IllegalArgumentException("Couldn't refresh identification data")
                }
            }.subscribe {
                compositeDisposable.add(getDocumentsUseCase.execute(Unit)
                    .subscribe({ documentsResultLiveData.postValue(it) }, {
                        documentsResultLiveData.postValue(Result.createUnknown(it))
                    })
                )
            }
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
        compositeDisposable.add(fetchPdfUseCase.execute(document)
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