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
import de.solarisbank.identhub.domain.navigation.router.*
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel
import de.solarisbank.sdk.core.BaseActivity.Companion.IDENTHUB_STEP_ACTION
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class IdentHubSessionObserver(
    private val successCallback: (IdentHubSessionResult) -> Unit,
    private val errorCallback: (IdentHubSessionFailure) -> Unit
) : DefaultLifecycleObserver {

    lateinit var viewModelFactory: (FragmentActivity) -> ViewModelProvider.Factory
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var viewModel: IdentHubSessionViewModel? = null

    var fragmentActivity: FragmentActivity? = null
        set(value) {
            field = value
            value?.lifecycle?.addObserver(this)
            value?.let {
                LocalBroadcastManager.getInstance(it)
                    .registerReceiver(receiver, IntentFilter(IDENTHUB_STEP_ACTION))
            } ?: run {
                field?.let {
                    LocalBroadcastManager.getInstance(it).unregisterReceiver(receiver)
                }
            }
        }
    var sessionUrl: String? = null
        set(value) {
            field = value
            viewModel?.saveSessionId(value)
        }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("onReceive 0")
            if (intent != null) {
                if (intent.hasExtra(NEXT_STEP_KEY) && !intent.hasExtra(IDENTIFICATION_ID_KEY)) {
                    Timber.d("onReceive 1")
                    intent.getStringExtra(NEXT_STEP_KEY)?.let {
                        toNextStep(fragmentActivity!!, it, sessionUrl)?.let {
                                nextStepIntent -> fragmentActivity?.startActivity(nextStepIntent)
                        }?:run {
                            errorCallback.invoke(
                                IdentHubSessionFailure(
                                    message = "Session aborted",
                                    step = COMPLETED_STEP.getEnum(intent.getIntExtra(COMPLETED_STEP_KEY, -1))
                                )
                            )
                        }
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

    fun clearDataOnCompletion() {
        Timber.d("clearDataOnCompletion")
        compositeDisposable.add(
            Observable.defer { Observable.just(clearDatabase()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Timber.d("Database successfully cleared") },
                    { Timber.e(it) })
        )
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
                fragmentActivity?.startActivity(toFirstStep(fragmentActivity!!, navResult.nextStep, sessionUrl))
            } else if (navResult.data == NEXT_STEP_KEY && navResult.nextStep != null) {
                Timber.d("processInitializationStateResult 2")
                val nextStep = toNextStep(fragmentActivity!!, navResult.nextStep, sessionUrl)
                if(nextStep != null) {
                    fragmentActivity?.startActivity(nextStep)
                } else {
                    errorCallback.invoke(IdentHubSessionFailure(message = "Session aborted", step = null))
                }
            } else {
                Timber.d("processInitializationStateResult 4")
            }
        } else {
            errorCallback.invoke(IdentHubSessionFailure(
                    //todo clear how fourthline simplified should be shown
                    message = "", null)
            )
        }
    }


    override fun onDestroy(owner: LifecycleOwner) {
        Timber.d("onDestroy")
        fragmentActivity?.let {
            it.lifecycle.removeObserver(this)
            LocalBroadcastManager.getInstance(it).unregisterReceiver(receiver)
        }
        viewModel = null
        fragmentActivity = null
        super.onDestroy(owner)
    }
}