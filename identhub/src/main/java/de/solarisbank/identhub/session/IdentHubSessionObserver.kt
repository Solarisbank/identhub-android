package de.solarisbank.identhub.session


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.router.FIRST_STEP_DIRECTION
import de.solarisbank.identhub.router.FIRST_STEP_KEY
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.session.core.ActivityResultLauncher
import de.solarisbank.identhub.session.core.ActivityResultRegistry
import de.solarisbank.identhub.session.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class IdentHubSessionObserver(val fragmentActivity: FragmentActivity,
                              private val successCallback: (IdentHubSessionResult) -> Unit,
                              private val errorCallback: (IdentHubSessionFailure) -> Unit,
                              private val sessionUrl: String
) : DefaultLifecycleObserver {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainActivity: ActivityResultLauncher<IdentHubSessionDescription>
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var viewModel: IdentHubSessionViewModel
    private val identHubObserverSubcomponent =
            IdentHubSessionComponent
                    .getInstance(fragmentActivity.applicationContext)
                    .IdentHubSessionObserverSubComponentFactory()
                    .create()

    override fun onCreate(owner: LifecycleOwner) {
        identHubObserverSubcomponent.inject(this)
        mainActivity = createResultLauncher(owner)
        initViewModel()
        viewModel.saveSessionId(sessionUrl)
        viewModel.getInitializationStateLiveData().observe(fragmentActivity, { processInitializationStateResult(it) })

    }

    fun initViewModel() {
        viewModel = ViewModelProvider(fragmentActivity, viewModelFactory)
                .get(IdentHubSessionViewModel::class.java)
    }

    fun obtainLocalIdentificationState() {
        viewModel.obtainLocalIdentificationState()
    }

    private fun processInitializationStateResult(
            // todo create typeAlias
            result: Result<NavigationalResult<String>>
    ) {
        Timber.d("processInitializationStateResult: ")
        if (result.isSuccess) {
            val navResult = result.getOrNull()!!
            if (navResult.data == FIRST_STEP_KEY && navResult.nextStep != null) {
                start(sessionUrl, navResult.nextStep, navResult.data)
            } else if (navResult.data == NEXT_STEP_KEY && navResult.nextStep != null) {
                start(sessionUrl, navResult.nextStep, navResult.data)
            } else if (navResult.data != "") {
                successCallback.invoke(IdentHubSessionResult(navResult.data, null))
            } else {
                start(sessionUrl, FIRST_STEP_DIRECTION.FOURTHLINE_UPLOADING.destination, "")
            }
        }
    }

    private fun createResultLauncher(owner: LifecycleOwner): ActivityResultLauncher<IdentHubSessionDescription> {
        return ActivityResultRegistry.register("de.solarisbank.identhub", fragmentActivity, IdentHubResultContract()) { result ->
            if (!result.data.isNullOrEmpty()) {
                successCallback(IdentHubSessionResult(result.data, result.step))
            } else {
                errorCallback(IdentHubSessionFailure(step = result.step))
            }
        }
    }

    private fun start(sessionUrl: String, step: String, stepType: String) {
        val description = IdentHubSessionDescription(sessionUrl, step)
        mainActivity.launch(description, fragmentActivity.supportFragmentManager, stepType)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        compositeDisposable.clear()
        super.onDestroy(owner)
    }
}