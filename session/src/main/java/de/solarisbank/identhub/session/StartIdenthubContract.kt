package de.solarisbank.identhub.session

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.solarisbank.identhub.session.main.MainActivity
import de.solarisbank.sdk.data.IdenthubResult
import de.solarisbank.sdk.data.StartIdenthubConfig

class StartIdenthubContract: ActivityResultContract<StartIdenthubConfig, IdenthubResult>() {
    override fun createIntent(context: Context, input: StartIdenthubConfig): Intent {
        return Intent(context, MainActivity::class.java)
            .putExtra(ConfigKey, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): IdenthubResult {
        return intent?.getBundleExtra(ResultKey)?.let {
            IdenthubResult.fromBundle(it)
        } ?: IdenthubResult.Failed("Could not parse result")
    }

    companion object {
        const val ConfigKey = "config_key"
        const val ResultKey = "result_key"
    }
}