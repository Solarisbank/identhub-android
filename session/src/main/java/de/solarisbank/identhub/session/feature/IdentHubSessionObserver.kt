package de.solarisbank.identhub.session.feature

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.*
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.feature.di.internal.Provider
import timber.log.Timber

class IdentHubSessionObserver(
    private val successCallback: (SessionStepResult) -> Unit,
    private val errorCallback: (IdentHubSessionFailure) -> Unit
) : DefaultLifecycleObserver {

    lateinit var viewModelFactory: (FragmentActivity) -> ViewModelProvider.Factory
    private var viewModel: IdentHubSessionViewModel? = null

    var fragmentActivity: FragmentActivity? = null
        set(value) {
            field = value
            value?.lifecycle?.addObserver(this)
        }

    var sessionUrl: String? = null
        set(value) {
            field = value
            viewModel?.saveSessionId(value)
        }

    fun getIdentificationLocalDataSourceProvider(): Provider<IdentificationLocalDataSource> {
        return IdentHubSessionComponent
            .getInstance(fragmentActivity!!.applicationContext)
            .getIdentificationLocalDataSourceProvider()
    }

    fun setSessionResult(sessionStepResult: SessionStepResult) {
        Timber.d("setSessionResult 0 : $sessionStepResult")
        when (sessionStepResult) {
            is NaviDirection.NextStepStepResult -> {
                Timber.d("setSessionResult 1")
                executeNextStepResult(sessionStepResult.nextStep, sessionStepResult.completedStep)
            }
            is NaviDirection.PaymentSuccessfulStepResult -> {
                if (IdentHub.isPaymentResultAvailable()) {
                    successCallback.invoke(sessionStepResult)
                } else {
                    executeNextStepResult(sessionStepResult.nextStep, null)
                }
            }
            is NaviDirection.VerificationSuccessfulStepResult,
            is NaviDirection.ConfirmationSuccessfulStepResult -> {
                Timber.d("setSessionResult 2")
                successCallback.invoke(sessionStepResult)
            }

            is NaviDirection.VerificationFailureStepResult -> {
                Timber.d("setSessionResult 3")
                errorCallback.invoke(IdentHubSessionFailure(
                    step = sessionStepResult.completedStep?.let {
                        COMPLETED_STEP.getEnum(sessionStepResult.completedStep)
                    }
                ))
            }
        }
    }

    private fun executeNextStepResult(nextStep: String?, completedStep: Int?) {
        nextStep?.let {
            toNextStep(fragmentActivity!!, it, sessionUrl)?.let { nextStepIntent ->
                fragmentActivity?.startActivity(nextStepIntent)
            }
        }?:run {
            //todo better to check in sender
            errorCallback.invoke(
                IdentHubSessionFailure(
                    message = "Session aborted",
                    step = COMPLETED_STEP.getEnum(completedStep ?: -1)
                )
            )
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        Timber.d("onCreate")
        IdentHubSessionComponent.getInstance(fragmentActivity!!.applicationContext)
                .IdentHubSessionObserverSubComponentFactory()
                .create()
                .inject(this)
        initViewModel()
        viewModel?.saveSessionId(sessionUrl)
        viewModel?.getInitializationStateLiveData()?.observe(fragmentActivity!!, { processInitializationStateResult(it) })
    }

    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(fragmentActivity!!, viewModelFactory(fragmentActivity!!))
                .get(IdentHubSessionViewModel::class.java)
        }
    }

    fun obtainLocalIdentificationState() {
        Timber.d("obtainLocalIdentificationState")
        viewModel?.obtainLocalIdentificationState()
    }

    fun obtainNextStep() {
        Timber.d("obtainNextStep")
        viewModel?.obtainNextStep()
    }

    private fun processInitializationStateResult(
            // todo create typeAlias
            result: Result<NavigationalResult<String>>
    ) {
        Timber.d("processInitializationStateResult, result: $result ")
        if (result.isSuccess) {
            val navResult = result.getOrNull()!!
            if (navResult.data == FIRST_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 1")
                fragmentActivity?.startActivity(toFirstStep(fragmentActivity!!,
                    navResult.nextStep!!, sessionUrl))
            } else if (navResult.data == NEXT_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 2")
                val nextStep = toNextStep(
                    fragmentActivity!!,
                    navResult.nextStep!!,
                    sessionUrl
                )
                if(nextStep != null) {
                    fragmentActivity?.startActivity(nextStep)
                } else {
                    errorCallback.invoke(IdentHubSessionFailure(message = "Session aborted", step = null))
                }
            } else {
                Timber.d("processInitializationStateResult 4")
            }
        } else {
            errorCallback.invoke(
                IdentHubSessionFailure(
                    //todo clear how fourthline simplified should be shown
                    message = "", null
                )
            )
        }
    }


    override fun onDestroy(owner: LifecycleOwner) {
        Timber.d("onDestroy")
        fragmentActivity?.let {
            it.lifecycle.removeObserver(this)
        }
        viewModel = null
        fragmentActivity = null
        super.onDestroy(owner)
    }
}