package de.solarisbank.identhub.verfication.bank.gateway

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.shared.result.Event
import de.solarisbank.shared.result.Result
import de.solarisbank.shared.result.data
import de.solarisbank.shared.result.succeeded
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class VerificationBankExternalGateViewModel(
        savedStateHandle: SavedStateHandle,
        private val fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
        private val getIdentificationUseCase: GetIdentificationUseCase
) : ViewModel() {
    private val verificationBankUrlLiveDataEvent: MutableLiveData<Event<String>>
    private val establishSecureConnectionLiveDataEvent: MutableLiveData<Event<Any>> = MutableLiveData()
    private val verificationStatusLiveDataEvent: MutableLiveData<Result<Any>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        val verificationBankUrl = savedStateHandle.get<String>(Identification.VERIFICATION_BANK_URL_KEY)
        Preconditions.checkNotNull(verificationBankUrl, "Verification bank url cannot be null")
        verificationBankUrlLiveDataEvent = MutableLiveData(Event(verificationBankUrl!!))
    }

    val verificationBankUrl: LiveData<Event<String>>
        get() = verificationBankUrlLiveDataEvent

    val establishSecureConnectionEvent: LiveData<Event<Any>>
        get() {
            compositeDisposable.add(Observable.just(Event(Any()))
                    .delay(1, TimeUnit.SECONDS)
                    .subscribe { value: Event<Any> -> establishSecureConnectionLiveDataEvent.postValue(value) })
            return establishSecureConnectionLiveDataEvent
        }

    fun getVerificationResultLiveData(): LiveData<Result<Any>> {
        compositeDisposable.add(getIdentificationUseCase.execute(Unit)
                .flatMapCompletable { result: Result<Identification> ->
                    if (result.succeeded) {
                        val identification = result.data!!
                        fetchingAuthorizedIBanStatusUseCase.execute(identification.id)
                    } else {
                        throw IllegalArgumentException("Couldn't get the verification result")
                    }
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