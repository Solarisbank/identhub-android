package de.solarisbank.identhub.contract.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.identhub.event.ClickEvent
import de.solarisbank.identhub.progress.DefaultCountDownTimer
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.identhub.verfication.phone.CountDownTime
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ContractSigningViewModel(
        savedStateHandle: SavedStateHandle?,
        private val authorizeContractSignUseCase: AuthorizeContractSignUseCase,
        private val confirmContractSignUseCase: ConfirmContractSignUseCase,
        private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val countDownTimeEventLiveData: MutableLiveData<Event<CountDownTime>> = MutableLiveData()
    private var countDownTimer = DefaultCountDownTimer(TimeUnit.SECONDS.toMillis(COUNTER_TIME), TimeUnit.SECONDS.toMillis(INTERVAL_IN_SEC))
    private val clickEventRelay = BehaviorRelay.createDefault(ClickEvent())
    private val authorizeResultLiveData: MutableLiveData<Result<Any>> = MutableLiveData<Result<Any>>()
    private val identificationResultLiveData: MutableLiveData<Result<IdentificationUiModel>> = MutableLiveData<Result<IdentificationUiModel>>()

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

    fun getIdentificationResultLiveData(): MutableLiveData<Result<IdentificationUiModel>> {
        return identificationResultLiveData
    }

    fun getCountDownTimeEventLiveData(): LiveData<Event<CountDownTime>> {
        return countDownTimeEventLiveData
    }

    fun onSendNewCodeClicked() {
        clickEventRelay.accept(ClickEvent())
        startTimer()
    }

    fun startTimer() {
        countDownTimer.start()
    }

    fun stopTimer() {
        countDownTimer.cancel()
    }

    fun onSubmitButtonClicked(confirmToken: String?) {
        Timber.d("onSubmitButtonClicked, confrimToken: $confirmToken")
        stopTimer()
        compositeDisposable.add(
                confirmContractSignUseCase.execute(confirmToken)
                        .andThen(identificationPollingStatusUseCase.execute(Unit))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Timber.d("onSubmitButtonClicked, success")
                            identificationResultLiveData.postValue(it)
                        },
                        {
                            Timber.d("onSubmitButtonClicked, fail")
                            identificationResultLiveData.postValue(Result.createUnknown(it))
                        }
                )
        )
    }

    override fun onCleared() {
        countDownTimer.removeListener(tickListener)
        countDownTimer.cancel()
        compositeDisposable.clear()
    }

    companion object {
        private const val INTERVAL_IN_SEC = 1L
        private const val COUNTER_TIME = 120L
    }
}