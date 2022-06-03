package de.solarisbank.identhub.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityExampleIdenthubBinding
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.IdentHubSessionFailure
import de.solarisbank.identhub.session.feature.IdentHubSessionResult
import timber.log.Timber

class ExampleIdentHubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleIdenthubBinding
    private lateinit var session: IdentHubSession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleIdenthubBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)

        prefillSessionUrl()

        session = IdentHub().sessionWithUrl(binding.sessionInputField.text.toString().trim())
            .apply {
                onCompletionCallback(
                    fragmentActivity = this@ExampleIdentHubActivity,
                    successCallback = this@ExampleIdentHubActivity::onSuccess,
                    errorCallback = this@ExampleIdentHubActivity::onFailure,
                    confirmationSuccessCallback = this@ExampleIdentHubActivity::onConfirmationSuccess
                )
            }

        binding.startButton.setOnClickListener {
            setLoadingState()
            session.start()
        }
    }

    private fun setLoadingState() {
        binding.startButton.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setResultState() {
        binding.startButton.isEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun onSuccess(result: IdentHubSessionResult) {
        setResultState()
        val identificationId = result.identificationId
        Timber.d("onSuccess; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.text = "onSuccess called"
        binding.callbackResult.setTextColor(Color.GREEN)
    }

    private fun onConfirmationSuccess(result: IdentHubSessionResult) {
        setResultState()
        Timber.d("onConfirmationSuccess")
        binding.callbackResult.text = "onConfirmationSuccess called,  identification id: ${result.identificationId}"
        binding.callbackResult.setTextColor(Color.BLUE)
    }

    private fun onFailure(failure: IdentHubSessionFailure) {
        setResultState()
        val message = failure.message
        Timber.d("onFailure; IdentHubSessionFailure identification has not completed: $message")
        binding.callbackResult.text = "onFailure called"
        binding.callbackResult.setTextColor(Color.RED)
    }

    private fun prefillSessionUrl() {
        if (!intent.dataString.isNullOrEmpty()) {
            intent.dataString
        } else {
            BuildConfig.SESSION_URL
        }?.let { sessionUrl ->
            if (sessionUrl != "null") {
                binding.sessionInputField.setText(sessionUrl)
            }
        }
    }
}