package de.solarisbank.identhub.session


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import de.solarisbank.identhub.session.core.ActivityResultLauncher
import de.solarisbank.identhub.session.core.ActivityResultRegistry

class IdentHubSessionObserver(private val fragmentActivity: FragmentActivity,
                              private val successCallback: (IdentHubSessionResult) -> Unit,
                              private val errorCallback: (IdentHubSessionFailure) -> Unit
) : DefaultLifecycleObserver {
    private lateinit var mainActivity: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        mainActivity = createResultLauncher(owner)
    }

    private fun createResultLauncher(owner: LifecycleOwner): ActivityResultLauncher<String> {
        return ActivityResultRegistry.register("de.solarisbank.identhub", fragmentActivity, IdentHubResultContract()) { result ->
            if (!result.data.isNullOrEmpty()) {
                successCallback(IdentHubSessionResult(result.data, result.step))
            } else {
                errorCallback(IdentHubSessionFailure(step = result.step))
            }
        }
    }

    fun start(sessionUrl: String) {
        mainActivity.launch(sessionUrl, fragmentActivity.supportFragmentManager)
    }
}