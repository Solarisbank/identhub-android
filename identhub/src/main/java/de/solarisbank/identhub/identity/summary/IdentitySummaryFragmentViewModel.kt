package de.solarisbank.identhub.identity.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.base.Optional
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase
import de.solarisbank.shared.result.Result
import de.solarisbank.shared.result.data
import de.solarisbank.shared.result.succeeded
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class IdentitySummaryFragmentViewModel(
        private val deleteAllLocalStorageUseCase: DeleteAllLocalStorageUseCase,
        private val getDocumentsUseCase: GetDocumentsUseCase,
        private val fetchPdfUseCase: FetchPdfUseCase,
        private val identificationStepPreferences: IdentificationStepPreferences,
        savedStateHandle: SavedStateHandle?) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val documentsResultLiveData: MutableLiveData<Result<List<Document>>> = MutableLiveData()
    private val fetchPdfFilesResultLiveData: MutableLiveData<Result<List<File>>> = MutableLiveData()
    private val fetchPdfResultLiveData: MutableLiveData<Result<Optional<File>>> = MutableLiveData()

    fun getFetchPdfResultLiveData(): LiveData<Result<Optional<File>>> {
        return fetchPdfResultLiveData
    }

    fun getFetchPdfFilesResultLiveData(): LiveData<Result<List<File>>> {
        return fetchPdfFilesResultLiveData
    }

    fun getDocumentsResultLiveData(): LiveData<Result<List<Document>>> {
        compositeDisposable.add(getDocumentsUseCase.execute(Unit)
                .subscribe({ notifySummaryResultChanged(it) }, { notifySummaryResultChanged(Result.createUnknown(it)) }))
        return documentsResultLiveData
    }

    private fun notifySummaryResultChanged(result: Result<List<Document>>) {
        documentsResultLiveData.postValue(result)
    }

    fun onDocumentActionClicked(document: Document) {
        compositeDisposable.add(fetchPdfUseCase.execute(document)
                .subscribe({
                    fetchPdfResultLiveData.postValue(it)
                }, {
                    fetchPdfResultLiveData.postValue(Result.createUnknown(it))
                }))
    }

    fun onDownloadAllDocumentClicked(documents: List<Document>?) {
        compositeDisposable.add(Observable.fromIterable(documents)
                .flatMap { document: Document ->
                    fetchPdfUseCase.execute(document)
                            .toObservable()
                }
                .toList()
                .map { it.filter { it.succeeded } }
                .map { it.map { it.data }.filter { it!!.isPresent } }
                .map { it.map { it!!.get() } }
                .subscribe({
                    fetchPdfFilesResultLiveData.postValue(Result.Success(it))
                }, {
                    fetchPdfFilesResultLiveData.postValue(Result.createUnknown(it))
                }))
    }

    override fun onCleared() {
        identificationStepPreferences.clear()
        deleteAllLocalStorageUseCase.execute(Unit).subscribe()
        compositeDisposable.clear()
        super.onCleared()
    }
}