package de.solarisbank.identhub.session


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.room.IdentityRoomDatabase.Companion.clearDatabase
import de.solarisbank.identhub.router.*
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel
import de.solarisbank.sdk.core.BaseActivity.Companion.IDENTHUB_STEP_ACTION
import timber.log.Timber

class IdentHubSessionObserver(val fragmentActivity: FragmentActivity,
                              private val successCallback: (IdentHubSessionResult) -> Unit,
                              private val errorCallback: (IdentHubSessionFailure) -> Unit,
                              private val sessionUrl: String
) : DefaultLifecycleObserver {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: IdentHubSessionViewModel
    private val identHubObserverSubcomponent =
            IdentHubSessionComponent
                    .getInstance(fragmentActivity.applicationContext)
                    .IdentHubSessionObserverSubComponentFactory()
                    .create()

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("onReceive 0")
            if (intent != null) {
                if (intent.hasExtra(NEXT_STEP_KEY) && !intent.hasExtra(IDENTIFICATION_ID_KEY)) {
                    Timber.d("onReceive 1")
                    intent.getStringExtra(NEXT_STEP_KEY)?.let {
                        fragmentActivity.startActivity(toNextStep(fragmentActivity, it, sessionUrl))
                        return
                    }
                } else if (intent.hasExtra(IDENTIFICATION_ID_KEY) && !intent.hasExtra(NEXT_STEP_KEY)) {
                    Timber.d("onReceive 2")
                    successCallback.invoke(IdentHubSessionResult(
                            intent.getStringExtra(IDENTIFICATION_ID_KEY)!!,
                            COMPLETED_STEP.getEnum(intent.getIntExtra(COMPLETED_STEP_KEY, -1))
                    ))
                } else if (!intent.hasExtra(NEXT_STEP_KEY) && !intent.hasExtra(IDENTIFICATION_ID_KEY)) {
                    Timber.d("onReceive 3")
                    errorCallback.invoke(IdentHubSessionFailure(
                            step = COMPLETED_STEP.getEnum(intent.getIntExtra(COMPLETED_STEP_KEY, -1))
                    ))
                } else {
                    Timber.d("onReceive 4")
                    Timber.d("bad intent: ${intent.extras}")
                }

            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        Timber.d("onCreate")
        identHubObserverSubcomponent.inject(this)
        initBroadcastReceiver()
        initViewModel()
        viewModel.saveSessionId(sessionUrl)
        viewModel.getInitializationStateLiveData().observe(fragmentActivity, { processInitializationStateResult(it) })
    }

    private fun initBroadcastReceiver() {
        LocalBroadcastManager.getInstance(fragmentActivity).registerReceiver(receiver, IntentFilter(IDENTHUB_STEP_ACTION))
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(fragmentActivity, viewModelFactory)
                .get(IdentHubSessionViewModel::class.java)
    }

    fun obtainLocalIdentificationState() {
        Timber.d("obtainLocalIdentificationState")
        viewModel.obtainLocalIdentificationState()
    }

    fun clearDataOnCompletion() {
        Timber.d("clearDataOnCompletion")
        clearDatabase(fragmentActivity)
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
                fragmentActivity.startActivity(toFirstStep(fragmentActivity, navResult.nextStep, sessionUrl))
            } else if (navResult.data == NEXT_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 2")
                fragmentActivity.startActivity(toNextStep(fragmentActivity, navResult.nextStep, sessionUrl))
            } else {
                Timber.d("processInitializationStateResult 4")
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Timber.d("onDestroy")
        LocalBroadcastManager.getInstance(fragmentActivity).unregisterReceiver(receiver)
        super.onDestroy(owner)
    }
}