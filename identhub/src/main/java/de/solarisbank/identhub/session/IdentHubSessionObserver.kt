package de.solarisbank.identhub.session


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.router.Router
import de.solarisbank.identhub.session.core.ActivityResultLauncher
import de.solarisbank.identhub.session.core.ActivityResultRegistry
import de.solarisbank.identhub.session.di.IdentHubSessionComponent
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel
import de.solarisbank.identhub.session.feature.IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE
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
            result: Pair<IdentHubSessionViewModel.LOCAL_IDENTIFICATION_STATE, IdentificationDto?>
    ) {
        Timber.d("processInitializationStateResult: ")
        when (result.first) {
            LOCAL_IDENTIFICATION_STATE.ABSENT -> result.second?.let{ start(sessionUrl, it.nextStep!!) }
            LOCAL_IDENTIFICATION_STATE.PROCESSING -> result.second?.let{ start(sessionUrl, Router.Companion.FLOW_DIRECTION.FOURTHLINE_UPLOADING.destination) }
            LOCAL_IDENTIFICATION_STATE.OBTAINED -> result.second?.let{
                successCallback.invoke(IdentHubSessionResult(it.id, null))
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

    private fun start(sessionUrl: String, firstStep: String) {
        val description = IdentHubSessionDescription(sessionUrl, firstStep)
        mainActivity.launch(description, fragmentActivity.supportFragmentManager)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        compositeDisposable.clear()
        super.onDestroy(owner)
    }
}