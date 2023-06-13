package de.solarisbank.identhub.session.main

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.solarisbank.sdk.module.abstraction.GeneralModuleLoader
import de.solarisbank.identhub.session.module.IdenthubModuleResolver
import de.solarisbank.identhub.session.module.ResolvedModule
import de.solarisbank.sdk.data.IdenthubResult
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.initial.FirstStepUseCase
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.data.initial.InitialConfigUseCase
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.module.abstraction.IdenthubModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.dsl.module

class MainViewModel(
    private val initialConfigUseCase: InitialConfigUseCase,
    private val moduleResolver: IdenthubModuleResolver,
    private val firstStepUseCase: FirstStepUseCase
): ViewModel(), IdenthubKoinComponent {

    private val viewState = MutableLiveData<MainViewState>()
    private val viewEvent = MutableLiveData<Event<MainViewEvent>>()

    fun state(): LiveData<MainViewState> = viewState
    fun events(): LiveData<Event<MainViewEvent>> = viewEvent
    var mainCoordinator: MainCoordinator? = null

    init {
        fetchInitialConfig()
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.CloseTapped -> {
                viewEvent.postValue(
                    Event(MainViewEvent.Close(IdenthubResult.Failed("User closed the SDK")))
                )
            }
            is MainAction.RetryTapped -> {
                IdLogger.info("Retrying the fetch of initial config")
                fetchInitialConfig()
            }
        }
    }

    private fun setModule(module: ResolvedModule.Module?) {
        module ?: return
        moduleResolver.makeModule(module.className)?.let {
            val generalLoaders = getKoin().getAll<GeneralModuleLoader>()
            viewState.value = MainViewState(currentModule = it)
            generalLoaders.forEach { loader -> loader.loadBefore(module.className, module.nextStep) }
            it.load()
            generalLoaders.forEach { loader -> loader.loadAfter(module.className, module.nextStep) }
        } ?: sendResult(
            IdenthubResult.Failed(
                "Module not found: ${module.className.substringAfterLast(".")}"
            )
        )
    }

    private fun fetchInitialConfig() {
        viewModelScope.launch {
            val storage: InitialConfigStorage
            try {
                storage = withContext(Dispatchers.IO) {
                    initialConfigUseCase.createInitialConfigStorage()
                }
            } catch(t: Throwable) {
                IdLogger.error("Initial config fetch failed: ${t.message}")
                viewState.value = MainViewState(null)
                return@launch
            }
            setUpWithConfig(storage)
        }
    }

    private fun setUpWithConfig(storage: InitialConfigStorage) {
        getKoin().loadModules(listOf(module {
            single { storage }
        }))
        mainCoordinator = MainCoordinator(
            storage,
            moduleResolver,
            ::handleMainCoordinatorEvent
        )
        val config = storage.get()
        IdLogger.setRemoteLoggingEnabled(config.isRemoteLoggingEnabled)
        initiateFirstStep(config)
    }

    private fun initiateFirstStep(initialConfig: IdenthubInitialConfig) {
        viewModelScope.launch {
            val firstStep = withContext(Dispatchers.IO) { firstStepUseCase.determineFirstStep(initialConfig) }
            mainCoordinator?.start(firstStep)
        }

    }

    private fun handleMainCoordinatorEvent(event: MainCoordinatorEvent) {
        when(event) {
            is MainCoordinatorEvent.Navigate -> {
                viewEvent.postValue(Event(MainViewEvent.Navigate(event.navigationId, event.bundle)))
            }
            is MainCoordinatorEvent.ModuleChange -> {
                setModule(event.module)
            }
            is MainCoordinatorEvent.ResultAvailable -> {
                sendResult(event.result)
            }
        }
    }

    private fun sendResult(result: IdenthubResult) {
        viewEvent.postValue(Event(MainViewEvent.Close(result)))
    }
}

data class MainViewState(val currentModule: IdenthubModule?)

sealed class MainViewEvent {
    data class Navigate(val navigationId: Int, val bundle: Bundle?): MainViewEvent()
    data class Close(val result: IdenthubResult): MainViewEvent()
}

sealed class MainAction {
    object CloseTapped: MainAction()
    object RetryTapped: MainAction()
}
