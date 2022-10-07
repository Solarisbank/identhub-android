package de.solarisbank.identhub.bank.feature.processing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.bank.domain.ProcessingVerificationUseCase
import de.solarisbank.identhub.feature.util.toProcessingVerificationState
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProcessingVerificationViewModel(
    private val processingVerificationUseCase: ProcessingVerificationUseCase,
) : ViewModel() {
    private val verificationResultLiveDataEvent:
            MutableLiveData<ProcessingVerificationFragment.ProcessingVerificationResult> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun processingVerificationEvent(iban: String): LiveData<ProcessingVerificationFragment.ProcessingVerificationResult> {
            compositeDisposable.add(
                    processingVerificationUseCase.execute(iban)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                            {
                                if (it.succeeded && it.data != null) {
                                    Timber.d("onSuccess 1, it : $it")
                                    verificationResultLiveDataEvent.value =
                                            it.data!!.toProcessingVerificationState()
                                } else {
                                    Timber.d("onSuccess 2, it : $it")
                                    verificationResultLiveDataEvent.value =
                                        ProcessingVerificationFragment.ProcessingVerificationResult.GenericError
                                }
                            },{
                                Timber.e(it,"onFailure")
                        verificationResultLiveDataEvent.value =
                            ProcessingVerificationFragment.ProcessingVerificationResult.GenericError
                    })
            )
            return verificationResultLiveDataEvent
        }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}