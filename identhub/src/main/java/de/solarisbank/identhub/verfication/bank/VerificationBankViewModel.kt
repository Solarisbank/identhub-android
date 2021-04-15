package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.disposables.CompositeDisposable

class VerificationBankViewModel(private val verifyIBanUseCase: VerifyIBanUseCase) : ViewModel() {
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
                        { notifyResultChanged(it) },
                        { notifyResultChanged(Result.createUnknown(it)) })
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