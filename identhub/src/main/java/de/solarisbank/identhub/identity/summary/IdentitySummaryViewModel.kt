package de.solarisbank.identhub.identity.summary

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.navigation.NaviDirection
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.IdentHub.LAST_COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.result.Result
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class IdentitySummaryViewModel(
        private val getIdentificationUseCase: GetIdentificationUseCase,
        savedStateHandle: SavedStateHandle?) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun onSubmitButtonClicked() {
        compositeDisposable.add(getIdentificationUseCase.execute(Unit).subscribe({
            if (it is Result.Success<*>) {
                val (id) = (it as Result.Success<Identification>).data
                val bundle = Bundle()
                bundle.putInt(LAST_COMPLETED_STEP_KEY, IdentHubSession.Step.CONTRACT_SIGNING.index)
                bundle.putString(IDENTIFICATION_ID_KEY, id)
                navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, bundle)
            }
        }, {
            navigateTo(IdentityActivityViewModel.ACTION_STOP_WITH_RESULT, null)
            Timber.e(it, "Cannot load identification data")
        }))
    }

    private fun navigateTo(actionId: Int, bundle: Bundle?) {
        navigationActionId.postValue(Event(NaviDirection(actionId, bundle)))
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}