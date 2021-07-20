package de.solarisbank.sdk.core.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlertViewModel: ViewModel() {
    private val _events = MutableLiveData<AlertEvent>()
    val events: LiveData<AlertEvent>
        get() {
            return _events
        }

    fun sendEvent(event: AlertEvent) {
        _events.value = event
    }
}

sealed class AlertEvent(val tag: String) {
    class Positive(tag: String): AlertEvent(tag)
    class Negative(tag: String): AlertEvent(tag)
    class Cancel(tag: String): AlertEvent(tag)
}