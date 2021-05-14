package de.solarisbank.identhub.session.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class IdentHubSessionViewModel(private val identHubSessionUseCase : IdentHubSessionUseCase) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _initializationStateLiveData = MutableLiveData<Pair<LOCAL_IDENTIFICATION_STATE, IdentificationDto?>>()
    /*todo implement error processing*/
    private val _errorLiveData = MutableLiveData<Throwable>()

    fun getInitializationStateLiveData(): LiveData<Pair<LOCAL_IDENTIFICATION_STATE, IdentificationDto?>> {
        return _initializationStateLiveData
    }

    fun saveSessionId(url: String?) {
        identHubSessionUseCase.saveSessionId(url)
    }

    fun obtainLocalIdentificationState() {
        compositeDisposable.add(
                identHubSessionUseCase.obtainLocalIdentificationState()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {_initializationStateLiveData.value = it},
                                { _errorLiveData.value = it }
                        )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    enum class LOCAL_IDENTIFICATION_STATE {
        OBTAINED, PROCESSING, ABSENT
    }

}