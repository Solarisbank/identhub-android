package de.solarisbank.identhub.session.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        }
    }

    override fun navigate(navigationId: Int) {
        viewEvent.postValue(Event(MainViewEvent.Navigate(navigationId)))
    }
}

data class MainViewState(val currentModule: IdenthubModule)

sealed class MainViewEvent {
    data class Navigate(val navigationId: Int): MainViewEvent()
}