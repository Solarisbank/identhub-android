package de.solarisbank.identhub.session

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.solarisbank.identhub.session.main.MainActivity
import de.solarisbank.sdk.data.IdenthubResult

class IdenthubContract: ActivityResultContract<String, IdenthubResult>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, MainActivity::class.java)
            .putExtra("session_url", input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): IdenthubResult {
        return intent?.getBundleExtra(MainActivity.KEY_RESULT)?.let {
            IdenthubResult.fromBundle(it)
        } ?: IdenthubResult.Failed("Could not parse result")
    }
}