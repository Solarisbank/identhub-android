package de.solarisbank.identhub.verfication.bank.gateway.processing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.domain.verification.bank.JointAccountBankIdPostUseCase
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.data.model.IdentificationUiModel
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.data
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProcessingVerificationViewModel(
        private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
        private val jointAccountBankIdPostUseCase: JointAccountBankIdPostUseCase
) : ViewModel() {
    private val processingVerificationLiveDataEvent: MutableLiveData<Result<IdentificationUiModel>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun processingVerificationEvent(iban: String): LiveData<Result<IdentificationUiModel>> {
            compositeDisposable.add(
                    identificationPollingStatusUseCase.execute(Unit)
                            .flatMap {
                                /**
                                 * Polling request for checking joint account fail condition
                                 */
                                return@flatMap if (it.succeeded && Status.getEnum(it.data?.status) == Status.FAILED) {
                                    Timber.d("processingVerificationEvent 1: ${it}")
                                    jointAccountBankIdPostUseCase.execute(iban)
                                } else {
                                    Timber.d("processingVerificationEvent 2: ${it}")
                                    Single.just(it)
                                }
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                            {
                                if (it.succeeded) {
                                    processingVerificationLiveDataEvent.value = it
                                } else {
                                    processingVerificationLiveDataEvent.value = it
                                }
                            },{
                                Timber.d("processingVerificationEvent 3: ${it}")
                        processingVerificationLiveDataEvent.value = Result.createUnknown(it)
                    })
            )
            return processingVerificationLiveDataEvent
        }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}