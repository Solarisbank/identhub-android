package de.solarisbank.identhub.contract.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.event.ClickEvent
import de.solarisbank.identhub.progress.DefaultCountDownTimer
import de.solarisbank.identhub.verfication.phone.CountDownTime
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class ContractSigningViewModel(
        savedStateHandle: SavedStateHandle?,
        private val authorizeContractSignUseCase: AuthorizeContractSignUseCase,
        private val confirmContractSignUseCase: ConfirmContractSignUseCase,
        private val getIdentificationUseCase: GetIdentificationUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val countDownTimeEventLiveData: MutableLiveData<Event<CountDownTime>> = MutableLiveData()
    private var countDownTimer = DefaultCountDownTimer(TimeUnit.SECONDS.toMillis(COUNTER_TIME), TimeUnit.SECONDS.toMillis(INTERVAL_IN_SEC))
    private val clickEventRelay = BehaviorRelay.createDefault(ClickEvent())
    private val authorizeResultLiveData: MutableLiveData<Result<Any>> = MutableLiveData<Result<Any>>()
    private val confirmResultLiveData: MutableLiveData<Result<Any>> = MutableLiveData<Result<Any>>()
    private val identificationResultLiveData: MutableLiveData<Result<Identification>> = MutableLiveData<Result<Identification>>()

    private val tickListener = object : DefaultCountDownTimer.OnTickListener {
        override fun onTick(millisUntilFinished: Long) {
            countDownTimeEventLiveData.postValue(Event(CountDownTime(millisUntilFinished)))
        }

        override fun onFinish(millisUntilFinished: Long) {
            countDownTimeEventLiveData.postValue(Event(CountDownTime(millisUntilFinished, true)))
        }
    }

    init {
        countDownTimer.addListener(tickListener)
        authorizeDocumentOnClick()
    }

    private fun authorizeDocumentOnClick() {
        compositeDisposable.add(
                clickEventRelay.doOnNext { onAuthorizedLoading() }
                        .switchMapCompletable {
                            authorizeContractSignUseCase.execute(Unit)
                                    .onErrorResumeNext { throwable: Throwable ->
                                        onAuthorizedError(throwable)
                                        Completable.complete()
                                    }
                        }
                        .subscribe({ onAuthorizedSucceed() }, { onAuthorizedError(it) }))
    }

    private fun onAuthorizedLoading() {
        authorizeResultLiveData.postValue(Result.Loading)
    }

    private fun onAuthorizedSucceed() {
        startTimer()
        authorizeResultLiveData.postValue(Result.createEmptySuccess())
    }

    private fun onAuthorizedError(throwable: Throwable) {
        authorizeResultLiveData.postValue(Result.createUnknown(throwable))
    }

    fun getAuthorizeResultLiveData(): MutableLiveData<Result<Any>> {
        return authorizeResultLiveData
    }

    fun getConfirmResultLiveData(): MutableLiveData<Result<Any>> {
        return confirmResultLiveData
    }

    fun getIdentificationResultLiveData(): MutableLiveData<Result<Identification>> {
        compositeDisposable.add(getIdentificationUseCase.execute(Unit)
                .subscribe(
                        { onDataSucceed(it) },
                        { onDataError(it) }
                )
        )
        return identificationResultLiveData
    }

    private fun onDataError(throwable: Throwable) {
        identificationResultLiveData.postValue(Result.createUnknown(throwable))
    }

    private fun onDataSucceed(result: Result<Identification>) {
        identificationResultLiveData.postValue(result)
    }

    fun getCountDownTimeEventLiveData(): LiveData<Event<CountDownTime>> {
        return countDownTimeEventLiveData
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

    fun onSubmitButtonClicked(confirmToken: String?) {
        stopTimer()
        confirmResultLiveData.postValue(Result.Loading)
        compositeDisposable.add(confirmContractSignUseCase.execute(confirmToken)
                .subscribe(
                        { onConfirmationSucceed() },
                        { onConfirmationError(it) }
                )
        )
    }

    private fun onConfirmationSucceed() {
        confirmResultLiveData.postValue(Result.createEmptySuccess())
    }

    private fun onConfirmationError(throwable: Throwable) {
        confirmResultLiveData.postValue(Result.createUnknown(throwable))
    }

    override fun onCleared() {
        countDownTimer.removeListener(tickListener)
        countDownTimer.cancel()
        compositeDisposable.clear()
    }

    companion object {
        private const val INTERVAL_IN_SEC = 1L
        private const val COUNTER_TIME = 20L
    }
}