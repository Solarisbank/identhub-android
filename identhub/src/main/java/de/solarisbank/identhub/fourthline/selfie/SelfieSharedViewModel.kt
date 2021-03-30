package de.solarisbank.identhub.fourthline.selfie

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.shared.result.Event

class SelfieSharedViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val slefieLiveData = MutableLiveData<Event<Any>>()

    var selfieResultBitmap: Bitmap? = null

    fun getslefieLiveData(): LiveData<Event<Any>> {
        return slefieLiveData
    }

    fun success() {
        slefieLiveData.postValue(Event(Unit))
    }

    fun onRetakeButtonClicked() {

    }

    fun onConfirmButtonClicked() {

    }
}