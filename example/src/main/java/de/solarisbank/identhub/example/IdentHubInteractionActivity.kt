package de.solarisbank.identhub.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityIdenthubInteractionBinding
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSessionFailure
import de.solarisbank.identhub.session.IdentHubSessionResult

class IdentHubInteractionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdenthubInteractionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdenthubInteractionBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)

        val identHubSession = IdentHub.sessionWithUrl(BuildConfig.SESSION_URL)
                .apply {
                    onCompletionCallback(this@IdentHubInteractionActivity, this@IdentHubInteractionActivity::onSuccess, this@IdentHubInteractionActivity::onFailure)
                    onPaymentCallback(this@IdentHubInteractionActivity::onSuccess)
                }

        binding.button.setOnClickListener { identHubSession.start() }
        binding.resumeButton.setOnClickListener { identHubSession.resume() }
    }

    private fun onSuccess(result: IdentHubSessionResult) {
        val identificationId = result.identificationId
        Log.d(TAG, "IdentHubSessionResult identification id: $identificationId")
        // Do something else.
    }

    private fun onFailure(failure: IdentHubSessionFailure) {
        val message = failure.message
        Log.e(TAG, "IdentHubSessionFailure identification has not completed: $message")
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
