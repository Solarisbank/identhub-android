package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.succeeded
import de.solarisbank.sdk.core.result.throwable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class VerificationBankIbanViewModel(private val verifyIBanUseCase: VerifyIBanUseCase) : ViewModel() {
    private val iBanStateLiveData: MutableLiveData<Event<IBanState>> = MutableLiveData()
    private val verifyResultLiveData: MutableLiveData<Result<String>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val iBanState: LiveData<Event<IBanState>>
        get() = iBanStateLiveData

    fun getVerifyResultLiveData(): LiveData<Result<String>> {
        return verifyResultLiveData
    }

    fun onSubmitButtonClicked(iBan: String) {
        notifyResultChanged(Result.Loading)
        compositeDisposable.add(verifyIBanUseCase.execute(iBan)
                .subscribe(
                        { it ->
                            if (it.succeeded) {
                                Timber.d("verifyIBanUseCase.execute 1")
                                notifyResultChanged(it)
                            } else {
                                Timber.d("verifyIBanUseCase.execute 2 it.data ${it}")
                                it.throwable?.let { notifyResultChanged(Result.createUnknown(it)) }
                            }
                        },
                        {
                            Timber.d("verifyIBanUseCase.execute 3 it.data")
                            notifyResultChanged(Result.createUnknown(it))
                        })
        )
    }

    private fun notifyResultChanged(result: Result<String>) {
        verifyResultLiveData.postValue(result)
    }

    private fun notifyIBanStateChanged(iBanState: IBanState) {
        iBanStateLiveData.postValue(Event(iBanState))
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    enum class IBanState(val value: Int) {
        INVALID(2), VALID(1), NONE(0);
    }
}