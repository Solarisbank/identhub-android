package de.solarisbank.identhub.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import de.solarisbank.identhub.example.databinding.ActivityMainBinding
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSessionFailure
import de.solarisbank.identhub.session.IdentHubSessionResult

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding!!.root
        setContentView(view)

        val identHubSession = IdentHub.sessionWithUrl(BuildConfig.SESSION_URL)
                .apply {
                    onCompletionCallback(this@MainActivity, this@MainActivity::onSuccess, this@MainActivity::onFailure)
                    onPaymentCallback(this@MainActivity::onSuccess)
                }

        binding!!.button.setOnClickListener { identHubSession.start() }
        binding!!.resumeButton.setOnClickListener { identHubSession.resume() }
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
