package de.solarisbank.identhub.qes.contract.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.qes.domain.FetchPdfUseCase
import de.solarisbank.identhub.qes.domain.GetDocumentsUseCase
import de.solarisbank.identhub.qes.domain.GetIdentificationUseCase
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.model.result.throwable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class ContractSigningPreviewViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val fetchPdfUseCase: FetchPdfUseCase,
    private val getIdentificationUseCase: GetIdentificationUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val fetchPdfFilesResultLiveData: MutableLiveData<Result<List<File>>> = MutableLiveData()
    private val viewState = MutableLiveData<ContractSigningPreviewState>()
    private val viewEvents = MutableLiveData<Event<ContractSigningPreviewEvent>>()

    fun state(): LiveData<ContractSigningPreviewState> = viewState
    fun events(): LiveData<Event<ContractSigningPreviewEvent>> = viewEvents

    init {
        val shouldShowTerms = getIdentificationUseCase.getInitialConfig()
            .firstStep == IdentificationStep.BANK_IBAN.destination
        viewState.value = ContractSigningPreviewState(
            documents = Result.Loading,
            shouldShowTerms = shouldShowTerms
        )
        refreshIdentificationData()
    }

    private fun refreshIdentificationData() {
        compositeDisposable.add(
            getIdentificationUseCase.execute(Unit)
                .flatMap { result: Result<IdentificationDto> ->
                    return@flatMap if (result.succeeded) {
                        getDocumentsUseCase.execute(Unit)
                    } else {
                        Single.error(
                            result.throwable
                            ?: IllegalArgumentException("Couldn't refresh identification data")
                        )
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.update { copy(documents = it) }
                }, {
                    viewState.update { copy(documents = Result.createUnknown(it)) }
                })
        )
    }

    fun getFetchPdfFilesResultLiveData(): LiveData<Result<List<File>>> {
        return fetchPdfFilesResultLiveData
    }

    private fun downloadDocument(document: DocumentDto) {
        compositeDisposable.add(
            fetchPdfUseCase.execute(document)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewEvents.postValue(Event(ContractSigningPreviewEvent.DocumentDownloaded(it)))
                    },
                    {
                        viewEvents.postValue(
                            Event(
                                ContractSigningPreviewEvent.DocumentDownloaded(Result.createUnknown(it))
                            )
                        )
                    })
        )
    }

    fun onAction(action: ContractSigningPreviewAction) {
        when (action) {
            is ContractSigningPreviewAction.DownloadDocument -> downloadDocument(action.document)
            is ContractSigningPreviewAction.Next -> viewEvents.postValue(Event(ContractSigningPreviewEvent.Done))
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
