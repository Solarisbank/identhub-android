package de.solarisbank.identhub.contract

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.R
import de.solarisbank.identhub.data.dto.QesStepParametersDto
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.contract.step.parameters.QesStepParametersUseCase
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.domain.model.result.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ContractViewModel(
    savedStateHandle: SavedStateHandle,
    private val identificationStepPreferences: IdentificationStepPreferences,
    sessionUrlRepository: SessionUrlRepository,
    private val getIdentificationUseCase: GetIdentificationUseCase,
    private val qesStepParametersUseCase: QesStepParametersUseCase
) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val navigationActionId = MutableLiveData<Event<NaviDirection>>()

    init {
        if (savedStateHandle.contains(IdentHub.SESSION_URL_KEY)) {
            sessionUrlRepository.save(savedStateHandle.get<String>(IdentHub.SESSION_URL_KEY))
        }
    }

    fun saveQesStepParameters(qesStepParametersDto: QesStepParametersDto) {
        qesStepParametersUseCase.saveParameters(qesStepParametersDto)
    }

    fun getNaviDirectionEvent(): LiveData<Event<NaviDirection>> {
        return navigationActionId
    }

    fun navigateToContractSigningProcess() {
        navigationActionId.postValue(
            Event(
                NaviDirection.FragmentDirection(
                    R.id.action_contractSigningPreviewFragment_to_contractSigningFragment
                )
            )
        )
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
                            val (id) = (it as Result.Success<IdentificationDto>).data
                            navigationActionId.value =
                                Event(NaviDirection.VerificationSuccessfulStepResult(id, COMPLETED_STEP.CONTRACT_SIGNING.index))
                        } else {
                            Timber.d("onSubmitButtonClicked(), fail;  result: $it ")
                        }
                    }, {
                        Timber.e(it, "Cannot load identification data")
                        navigationActionId.value = Event(NaviDirection.VerificationFailureStepResult())
                    })
        )
    }

    fun callOnConfirmedResult() {
        val identificationId = (getIdentificationUseCase
            .execute(Unit).blockingGet() as Result.Success<IdentificationDto>).data.id
        navigationActionId.value = Event(NaviDirection.ConfirmationSuccessfulStepResult(
            identificationId
        ))
    }

}