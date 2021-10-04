package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.data.dto.IbanVerificationModel
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.feature.util.toVerificationState
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class VerificationBankIbanViewModel(
    private val verifyIBanUseCase: VerifyIBanUseCase,
    private val bankIdPostUseCase: BankIdPostUseCase,
    initializationInfoRepository: InitializationInfoRepository
) : ViewModel() {

    private val verifyResultLiveData: MutableLiveData<VerificationState> = MutableLiveData()
    private val termsAgreedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var ibanAttempts = 0

    init {
        termsAgreedLiveData.value = initializationInfoRepository.isTermsAgreed()
    }

    fun getVerificationStateLiveData(): LiveData<VerificationState> {
        return verifyResultLiveData
    }

    fun getTermsAgreedLiveData(): LiveData<Boolean> {
        return termsAgreedLiveData
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
                    IbanVerificationModel.IbanVerificationSuccessful(it.data!!.url, it.nextStep)
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
        ibanAttempts++
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