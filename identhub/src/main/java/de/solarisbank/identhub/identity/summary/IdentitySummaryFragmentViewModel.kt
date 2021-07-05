package de.solarisbank.identhub.identity.summary

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.core.data.model.StateUiModel
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class IdentitySummaryFragmentViewModel(
        private val deleteAllLocalStorageUseCase: DeleteAllLocalStorageUseCase,
        private val getDocumentsUseCase: GetDocumentsUseCase,
        private val fetchPdfUseCase: FetchPdfUseCase,
        private val identificationStepPreferences: IdentificationStepPreferences,
        private val getIdentificationUseCase: GetIdentificationUseCase,
        private val savedStateHandle: SavedStateHandle?) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val documentsResultLiveData: MutableLiveData<Result<List<Document>>> = MutableLiveData()
    private val fetchPdfFilesResultLiveData: MutableLiveData<Result<List<File>>> = MutableLiveData()
    private val fetchPdfResultLiveData: MutableLiveData<Result<de.solarisbank.sdk.core.Optional<File>>> = MutableLiveData()

    fun getFetchPdfResultLiveData(): LiveData<Result<de.solarisbank.sdk.core.Optional<File>>> {
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

    private val uiStateLiveData = MutableLiveData<StateUiModel<Bundle?>>()

    fun getUiState(): LiveData<StateUiModel<Bundle?>> {
        return uiStateLiveData
    }

    fun onSubmitButtonClicked() {
        compositeDisposable.add(
                getIdentificationUseCase
                        .execute(Unit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    if (it is Result.Success<*>) {
                                        Timber.d("onSubmitButtonClicked(), success;  result: $it ")
                                        val (id) = (it as Result.Success<Identification>).data
                                        val bundle = Bundle()
                                        bundle.putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.CONTRACT_SIGNING.index)
                                        bundle.putString(IdentHub.IDENTIFICATION_ID_KEY, id)
                                        uiStateLiveData.value = StateUiModel.Success(bundle)
                                    } else {
                                        Timber.d("onSubmitButtonClicked(), fail;  result: $it ")
                                    }
                                }, {
                            Timber.e(it, "Cannot load identification data")
                            uiStateLiveData.value = StateUiModel.Error(null)
                        }))
    }

    override fun onCleared() {
        identificationStepPreferences.clear()
        deleteAllLocalStorageUseCase.execute(Unit).subscribe()
        compositeDisposable.clear()
        super.onCleared()
    }
}