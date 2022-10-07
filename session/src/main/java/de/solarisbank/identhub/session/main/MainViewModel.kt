package de.solarisbank.identhub.session.main

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.feature.IdentHubSessionFailure
import de.solarisbank.identhub.session.feature.IdentHubSessionResult
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.module.abstraction.IdenthubModule
import de.solarisbank.sdk.module.resolver.IdenthubModuleResolver

class MainViewModel: ViewModel(), Navigator {
    private val viewState = MutableLiveData<MainViewState>()
    private val viewEvent = MutableLiveData<Event<MainViewEvent>>()

    fun state(): LiveData<MainViewState> = viewState
    fun events(): LiveData<Event<MainViewEvent>> = viewEvent

    fun setModule(moduleName: String?) {
        moduleName ?: return
        IdenthubModuleResolver().makeModule(moduleName)?.let {
            viewState.value = MainViewState(currentModule = it)
            it.load()
        } ?: moduleNotFound(moduleName)
    }

    override fun navigate(navigationId: Int, bundle: Bundle?) {
        viewEvent.postValue(Event(MainViewEvent.Navigate(navigationId, bundle)))
    }

    override fun onResult(result: Any?) {
        setModule(IdenthubModuleResolver.QESClassName)
    }

    private fun moduleNotFound(moduleName: String) {
        IdentHubSessionViewModel.INSTANCE?.setSessionResult(
            NaviDirection.VerificationFailureStepResult(
                "Module not found: ${moduleName.substringAfterLast(".")}"
            )
        )
        viewEvent.postValue(Event(MainViewEvent.Close))
    }
}

data class MainViewState(val currentModule: IdenthubModule)

sealed class MainViewEvent {
    data class Navigate(val navigationId: Int, val bundle: Bundle?): MainViewEvent()
    object Close: MainViewEvent()
}