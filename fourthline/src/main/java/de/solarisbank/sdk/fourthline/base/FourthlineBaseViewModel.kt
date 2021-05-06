package de.solarisbank.sdk.fourthline.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class FourthlineBaseViewModel : ViewModel(){
    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData = _errorLiveData as LiveData<String>
}