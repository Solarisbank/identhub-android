package de.solarisbank.identhub.session

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class IdentHubSessionObserver(private val registry: ActivityResultRegistry,
                              private val successCallback: (IdentHubSessionResult) -> Unit,
                              private val errorCallback: (IdentHubSessionFailure) -> Unit
) : DefaultLifecycleObserver {
    private lateinit var mainActivity: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        mainActivity = createResultLauncher(owner)
    }

    private fun createResultLauncher(owner: LifecycleOwner): ActivityResultLauncher<String> {
        return registry.register("de.solarisbank.identhub", owner, IdentHubResultContract()) { result ->
            if (!result.data.isNullOrEmpty()) {
                successCallback(IdentHubSessionResult(result.data, result.step))
            } else {
                errorCallback(IdentHubSessionFailure(step = result.step))
            }
        }
    }

    fun start(sessionUrl: String) {
        mainActivity.launch(sessionUrl)
    }
}