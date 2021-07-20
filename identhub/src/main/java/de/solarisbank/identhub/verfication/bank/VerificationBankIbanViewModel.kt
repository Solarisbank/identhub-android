package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.feature.util.toVerificationState
import de.solarisbank.sdk.core.result.data
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class VerificationBankIbanViewModel(private val verifyIBanUseCase: VerifyIBanUseCase) : ViewModel() {
    private val verifyResultLiveData: MutableLiveData<VerificationState> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var ibanAttemts = 0

    fun getVerificationStateLiveData(): LiveData<VerificationState> {
        return verifyResultLiveData
    }

    fun onSubmitButtonClicked(iBan: String) {

        verifyResultLiveData.value = SealedVerificationState.Loading()
        ibanAttemts++
        compositeDisposable.add(verifyIBanUseCase.execute(iBan)
                .map {
                    it.data!!.toVerificationState()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { it ->
                            Timber.d("Iban verification result 7")
                            verifyResultLiveData.value = it

                        },
                        {
                            Timber.e(it,"Iban verification result 8")
                            verifyResultLiveData.value = SealedVerificationState.GenericError()
                        })
        )
    }


    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}