package de.solarisbank.identhub.error;

import androidx.lifecycle.ViewModel;

public abstract class ErrorViewModel extends ViewModel {
    protected abstract void onSubmitButtonClicked();

    protected abstract void onCancelButtonClicked();
}
