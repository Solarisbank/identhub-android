package de.solarisbank.identhub.session.main

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.module.IdenthubModuleConfigurator
import de.solarisbank.identhub.session.module.IdenthubModuleResolver
import de.solarisbank.identhub.session.module.ResolvedModule
import de.solarisbank.sdk.data.IdenthubResult
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.initial.InitialConfigUseCase
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.module.abstraction.IdenthubModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module

class MainViewModel(
    private val initialConfigUseCase: InitialConfigUseCase,
    private val moduleResolver: IdenthubModuleResolver,
    private val moduleConfigurator: IdenthubModuleConfigurator
): ViewModel(), IdenthubKoinComponent {
    private val viewState = MutableLiveData<MainViewState>()
    private val viewEvent = MutableLiveData<Event<MainViewEvent>>()
    private val disposables = CompositeDisposable()

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
        }
    }

    private fun setModule(module: ResolvedModule.Module?) {
        module ?: return
        moduleResolver.makeModule(module.className)?.let {
            moduleConfigurator.configure(module)
            viewState.value = MainViewState(currentModule = it)
            it.load()
        } ?: sendResult(
            IdenthubResult.Failed(
                "Module not found: ${module.className.substringAfterLast(".")}"
            )
        )
    }

    private fun fetchInitialConfig() {
        disposables.add(
            initialConfigUseCase
                .createInitialConfigStorage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { config ->
                        getKoin().loadModules(listOf(module {
                            single { config }
                        }))
                        mainCoordinator = MainCoordinator(
                            config,
                            moduleResolver,
                            ::handleMainCoordinatorEvent
                        ).apply {
                            start()
                        }
                        IdLogger.setRemoteLoggingEnabled(config.get().isRemoteLoggingEnabled)
                    },
                    { throwable ->
                        throwable.message?.let { IdLogger.debug(it) }
                    }
                )
        )
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

data class MainViewState(val currentModule: IdenthubModule)

sealed class MainViewEvent {
    data class Navigate(val navigationId: Int, val bundle: Bundle?): MainViewEvent()
    data class Close(val result: IdenthubResult): MainViewEvent()
}

sealed class MainAction {
    object CloseTapped: MainAction()
}
