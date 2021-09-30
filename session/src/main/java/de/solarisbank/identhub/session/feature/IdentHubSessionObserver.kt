package de.solarisbank.identhub.session.feature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.feature.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.navigation.router.*
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.feature.base.BaseActivity.Companion.IDENTHUB_STEP_ACTION
import de.solarisbank.sdk.feature.di.internal.Provider
import timber.log.Timber

class IdentHubSessionObserver(
    private val successCallback: (IdentHubSessionResult) -> Unit,
    private val errorCallback: (IdentHubSessionFailure) -> Unit
) : DefaultLifecycleObserver {

    lateinit var viewModelFactory: (FragmentActivity) -> ViewModelProvider.Factory
    private var viewModel: IdentHubSessionViewModel? = null

    var fragmentActivity: FragmentActivity? = null
        set(value) {
            val old = field
            field = value
            value?.lifecycle?.addObserver(this)
            value?.let {
                Timber.d("set fragmentActivity 1")
                LocalBroadcastManager.getInstance(it)
                    .registerReceiver(receiver, IntentFilter(IDENTHUB_STEP_ACTION))
            } ?: run {
                old?.let {
                    Timber.d("set fragmentActivity 2")
                    LocalBroadcastManager.getInstance(it).unregisterReceiver(receiver)
                }
            }
        }
    var sessionUrl: String? = null
        set(value) {
            field = value
            viewModel?.saveSessionId(value)
        }

    fun getIdentificationLocalDataSourceProvider(): Provider<out IdentificationLocalDataSource> {
        return IdentHubSessionComponent
            .getInstance(fragmentActivity!!.applicationContext)
            .getIdentificationLocalDataSourceProvider()
    }

    private var cachedUUID: String? = null

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("onReceive 0, this: $this, this@IdentHubSessionObserver: ${this@IdentHubSessionObserver}")
            if (intent != null) {
                if (
                    intent.hasExtra(NEXT_STEP_KEY) // VerificationBank and Fourthline case
                    && !intent.hasExtra(IDENTIFICATION_ID_KEY)
                    && intent.hasExtra("uuid")
                ) {
                    val intentUuid = intent.getStringExtra("uuid")
                    if (cachedUUID != intentUuid) {
                        Timber.d("onReceive 1, $intentUuid")
                        cachedUUID = intentUuid
                        intent.getStringExtra(NEXT_STEP_KEY)?.let {
                            Timber.d("onReceive 1.1${intent.getStringExtra("uuid")}")
                            toNextStep(fragmentActivity!!, it, sessionUrl)?.let { nextStepIntent ->
                                fragmentActivity?.startActivity(nextStepIntent)
                            } ?: run {
                                errorCallback.invoke(
                                    IdentHubSessionFailure(
                                        message = "Session aborted",
                                        step = COMPLETED_STEP.getEnum(
                                            intent.getIntExtra(COMPLETED_STEP_KEY, -1)
                                        ))
                                )
                            }
                        }
                            return
                    }
                } else if (
                    intent.hasExtra(IDENTIFICATION_ID_KEY) // ContractActivity case
                    && !intent.hasExtra(NEXT_STEP_KEY)
                    && intent.hasExtra("uuid")
                ) {
                    val intentUuid = intent.getStringExtra("uuid")
                    Timber.d("onReceive 2, $intentUuid")
                    if (cachedUUID != intentUuid) {
                        cachedUUID = intentUuid
                        successCallback.invoke(
                            IdentHubSessionResult(
                                intent.getStringExtra(IDENTIFICATION_ID_KEY)!!,
                                COMPLETED_STEP.getEnum(intent.getIntExtra(COMPLETED_STEP_KEY, -1))
                            )
                        )
                    }
                } else if (!intent.hasExtra(NEXT_STEP_KEY) && !intent.hasExtra(IDENTIFICATION_ID_KEY)) {  //onBackPressed case
                    //todo foresee intenttypes and avoid null bundle
                    Timber.d("onReceive 3")
                    errorCallback.invoke(
                        IdentHubSessionFailure(
                            step = COMPLETED_STEP.getEnum(intent.getIntExtra(COMPLETED_STEP_KEY, -1)
                            )
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
            Timber.d("fragmentActivity 3")
            LocalBroadcastManager.getInstance(it).unregisterReceiver(receiver)
        }
        viewModel = null
        fragmentActivity = null
        super.onDestroy(owner)
    }
}