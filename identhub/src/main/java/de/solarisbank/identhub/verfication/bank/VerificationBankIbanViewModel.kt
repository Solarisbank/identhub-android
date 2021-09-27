package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.data.dto.IbanVerificationDto
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.feature.util.toVerificationState
import de.solarisbank.sdk.domain.model.result.data
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class VerificationBankIbanViewModel(
    private val verifyIBanUseCase: VerifyIBanUseCase,
    private val bankIdPostUseCase: BankIdPostUseCase,
) : ViewModel() {
    private val verifyResultLiveData: MutableLiveData<VerificationState> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var ibanAttemts = 0

    fun getVerificationStateLiveData(): LiveData<VerificationState> {
        return verifyResultLiveData
    }

    fun onSubmitButtonClicked(iBan: String, useBankId: Boolean) {
        if (useBankId) {
            createBankIdIdentification(iBan)
        } else {
            verifyIbanAndCreateBankIdentification(iBan)
        }
    }

    private fun createBankIdIdentification(iBan: String) {
        compositeDisposable.add(
            bankIdPostUseCase.execute(Pair(iBan, null))
                .map {
                    IbanVerificationDto.IbanVerificationSuccessful(it.data!!.url, it.nextStep)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("bank_id identification created")
                        verifyResultLiveData.value = it.toVerificationState()
                    },
                    {
                        Timber.e(it, "bank_id idenification creation failed")
                        verifyResultLiveData.value = SealedVerificationState.GenericError()
                    }
                )
        )
    }

    private fun verifyIbanAndCreateBankIdentification(iBan: String) {
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
                    Timber.e(it, "Iban verification result 8")
                    verifyResultLiveData.value = SealedVerificationState.GenericError()
                })
        )
    }


    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}