package de.solarisbank.identhub.example

import android.os.Bundle
import android.view.View
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
            setLoadingState()
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
            setLoadingState()
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
            setLoadingState()
            IdentHub.sessionWithUrl(intent.getStringExtra(IdentHub.SESSION_URL_KEY)!!)
                .apply {
                    onCompletionCallback(
                        fragmentActivity = this@IdentHubInteractionActivity,
                        successCallback = this@IdentHubInteractionActivity::onSuccess,
                        errorCallback = this@IdentHubInteractionActivity::onFailure,
                        confirmationSuccessCallback = this@IdentHubInteractionActivity::onConfirmationSuccess,
                    )
                    /*
                    onPaymentCallback is not set here
                     */
                    start()
                }
        }
    }

    private fun setLoadingState() {
        binding.startButton.isEnabled = false
        binding.resumeButton.isEnabled = false
        binding.startButtonWithoutOnPayment.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setResultState() {
        binding.startButton.isEnabled = true
        binding.resumeButton.isEnabled = false
        binding.startButtonWithoutOnPayment.isEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun setOnPaymentResultState() {
        binding.startButton.isEnabled = false
        binding.resumeButton.isEnabled = true
        binding.startButtonWithoutOnPayment.isEnabled = false
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun onSuccess(result: IdentHubSessionResult) {
        setResultState()
        val identificationId = result.identificationId
        Timber.d("onSuccess; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.setText("onSuccess called")
        // Do something else.
    }

    private fun onPayment(result: IdentHubSessionResult) {
        setOnPaymentResultState()
        val identificationId = result.identificationId
        Timber.d("onPayment; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.setText("onPayment called")
        // Do something else.
    }

    private fun onConfirmationSuccess(result: IdentHubSessionResult) {
        setResultState()
        Timber.d("onConfirmationSuccess")
        binding.callbackResult.setText("onConfirmationSuccess called,  identification id: ${result.identificationId}")
    }

    private fun onFailure(failure: IdentHubSessionFailure) {
        setResultState()
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