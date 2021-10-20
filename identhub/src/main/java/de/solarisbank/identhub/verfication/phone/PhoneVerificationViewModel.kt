package de.solarisbank.identhub.verfication.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.Type
import de.solarisbank.sdk.domain.model.result.data
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PhoneVerificationViewModel(
    private val phoneVerificationUseCase: PhoneVerificationUseCase
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PhoneVerificationState())
    private val eventLiveData = MutableLiveData<Event<PhoneVerificationEvent>>()
    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            phoneVerificationUseCase.fetchPhoneNumber()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    stateLiveData.update {
                        it.phoneNumber = result.data?.number
                    }
                }, {
                    Timber.d(it)
                })
        )
    }

    fun getStateLiveData(): LiveData<PhoneVerificationState> = stateLiveData

    fun getEventLiveData(): LiveData<Event<PhoneVerificationEvent>> = eventLiveData

    fun submit(code: String?) {
        if (code == null) {
            return
        }

        disposables.add(
            phoneVerificationUseCase.verifyToken(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stateLiveData.update {
                        it.verifyResult = Result.Success(Unit)
                    }
                }, { throwable ->
                    Timber.d(throwable)
                    stateLiveData.update {
                        it.verifyResult = Result.Error(Type.Unknown, throwable)
                    }
                })
        )
    }

    fun resendCode() {
        stateLiveData.update {
            it.showResendButton = false
        }
        eventLiveData.postValue(Event(PhoneVerificationEvent.ResentVerification))

        disposables.add(
            phoneVerificationUseCase.resendCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, { throwable ->
                    Timber.d(throwable)
                })
        )
    }

    fun resendTimerExpired() {
        stateLiveData.update {
            it.showResendButton = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun codeChanged(code: String) {
        if(stateLiveData.value?.verifyResult is Result.Error) {
            stateLiveData.update {
                it.verifyResult = null
            }
        }
        stateLiveData.update {
            it.submitEnabled = code.length >= MIN_CODE_LENGTH
        }
    }

    companion object {
        const val MIN_CODE_LENGTH = 6
    }
}

data class PhoneVerificationState(
    var phoneNumber: String? = null,
    var verifyResult: Result<Unit>? = null,
    var showResendButton: Boolean = false,
    var submitEnabled: Boolean = false
)

sealed class PhoneVerificationEvent {
    object ResentVerification: PhoneVerificationEvent()
}

fun <T> MutableLiveData<T>.update(reducer: (T) -> Unit) {
    value = value?.apply {
        reducer(this)
    }
}