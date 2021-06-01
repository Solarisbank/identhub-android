package de.solarisbank.identhub.session.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class IdentHubSessionViewModel(private val identHubSessionUseCase : IdentHubSessionUseCase) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _initializationStateLiveData = MutableLiveData<Result<NavigationalResult<String>>>()
    /*todo implement error processing*/

    fun getInitializationStateLiveData(): LiveData<Result<NavigationalResult<String>>> {
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
                                {_initializationStateLiveData.value = Result.success(it)},
                                { _initializationStateLiveData.value = Result.failure(Throwable()) }
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