package de.solarisbank.sdk.fourthline.feature.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.result.Event

class TermsAndConditionsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val termsAndConditionCheckedEventLiveData = MutableLiveData<Event<Boolean>>()

    fun getTermsAndConditionCheckedEventLiveData(): LiveData<Event<Boolean>> {
        return termsAndConditionCheckedEventLiveData
    }

    fun onTermsAndConditionCheckChanged(checked: Boolean) {
        termsAndConditionCheckedEventLiveData.postValue(Event(checked))
    }
}