package de.solarisbank.identhub.session

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.solarisbank.identhub.session.main.MainActivity
import de.solarisbank.sdk.data.IdenthubResult

class StartIdenthubContract: ActivityResultContract<String, IdenthubResult>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, MainActivity::class.java)
            .putExtra(SessionUrlKey, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): IdenthubResult {
        return intent?.getBundleExtra(ResultKey)?.let {
            IdenthubResult.fromBundle(it)
        } ?: IdenthubResult.Failed("Could not parse result")
    }

    companion object {
        const val SessionUrlKey = "session_url_key"
        const val ResultKey = "result_key"
    }
}