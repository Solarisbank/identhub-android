package de.solarisbank.identhub.startup.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.Type
import de.solarisbank.sdk.domain.usecase.MobileNumberUseCase
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PhoneVerificationViewModel(
    private val phoneVerificationUseCase: PhoneVerificationUseCase,
    private val mobileNumberUseCase: MobileNumberUseCase,
    private val dispatchers: IdenthubDispatchers
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PhoneVerificationState())
    private val eventLiveData = MutableLiveData<Event<PhoneVerificationEvent>>()
    private val disposables = CompositeDisposable()

    init {
        authorize()
        fetchPhoneNumber()
    }

    fun state(): LiveData<PhoneVerificationState> = stateLiveData
    fun events(): LiveData<Event<PhoneVerificationEvent>> = eventLiveData

    fun onAction(action: PhoneVerificationAction) {
        when (action) {
            is PhoneVerificationAction.ResendCode -> resendCode()
            is PhoneVerificationAction.TimerExpired -> resendTimerExpired()
            is PhoneVerificationAction.CodeChanged -> codeChanged(action.code)
            is PhoneVerificationAction.Submit -> submitCode(action.code)
        }
    }

    private fun submitCode(code: String?) {
        if (code == null) {
            return
        }

        stateLiveData.update { copy(verifyResult = Result.Loading) }
        disposables.add(
            phoneVerificationUseCase.verifyToken(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stateLiveData.update {
                        copy(verifyResult = Result.Success(Unit))
                    }
                }, { throwable ->
                    Timber.d(throwable)
                    stateLiveData.update {
                        copy(verifyResult = Result.Error(Type.Unknown, throwable))
                    }
                })
        )
    }

    private fun resendCode() {
        stateLiveData.update {
            copy(shouldShowResend = false)
        }
        eventLiveData.postValue(Event(PhoneVerificationEvent.CodeResent))

        authorize()
    }

    private fun fetchPhoneNumber() {
        viewModelScope.launch {
            withContext(dispatchers.IO) {
                try {
                    val number = mobileNumberUseCase.fetchMobileNumber()
                    mobileNumberUseCase.maskPhoneNumber(number)
                } catch (throwable: Throwable) {
                    IdLogger.error("Failed to fetch phone number", throwable)
                    null
                }
            }?.let {
                stateLiveData.update { copy(phoneNumber = it) }
            }
        }
    }

    private fun authorize() {
        disposables.add(
            phoneVerificationUseCase.authorize()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, { throwable ->
                    Timber.d(throwable)
                })
        )
    }

    private fun resendTimerExpired() {
        stateLiveData.update {
            copy(shouldShowResend = true)
        }
    }

    private fun codeChanged(code: String) {
        if(stateLiveData.value?.verifyResult is Result.Error) {
            stateLiveData.update {
                copy(verifyResult = null)
            }
        }
        stateLiveData.update {
            copy(submitEnabled = code.length >= MIN_CODE_LENGTH)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    companion object {
        const val MIN_CODE_LENGTH = 6
    }
}