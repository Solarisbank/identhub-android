package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.sdk.core.network.utils.parseErrorResponseDto
import de.solarisbank.sdk.core.result.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber

class VerificationBankIbanViewModel(private val verifyIBanUseCase: VerifyIBanUseCase) : ViewModel() {
    private val verifyResultLiveData: MutableLiveData<IbanVerificationState> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var ibanAttemts = 0

    fun getVerifyResultLiveData(): LiveData<IbanVerificationState> {
        return verifyResultLiveData
    }

    fun onSubmitButtonClicked(iBan: String) {

        verifyResultLiveData.value = IbanVerificationState.Loading()
        ibanAttemts++
        compositeDisposable.add(verifyIBanUseCase.execute(iBan)
                .map {
                    Timber.d("Iban verification result $it")
                    if (it.succeeded) {
                        Timber.d("Iban verification result 1 : ${it.data}, ${it.nextStep}")
                        return@map IbanVerificationState.BankSuccessful(it.data, it.nextStep)
                    } else if (it is Result.Error){
                        val type = it.type
                        var errorDtoCode: String? = null
                        if (it.throwable is HttpException) {
                            Timber.d("Iban verification result 2")
                            try {
                                errorDtoCode =
                                        (it.throwable as HttpException)
                                                .parseErrorResponseDto()?.errors?.get(0)
                                                ?.code.toString()
                                Timber.d("errorDto: $errorDtoCode")
                            } catch (e: Exception) {
                                Timber.w(e,"Error during errorDto parsing")
                                return@map IbanVerificationState.Error()
                            }
                        }

                        if (type is Type.BadRequest && (errorDtoCode == INVALID_IBAN)) {
                            val initializationDto = verifyIBanUseCase.getInitializationDto()
                            if (
                                    initializationDto?.allowedRetries != null &&
                                    ibanAttemts <= verifyIBanUseCase.getInitializationDto()!!.allowedRetries
                            ) {
                                Timber.d("Iban verification result 3")
                                return@map IbanVerificationState.BankIdDialogRetry(initializationDto!!.fallbackStep)
                            } else {
                                Timber.d("Iban verification result 4")
                                return@map IbanVerificationState.BankIdDialogAlterOnly(initializationDto!!.fallbackStep)
                            }
                        } else {
                            Timber.d("Iban verification result 5")
                            return@map IbanVerificationState.Error()
                        }
                    } else {
                        Timber.d("Iban verification result 6")
                        return@map IbanVerificationState.Error()
                    }
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
                            verifyResultLiveData.value = IbanVerificationState.Error()
                        })
        )
    }


    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {
        private const val INVALID_IBAN = "invalid_iban"
    }
}