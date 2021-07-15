package de.solarisbank.identhub.contract

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.R
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.router.COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHub.isPaymentResultAvailable
import de.solarisbank.sdk.core.data.model.StateUiModel
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ContractViewModel(
    savedStateHandle: SavedStateHandle,
    private val identificationStepPreferences: IdentificationStepPreferences,
    sessionUrlRepository: SessionUrlRepository,
    private val getIdentificationUseCase: GetIdentificationUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()
    private val uiStateLiveData = MutableLiveData<StateUiModel<Bundle?>>()

    fun getUiState(): LiveData<StateUiModel<Bundle?>> {
        return uiStateLiveData
    }

    init {
        if (savedStateHandle.contains(IdentHub.SESSION_URL_KEY)) {
            sessionUrlRepository.save(savedStateHandle.get<String>(IdentHub.SESSION_URL_KEY))
        }
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun doOnNavigationChanged(actionId: Int) {
        if (isPaymentResultAvailable && actionId == IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
            identificationStepPreferences.save(COMPLETED_STEP.VERIFICATION_BANK)
        }
    }

    fun callOnFailureResult() {
        navigationActionId.value = Event<NaviDirection>(NaviDirection(actionId = IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, null))
    }

    fun navigateToContractSigningProcess() {
        navigateTo(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment)
    }

    fun callOnSuccessResult() {
        identificationStepPreferences.save(COMPLETED_STEP.CONTRACT_SIGNING)
        compositeDisposable.add(
            getIdentificationUseCase
                .execute(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it is Result.Success<*>) {
                            Timber.d("onSubmitButtonClicked(), success;  result: $it ")
                            val (id) = (it as Result.Success<Identification>).data
                            val bundle = Bundle()
                            bundle.putInt(COMPLETED_STEP_KEY, COMPLETED_STEP.CONTRACT_SIGNING.index)
                            bundle.putString(IdentHub.IDENTIFICATION_ID_KEY, id)
                            uiStateLiveData.value = StateUiModel.Success(bundle)
                        } else {
                            Timber.d("onSubmitButtonClicked(), fail;  result: $it ")
                        }
                    }, {
                        Timber.e(it, "Cannot load identification data")
                        uiStateLiveData.value = StateUiModel.Error(null)
                    })
        )
    }

    fun sendResult(bundle: Bundle?) {
        navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, bundle)
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?) {
        navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
    }

    private fun navigateTo(actionId: Int) {
        navigateTo(actionId, null)
    }
}