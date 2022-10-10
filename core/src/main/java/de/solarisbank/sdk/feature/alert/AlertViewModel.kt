package de.solarisbank.sdk.feature.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.domain.model.result.Event

class AlertViewModel(
    private val customizationRepository: CustomizationRepository
): ViewModel() {
    private val _events = MutableLiveData<Event<AlertEvent>>()
    val events: LiveData<Event<AlertEvent>>
        get() {
            return _events
        }

    fun sendEvent(event: AlertEvent) {
        _events.value = Event(event)
    }

    fun getCustomization(): Customization {
        return customizationRepository.get()
    }
}

sealed class AlertEvent(val tag: String) {
    class Positive(tag: String): AlertEvent(tag)
    class Negative(tag: String): AlertEvent(tag)
    class Cancel(tag: String): AlertEvent(tag)
}