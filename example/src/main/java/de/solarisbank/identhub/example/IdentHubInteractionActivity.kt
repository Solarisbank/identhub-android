package de.solarisbank.identhub.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityIdenthubInteractionBinding
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.session.IdentHubSessionFailure
import de.solarisbank.identhub.session.IdentHubSessionResult
import timber.log.Timber

class IdentHubInteractionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdenthubInteractionBinding
    private lateinit var identHubSession: IdentHubSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdenthubInteractionBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)
        Timber.d("onCreate, intent.getStringExtra(IdentHub.SESSION_URL_KEY) : ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        identHubSession = IdentHub.sessionWithUrl(intent.getStringExtra(IdentHub.SESSION_URL_KEY)!!)
                .apply {
                    onCompletionCallback(this@IdentHubInteractionActivity, this@IdentHubInteractionActivity::onSuccess, this@IdentHubInteractionActivity::onFailure)
                    onPaymentCallback(this@IdentHubInteractionActivity::onPayment)
                }

        binding.button.setOnClickListener { identHubSession.start() }
        binding.resumeButton.setOnClickListener { identHubSession.resume() }
    }

    private fun onSuccess(result: IdentHubSessionResult) {
        val identificationId = result.identificationId
        Timber.d("onSuccess; IdentHubSessionResult identification id: $identificationId")
        // Do something else.
    }

    private fun onPayment(result: IdentHubSessionResult) {
        val identificationId = result.identificationId
        Timber.d("onPayment; IdentHubSessionResult identification id: $identificationId")
        // Do something else.
    }

    private fun onFailure(failure: IdentHubSessionFailure) {
        val message = failure.message
        Timber.d("onFailure; IdentHubSessionFailure identification has not completed: $message")
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
