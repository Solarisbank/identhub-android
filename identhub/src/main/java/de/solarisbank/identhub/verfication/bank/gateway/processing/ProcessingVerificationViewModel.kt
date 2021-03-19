package de.solarisbank.identhub.verfication.bank.gateway.processing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.shared.result.Event
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class ProcessingVerificationViewModel(
) : ViewModel() {
    private val processingVerificationLiveDataEvent: MutableLiveData<Event<Any>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val processingVerificationEvent: LiveData<Event<Any>>
        get() {
            compositeDisposable.add(Observable.just(Event(Any()))
                    .delay(1, TimeUnit.SECONDS)
                    .subscribe { processingVerificationLiveDataEvent.postValue(it) })
            return processingVerificationLiveDataEvent
        }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}