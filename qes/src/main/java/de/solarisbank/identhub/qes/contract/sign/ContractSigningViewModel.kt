package de.solarisbank.identhub.qes.contract.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.solarisbank.identhub.qes.domain.AuthorizeContractSignUseCase
import de.solarisbank.identhub.qes.domain.ConfirmContractSignUseCase
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.MobileNumberUseCase
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContractSigningViewModel(
    private val authorizeContractSignUseCase: AuthorizeContractSignUseCase,
    private val confirmContractSignUseCase: ConfirmContractSignUseCase,
    private val mobileNumberUseCase: MobileNumberUseCase,
    private val dispatchers: IdenthubDispatchers
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val viewState = MutableLiveData<ContractSigningState>()
    private val viewEvents = MutableLiveData<Event<ContractSigningEvent>>()

    fun state(): LiveData<ContractSigningState> = viewState
    fun events(): LiveData<Event<ContractSigningEvent>> = viewEvents

    init {
        viewState.value = ContractSigningState(null, null, false)
        authorize()
        fetchPhoneNumber()
    }

    private fun authorize() {
        viewState.update { copy(shouldShowResend = false) }
        viewEvents.postValue(Event(ContractSigningEvent.CodeResent))

        compositeDisposable.add(
            authorizeContractSignUseCase.execute(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {})
        )
    }

    private fun fetchPhoneNumber() {
        viewModelScope.launch {
            withContext(dispatchers.IO) {
                try {
                    val number = mobileNumberUseCase.fetchMobileNumber()
                    mobileNumberUseCase.maskPhoneNumber(number)
                } catch (throwable: Throwable) {
                    IdLogger.error("Failed to fetch phone number", throwable)
                    null
                }
            }?.let {
                viewState.update { copy(phoneNumber = it) }
            }
        }
    }

    fun onAction(action: ContractSigningAction) {
        when (action) {
            is ContractSigningAction.ResendCode -> authorize()
            is ContractSigningAction.Submit -> submitToken(action.code)
            is ContractSigningAction.TimerExpired -> viewState.update { copy(shouldShowResend = true) }
        }
    }

    private fun submitToken(confirmToken: String) {
        Timber.d("onSubmitButtonClicked, confirmToken: $confirmToken")
        viewState.update { copy(signingResult = Result.Loading) }

        compositeDisposable.add(
                confirmContractSignUseCase
                    .execute(confirmToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            viewState.update { copy(signingResult = result) }
                            if (result.succeeded) {
                                Timber.d("onSubmitButtonClicked 1, success")

                            } else {
                                Timber.d("onSubmitButtonClicked 2, else")
                            }
                        },
                        {
                            Timber.d("onSubmitButtonClicked 3, fail")
                            viewState.update { copy(signingResult = Result.createUnknown(it)) }
                        }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}