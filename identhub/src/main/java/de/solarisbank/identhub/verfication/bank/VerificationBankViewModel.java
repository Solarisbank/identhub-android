package de.solarisbank.identhub.verfication.bank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VerificationBankViewModel extends ViewModel {

    public static final int DEFAULT_STATE = 0;
    public static final int SUCCESS_STATE = 1;
    public static final int ERROR_STATE = 2;
    public static final int DISABLED_STATE = 3;
    private MutableLiveData<Integer> state;

    public LiveData<Integer> getState() {
        if (state == null) {
            state = new MutableLiveData<>(0);
        }

        return state;
    }

    public void onSubmitButtonClicked() {
        int state = this.state.getValue();
        if (state >= DISABLED_STATE) {
            state = DEFAULT_STATE;
        } else {
            state++;
        }

        this.state.postValue(state);
    }
}
