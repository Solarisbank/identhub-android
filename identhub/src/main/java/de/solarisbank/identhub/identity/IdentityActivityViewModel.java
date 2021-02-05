package de.solarisbank.identhub.identity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IdentityActivityViewModel extends ViewModel {

    private final MutableLiveData<Integer> navigationActionId = new MutableLiveData<>();

    public IdentityActivityViewModel() {

    }

    public void navigateTo(int actionId) {
        navigationActionId.postValue(actionId);
    }

    public LiveData<Integer> getNavigationActionId() {
        return navigationActionId;
    }
}
