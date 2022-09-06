package de.solarisbank.identhub.verfication.bank.gateway

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.session.IdentHub.Companion.VERIFICATION_BANK_URL_KEY
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.feature.di.internal.Preconditions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class VerificationBankExternalGateViewModel(
        savedStateHandle: SavedStateHandle,
        private val fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
        private val identificationLocalDataSource: IdentificationLocalDataSource
) : ViewModel() {
    private val verificationBankUrlLiveDataEvent: MutableLiveData<String>
    private val establishSecureConnectionLiveDataEvent: MutableLiveData<Event<Any>> = MutableLiveData()
    private val verificationStatusLiveDataEvent: MutableLiveData<Result<Any>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        val verificationBankUrl = savedStateHandle.get<String>(VERIFICATION_BANK_URL_KEY)
        Preconditions.checkNotNull(verificationBankUrl, "Verification bank url cannot be null")
        verificationBankUrlLiveDataEvent = MutableLiveData(verificationBankUrl!!)
    }

    val verificationBankUrl: LiveData<String>
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