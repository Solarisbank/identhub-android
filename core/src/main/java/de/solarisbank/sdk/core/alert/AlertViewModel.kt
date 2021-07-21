package de.solarisbank.sdk.core.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.result.Event

class AlertViewModel: ViewModel() {
    private val _events = MutableLiveData<Event<AlertEvent>>()
    val events: LiveData<Event<AlertEvent>>
        get() {
            return _events
        }

    fun sendEvent(event: AlertEvent) {
        _events.value = Event(event)
    }
}

sealed class AlertEvent(val tag: String) {
    class Positive(tag: String): AlertEvent(tag)
    class Negative(tag: String): AlertEvent(tag)
    class Cancel(tag: String): AlertEvent(tag)
    class Dismiss(tag: String): AlertEvent(tag)
}