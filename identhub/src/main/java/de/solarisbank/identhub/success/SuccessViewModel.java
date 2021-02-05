package de.solarisbank.identhub.success;

import androidx.lifecycle.ViewModel;

public abstract class SuccessViewModel extends ViewModel {
    protected abstract void onSubmitButtonClicked();
}
