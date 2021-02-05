package de.solarisbank.identhub.verfication.phone;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class VerificationPhoneViewModel extends ViewModel {

    public static final int DEFAULT_STATE = 0;
    public static final int SUCCESS_STATE = 1;
    public static final int ERROR_STATE = 2;
    public static final int DISABLED_STATE = 3;
    private static final int ONE_SEC = 1;
    private static final int COUNTER_TIME = 20;
    private static final int MIN_CODE_LENGTH = 6;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<CountDownTimeState> timerLiveData;
    private final CountDownTimer countDownTimer;
    private MutableLiveData<Integer> state;

    public VerificationPhoneViewModel() {
        compositeDisposable = new CompositeDisposable();
        timerLiveData = new MutableLiveData<>();
        countDownTimer = new CountDownTimer(TimeUnit.SECONDS.toMillis(COUNTER_TIME), TimeUnit.SECONDS.toMillis(ONE_SEC)) {
            @Override
            public void onTick(long millisUntilFinished) {
                String currentTime = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                timerLiveData.postValue(new CountDownTimeState(currentTime));
            }

            @Override
            public void onFinish() {
                CountDownTimeState currentState = timerLiveData.getValue();
                timerLiveData.postValue(new CountDownTimeState(currentState.getCurrentValue(), true));
            }
        };

    }

    public LiveData<Integer> getState() {
        if (state == null) {
            state = new MutableLiveData<>(0);
        }

        return state;
    }

    public LiveData<CountDownTimeState> getTimer() {
        return timerLiveData;
    }

    public void onSubmitButtonClicked() {
        int state = this.state.getValue();

        state = state == DEFAULT_STATE ? ERROR_STATE : DEFAULT_STATE;

        if (state == DEFAULT_STATE) {
            startTimer();
        } else {
            stopTimer();
        }

        this.state.postValue(state);
    }


    public void onSendNewCodeClicked() {
        state.postValue(DEFAULT_STATE);
        startTimer();
    }

    public void startTimer() {
        countDownTimer.start();
    }

    public void stopTimer() {
        countDownTimer.cancel();
    }

    public void onVerificationCodeReady(String code) {
        if (code.length() == MIN_CODE_LENGTH) {
            stopTimer();
            state.postValue(DISABLED_STATE);
            compositeDisposable.add(
                    Observable.just(SUCCESS_STATE)
                            .delay(6, TimeUnit.SECONDS)
                            .subscribe(integer -> state.postValue(SUCCESS_STATE))
            );
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }
}
