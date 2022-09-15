package de.solarisbank.identhub.session.feature.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.identhub.session.domain.utils.SessionAlreadyResumedException
import de.solarisbank.identhub.session.domain.utils.SessionAlreadyStartedException
import de.solarisbank.identhub.session.feature.di.IdentHubSessionReceiver
import de.solarisbank.identhub.session.feature.di.IdentHubViewModelComponent
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_DIRECTION
import de.solarisbank.identhub.session.feature.navigation.router.FIRST_STEP_KEY
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_DIRECTION
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_KEY
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.logger.LoggerUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class IdentHubSessionViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application
): AndroidViewModel(application), IdentHubSessionReceiver {

    private var identHubSessionComponent: IdentHubViewModelComponent =
        IdentHubViewModelComponent.getInstance(application.applicationContext, savedStateHandle)

    val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository> =
        identHubSessionComponent.initializationInfoRepositoryProvider

    private var identHubSessionUseCase : IdentHubSessionUseCase =
        identHubSessionComponent.identHubSessionUseCaseProvider.get()

    private var _sessionStepResultLiveData = MutableLiveData<SessionStepResult>()
    var sessionStepResultLiveData: LiveData<SessionStepResult> = _sessionStepResultLiveData


    private val compositeDisposable = CompositeDisposable()
    private val _initializationStateLiveData = MutableLiveData<Result<NavigationalResult<String>>>()
    val initializationStateLiveData: LiveData<Result<NavigationalResult<String>>> = _initializationStateLiveData
    /*todo implement error processing*/

    private val mutableLiveData = MutableLiveData<Event<SessionStepResult>>()
    internal val livedata = mutableLiveData as LiveData<Event<SessionStepResult>>

    fun saveSessionId(url: String?) {
        identHubSessionUseCase.saveSessionId(url)
    }

    fun startIdentificationProcess(isResumeExpected: Boolean) {
        compositeDisposable.add(
            //todo move to usecase
            identHubSessionUseCase.startIdentification(isResumeExpected)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("obtainLocalIdentificationState1 success: $it")
                        if (initializationInfoRepositoryProvider.get().isPhoneVerified()) {
                            _initializationStateLiveData.value = Result.success(it)
                        } else {
                            _initializationStateLiveData.value = Result.success(NavigationalResult(
                                NEXT_STEP_KEY, NEXT_STEP_DIRECTION.MOBILE_NUMBER.destination))
                        }
                    },
                    {
                        if (it is SessionAlreadyStartedException || it is SessionAlreadyResumedException) {
                            Timber.w( "obtainLocalIdentificationState2: ${it::class.java.simpleName}")
                        } else {
                            Timber.e(it, "obtainLocalIdentificationState3: failure")
                            _initializationStateLiveData.value = Result.failure(it)
                        }
                    }
                )
        )
    }

    fun resumeIdentificationProcess() {
        identHubSessionUseCase.resumeIdentification()
    }

    @SuppressLint("CheckResult")
    fun resetIdentificationProcess() {
        identHubSessionUseCase.resetIdentification().blockingGet()
    }

    fun getIdentificationLocalDataSourceProvider(): Provider<IdentificationLocalDataSource> {
        return identHubSessionComponent.getIdentificationLocalDataSourceProvider()
    }

    fun getInitializationInMemoryDataSourceProvider(): Provider<IdentityInitializationDataSource> {
        return identHubSessionComponent.getIdentityInitializationDataSource()
    }

    fun getLoggerUseCase(): LoggerUseCase {
        return identHubSessionComponent.getLoggerUseCase().get()
    }

    fun phoneVerificationDone() {
        val firstStep = getInitializationInMemoryDataSourceProvider().get().getInitializationDto()?.firstStep
        _initializationStateLiveData.value = Result.success(NavigationalResult(NEXT_STEP_KEY, firstStep))
    }

    override fun onCleared() {
        Timber.d("onCleared")
        resetIdentificationProcess()
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {

        @Volatile var INSTANCE: IdentHubSessionViewModel? = null

        var initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>? = null
            private set

        fun getInstance(
            savedStateHandle: SavedStateHandle,
            application: Application
        ): IdentHubSessionViewModel {
            return INSTANCE ?: synchronized(this) {
                //todo check should savedStateHandle be passed as constructor parameter
                INSTANCE ?: IdentHubSessionViewModel(savedStateHandle, application)
                    .also {
                        INSTANCE = it
                        initializationInfoRepositoryProvider =
                            INSTANCE?.identHubSessionComponent
                                ?.initializationInfoRepositoryProvider
                    }
            }
        }
    }

    override fun setSessionResult(sessionStepResult: SessionStepResult) {
        _sessionStepResultLiveData.value = sessionStepResult
    }


}