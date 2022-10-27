package de.solarisbank.identhub.qes.contract.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.qes.domain.AuthorizeContractSignUseCase
import de.solarisbank.identhub.qes.domain.ConfirmContractSignUseCase
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.usecase.GetMobileNumberUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ContractSigningViewModel(
    private val authorizeContractSignUseCase: AuthorizeContractSignUseCase,
    private val confirmContractSignUseCase: ConfirmContractSignUseCase,
    private val getMobileNumberUseCase: GetMobileNumberUseCase
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
        compositeDisposable.add(
            getMobileNumberUseCase
                .execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("getMobileNumberUseCase.execute: succeeded: ${ it.succeeded }")
                        viewState.update { copy(phoneNumber = it.data?.number) }
                    },
                    {
                        Timber.e(it, "Error fetching phone number")
                    }
                )
        )
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