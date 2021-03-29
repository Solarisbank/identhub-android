package de.solarisbank.identhub.session.core

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY)
object ActivityResultRegistry {
    private val random = Random()

    private val keyToCallback: MutableMap<String, ContractAndCallback<*, *>> = HashMap()
    private val keyToLifecycleContainers: MutableMap<String, LifecycleContainer> = HashMap()
    private val keyToRequestCode: MutableMap<String, Int> = HashMap()
    private val parsedPendingResults: MutableMap<String, Any> = HashMap()
    private val requestCodeToKey: MutableMap<Int, String> = HashMap()
    private val pendingResults = Bundle()

    fun <I, O> register(key: String,
                        componentActivity: ComponentActivity,
                        contract: ActivityResultContract<I, O>,
                        callback: (O) -> Unit): ActivityResultLauncher<I> {

        val lifecycle = componentActivity.lifecycle
        val requestCode = registerKey(key)

        val lifecycleContainer = keyToLifecycleContainers[key] ?: LifecycleContainer(lifecycle)

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                keyToCallback[key] = ContractAndCallback(contract, callback)
                if (parsedPendingResults.containsKey(key)) {
                    val parsedPendingResult = parsedPendingResults[key] as O
                    parsedPendingResults.remove(key)
                    callback(parsedPendingResult)
                }
                val pendingResult = pendingResults.getParcelable<ActivityResult>(key)
                if (pendingResult != null) {
                    pendingResults.remove(key)
                    callback(contract.parseResult(
                            pendingResult.resultCode,
                            pendingResult.data))
                }
            } else if (event == Lifecycle.Event.ON_STOP) {
                keyToCallback.remove(key)
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                unregister(key)
            }
        }

        lifecycleContainer.addObserver(lifecycleEventObserver)
        keyToLifecycleContainers[key] = lifecycleContainer

        return object : ActivityResultLauncher<I>() {
            override fun launch(input: I, fragmentManager: FragmentManager) {
                val intent = contract.createIntent(componentActivity, input)
                val fragment = InlineFragment.newInstance(
                        launchIntent = intent,
                        requestCode = requestCode
                )
                onLaunch(fragmentManager, fragment, requestCode)
            }
        }
    }

    private fun unregister(key: String) {
        val keyToRequestCode = keyToRequestCode.remove(key)
        if (keyToRequestCode != null) {
            requestCodeToKey.remove(keyToRequestCode)
        }
        keyToCallback.remove(key)
        if (pendingResults.containsKey(key)) {
            pendingResults.remove(key)
        }

        if (parsedPendingResults.containsKey(key)) {
            parsedPendingResults.remove(key)
        }

        val lifecycleContainer = keyToLifecycleContainers[key]
        lifecycleContainer?.clearObservers()
        keyToLifecycleContainers.remove(key)
    }

    private fun registerKey(key: String): Int {
        val existing = keyToRequestCode[key]
        if (existing != null) {
            return existing
        }
        val rc = generateRandomNumber()
        requestCodeToKey[rc] = key
        keyToRequestCode[key] = rc
        return rc
    }

    private fun generateRandomNumber(): Int {
        var number: Int = (random.nextInt(INITIAL_REQUEST_CODE_VALUE) + 1)
        while (requestCodeToKey.containsKey(number)) {
            number = (random.nextInt(INITIAL_REQUEST_CODE_VALUE) + 1)
        }
        return number
    }

    private fun onLaunch(fragmentManager: FragmentManager,
                         fragment: Fragment,
                         requestCode: Int) {

        fragmentManager.beginTransaction()
                .add(fragment, getTag(requestCode))
                .commit()
    }

    fun dispatchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val key = requestCodeToKey[requestCode]
        if (key == null) {
            Log.w("ActivityResultRegistry", "Result listener not registered")
            return
        }

        val contractAndCallback = keyToCallback[key]

        doDispatch(key, resultCode, data, contractAndCallback)
    }

    private fun <I, O> doDispatch(key: String, resultCode: Int, data: Intent?,
                                  contractAndCallback: ContractAndCallback<I, O>?) {
        if (contractAndCallback != null) {
            val callback = contractAndCallback.callback
            val contract = contractAndCallback.activityResultContract
            callback(contract.parseResult(resultCode, data))
        } else {
            // Remove any parsed pending result
            parsedPendingResults.remove(key)
            pendingResults.putParcelable(key, ActivityResult(resultCode, data))
        }
    }

    private fun getTag(requestCode: Int): String = "${FRAGMENT_TAG_PREFIX}_$requestCode"

    // Use upper 16 bits for request codes
    private const val INITIAL_REQUEST_CODE_VALUE = 0x00010000
    private const val FRAGMENT_TAG_PREFIX = "inline_activity_result_"
}