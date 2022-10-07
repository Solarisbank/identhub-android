package de.solarisbank.identhub.bank.feature.gateway

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.bank.domain.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class VerificationBankExternalGateViewModel(
    private val fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
    private val identificationLocalDataSource: IdentificationLocalDataSource,
    verificationBankUrl: String
) : ViewModel() {
    private val verificationBankUrlLiveDataEvent: MutableLiveData<String>
    private val establishSecureConnectionLiveDataEvent: MutableLiveData<Event<Any>> = MutableLiveData()
    private val verificationStatusLiveDataEvent: MutableLiveData<Result<Any>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        verificationBankUrlLiveDataEvent = MutableLiveData(verificationBankUrl)
    }

    val verificationBankUrlLiveData: LiveData<String>
        get() = verificationBankUrlLiveDataEvent

    val establishSecureConnectionEvent: LiveData<Event<Any>>
        get() {
            compositeDisposable.add(Observable.just(Event(Any()))
                    .delay(1, TimeUnit.SECONDS)
                    .subscribe { value: Event<Any> -> establishSecureConnectionLiveDataEvent.postValue(value) })
            return establishSecureConnectionLiveDataEvent
        }

    fun getVerificationResultLiveData(): LiveData<Result<Any>> {
        compositeDisposable.add(
            identificationLocalDataSource.obtainIdentificationDto()
                .flatMapCompletable { identification ->
                    fetchingAuthorizedIBanStatusUseCase.execute(identification.id)
                }.subscribe({ verificationStatusLiveDataEvent.postValue(Result.createEmptySuccess()) }
                ) { verificationStatusLiveDataEvent.postValue(Result.createUnknown(it)) }
        )
        return verificationStatusLiveDataEvent
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}