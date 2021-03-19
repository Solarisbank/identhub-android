package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.shared.result.Event
import de.solarisbank.shared.result.Result
import io.reactivex.disposables.CompositeDisposable
import java.util.regex.Pattern

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

    fun onIBanInputChanged(iBan: String) {
        if (isIBanValid(iBan)) {
            notifyIBanStateChanged(IBanState.VALID)
        }
    }

    private fun isIBanValid(iBan: String): Boolean {
        return Pattern.compile(IBAN_PATTERN).matcher(iBan).matches()
    }

    fun validationIBan(iBan: String): Boolean {
        val isValid = isIBanValid(iBan)
        notifyIBanStateChanged(if (isValid) IBanState.VALID else IBanState.INVALID)
        return isValid
    }

    enum class IBanState(val value: Int) {
        INVALID(2), VALID(1), NONE(0);
    }

    companion object {
        private const val IBAN_PATTERN = "^[A-Z]{2} ?[0-9]{2} ?[0-9]{4} ?[0-9]{4} ?[0-9]{4} ?[0-9]{4} ?[0-9]{2,8}$"
    }
}