package de.solarisbank.identhub.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityExampleIdenthubBinding
import de.solarisbank.identhub.session.StartIdenthubContract
import de.solarisbank.sdk.data.IdenthubResult
import de.solarisbank.sdk.data.StartIdenthubConfig
import timber.log.Timber

class ExampleIdentHubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleIdenthubBinding

    private val identhub = registerForActivityResult(StartIdenthubContract()) {
        when (it) {
            is IdenthubResult.Success -> onSuccess(it.identificationId)
            is IdenthubResult.Confirmed -> onConfirmationSuccess(it.identificationId)
            is IdenthubResult.Failed -> onFailure(it.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleIdenthubBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)

        prefillSessionUrl()

        binding.startButton.setOnClickListener {
            setLoadingState()
            val sessionUrl = binding.sessionInputField.text.toString().trim()
            identhub.launch(StartIdenthubConfig(sessionUrl = sessionUrl))
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

    private fun onSuccess(identificationId: String) {
        setResultState()
        Timber.d("onSuccess; IdentHubSessionResult identification id: $identificationId")
        binding.callbackResult.text = "onSuccess called, identificationId: $identificationId"
        binding.callbackResult.setTextColor(Color.GREEN)
    }

    private fun onConfirmationSuccess(identificationId: String) {
        setResultState()
        Timber.d("onConfirmationSuccess")
        binding.callbackResult.text = "onConfirmationSuccess called,  identification id: $identificationId"
        binding.callbackResult.setTextColor(Color.BLUE)
    }

    private fun onFailure(message: String?) {
        setResultState()
        Timber.d("onFailure; IdentHubSessionFailure identification has not completed: $message")
        binding.callbackResult.text = "onFailure called: $message"
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