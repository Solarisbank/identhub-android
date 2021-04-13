package de.solarisbank.sdk.fourthline

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event


class FourthlineViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

//    val lastStep = MutableLiveData<>()

    private val _navigationActionId = MutableLiveData<Event<NaviDirection>>()
    val navigationActionId = _navigationActionId as LiveData<Event<NaviDirection>>


    fun navigateToDocTypeSelectionFragment() {

    }

//    fun navigateToDocScanFragment(bundle: Bundle) {
//        navigateTo(R.id.action_documentTypeSelectionFragment_to_documentScanFragment, bundle)
//    }
//
//    fun navigateToDocScanResultFragment() {
//        navigateTo(R.id.action_documentScanFragment_to_documentResultFragment)
//    }

    private fun navigateTo(actionId: Int, bundle: Bundle? = null) {
        _navigationActionId.value = Event(NaviDirection(actionId, bundle))
    }



}