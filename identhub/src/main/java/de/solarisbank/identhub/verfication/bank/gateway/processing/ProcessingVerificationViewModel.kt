package de.solarisbank.identhub.verfication.bank.gateway.processing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.presentation.IdentificationUiModel
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.succeeded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class
ProcessingVerificationViewModel(
        private val identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,

) : ViewModel() {

    private val processingVerificationLiveDataEvent: MutableLiveData<Result<IdentificationUiModel>> = MutableLiveData()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val processingVerificationEvent: LiveData<Result<IdentificationUiModel>>
        get() {
            compositeDisposable.add(
                    identificationPollingStatusUseCase.execute(Unit)
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
                                Timber.d("identificationPollingStatusUseCase 3: ${it}")
                        processingVerificationLiveDataEvent.value = Result.createUnknown(it)
                    })
            )
            return processingVerificationLiveDataEvent
        }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    companion object {

    }
}