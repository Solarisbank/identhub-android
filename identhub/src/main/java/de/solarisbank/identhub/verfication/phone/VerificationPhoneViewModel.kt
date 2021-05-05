package de.solarisbank.identhub.verfication.phone

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase
import de.solarisbank.identhub.event.ClickEvent
import de.solarisbank.identhub.progress.DefaultCountDownTimer
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class VerificationPhoneViewModel(
        private val authorizeVerificationPhoneUseCase: AuthorizeVerificationPhoneUseCase,
        private val confirmVerificationPhoneUseCase: ConfirmVerificationPhoneUseCase
) : ViewModel() {
    private val authorizeResultLiveData: MutableLiveData<Result<VerificationPhoneResponse>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val countDownTimeEventLiveData: MutableLiveData<Event<CountDownTime>> = MutableLiveData()
    private val clickEventRelay = BehaviorRelay.createDefault(ClickEvent())
    private var countDownTimer = DefaultCountDownTimer(TimeUnit.SECONDS.toMillis(COUNTER_TIME), TimeUnit.SECONDS.toMillis(INTERVAL_IN_SEC))
    private val confirmResultLiveData: MutableLiveData<Result<VerificationPhoneResponse>> = MutableLiveData()
    private val tickListener = object : DefaultCountDownTimer.OnTickListener {
        override fun onTick(millisUntilFinished: Long) {
            countDownTimeEventLiveData.postValue(Event(CountDownTime(millisUntilFinished)))
        }

        override fun onFinish(millisUntilFinished: Long) {
            countDownTimeEventLiveData.postValue(Event(CountDownTime(millisUntilFinished, true)))
        }
    }

    init {
        authorizeTanOnClick()
        countDownTimer.addListener(tickListener)
    }

    private fun authorizeTanOnClick() {
        compositeDisposable.add(
                clickEventRelay.doOnNext { authorizeResultLiveData.postValue(Result.Loading) }
                        .switchMap {
                            authorizeVerificationPhoneUseCase.execute(Unit)
                                    .toObservable()
                        }
                        .subscribe({ onAuthorizedReceived(it) }) { onAuthorizedError(it) })
    }

    private fun onAuthorizedError(throwable: Throwable) {
        stopTimer()
        authorizeResultLiveData.postValue(Result.createUnknown(throwable))
    }

    private fun onAuthorizedReceived(result: Result<VerificationPhoneResponse>) {
        if (result.succeeded) {
            startTimer()
        } else {
            stopTimer()
        }
        authorizeResultLiveData.postValue(result)
    }

    fun getAuthorizeResultLiveData(): LiveData<Result<VerificationPhoneResponse>> {
        return authorizeResultLiveData
    }

    fun getCountDownTimeEventLiveData(): LiveData<Event<CountDownTime>> {
        return countDownTimeEventLiveData
    }

    fun getConfirmResultLiveData(): LiveData<Result<VerificationPhoneResponse>> {
        return confirmResultLiveData
    }

    fun onSubmitButtonClicked(confirmToken: String) {
        stopTimer()
        confirmResultLiveData.postValue(Result.Loading)
        compositeDisposable.add(
                confirmVerificationPhoneUseCase.execute(confirmToken)
                        .subscribe(
                                { notifyConfirmResultChanged(it) },
                                { notifyConfirmResultChanged(Result.createUnknown(it)) }
                        )
        )
    }

    private fun notifyConfirmResultChanged(result: Result<VerificationPhoneResponse>) {
        confirmResultLiveData.postValue(result)
    }

    fun onSendNewCodeClicked() {
        clickEventRelay.accept(ClickEvent())
    }

    fun startTimer() {
        countDownTimer.start()
    }

    fun stopTimer() {
        countDownTimer.cancel()
    }

    override fun onCleared() {
        Log.d("addOnBackStackChanged", "onCleared " + javaClass.name)
        countDownTimer.removeListener(tickListener)
        countDownTimer.cancel()
        compositeDisposable.clear()
    }

    companion object {
        private const val INTERVAL_IN_SEC = 1L
        private const val COUNTER_TIME = 20L
        const val MIN_CODE_LENGTH = 6
    }
}