package de.solarisbank.identhub.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityIdenthubInteractionBinding
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.IdentHubSessionFailure
import de.solarisbank.identhub.session.feature.IdentHubSessionResult
import timber.log.Timber

class IdentHubInteractionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdenthubInteractionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdenthubInteractionBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)
        Timber.d("onCreate, intent.getStringExtra(IdentHub.SESSION_URL_KEY) : ${intent.getStringExtra(
            IdentHub.SESSION_URL_KEY)}")

        binding.startButton.setOnClickListener {
            IdentHub.sessionWithUrl(intent.getStringExtra(IdentHub.SESSION_URL_KEY)!!)
            .apply {
                onCompletionCallback(
                    fragmentActivity = this@IdentHubInteractionActivity,
                    successCallback = this@IdentHubInteractionActivity::onSuccess,
                    errorCallback = this@IdentHubInteractionActivity::onFailure
                )
                onPaymentCallback(this@IdentHubInteractionActivity::onPayment)
                start()
            }
        }
        binding.resumeButton.setOnClickListener {
            IdentHub.sessionWithUrl(intent.getStringExtra(IdentHub.SESSION_URL_KEY)!!)
                .apply {
                    onCompletionCallback(
                        fragmentActivity = this@IdentHubInteractionActivity,
                        successCallback = this@IdentHubInteractionActivity::onSuccess,
                        errorCallback = this@IdentHubInteractionActivity::onFailure
                    )
                    onPaymentCallback(this@IdentHubInteractionActivity::onPayment)
                    resume()
                }
        }
        binding.startButtonWithoutOnPayment.setOnClickListener {
            IdentHub.sessionWithUrl(intent.getStringExtra(IdentHub.SESSION_URL_KEY)!!)
                .apply {
                    onCompletionCallback(
                        fragmentActivity = this@IdentHubInteractionActivity,
                        successCallback = this@IdentHubInteractionActivity::onSuccess,
                        errorCallback = this@IdentHubInteractionActivity::onFailure
                    )
                    /*
                    onPaymentCallback is not set here
                     */
                    start()
                }
        }
    }

    private fun onSuccess(result: IdentHubSessionResult) {
        val identificationId = result.identificationId
        Timber.d("onSuccess; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.setText("onSuccess called")
        // Do something else.
    }

    private fun onPayment(result: IdentHubSessionResult) {
        val identificationId = result.identificationId
        Timber.d("onPayment; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.setText("onPayment called")
        // Do something else.
    }

    private fun onFailure(failure: IdentHubSessionFailure) {
        val message = failure.message
        Timber.d("onFailure; IdentHubSessionFailure identification has not completed: $message")
        binding.callbackResult.setText("onFailure called")
        // Continue after failed identification.
    }

    override fun onDestroy() {
        IdentHub.clear()
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}