package de.solarisbank.identhub.session.feature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class IdentHubSessionViewModel(
    private val identHubSessionUseCase : IdentHubSessionUseCase,
    private val initializationInfoRepository: InitializationInfoRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _initializationStateLiveData = MutableLiveData<Result<NavigationalResult<String>>>()
    /*todo implement error processing*/

    fun getInitializationStateLiveData(): LiveData<Result<NavigationalResult<String>>> {
        return _initializationStateLiveData
    }

    fun saveSessionId(url: String?) {
        identHubSessionUseCase.saveSessionId(url)
    }

    fun obtainNextStep() {
        compositeDisposable.add(
            identHubSessionUseCase.getNextStep()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("obtainLocalIdentificationState success: $it")
                        _initializationStateLiveData.value = Result.success(it)
                    },
                    {
                        Timber.e(it, "obtainLocalIdentificationState fail")
                        _initializationStateLiveData.value = Result.failure(it)
                    }
                )
        )
    }

    fun obtainLocalIdentificationState() {
        compositeDisposable.add(
            Single.zip(
                identHubSessionUseCase.obtainLocalIdentificationState(),
                initializationInfoRepository.obtainInfo(),
                { navigationResult, _ -> navigationResult }
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("obtainLocalIdentificationState success: $it")
                        _initializationStateLiveData.value = Result.success(it)
                    },
                    {
                        Timber.e(it, "obtainLocalIdentificationState fail")
                        _initializationStateLiveData.value = Result.failure(it)
                    }
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