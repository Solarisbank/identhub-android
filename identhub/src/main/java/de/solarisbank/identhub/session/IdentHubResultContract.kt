package de.solarisbank.identhub.session

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.router.Router
import de.solarisbank.identhub.session.IdentHub.IDENTIFICATION_ID_KEY
import de.solarisbank.identhub.session.IdentHub.LAST_COMPLETED_STEP_KEY
import de.solarisbank.identhub.session.core.ActivityResultContract

class IdentHubResultContract : ActivityResultContract<IdentHubSessionDescription, IdentHubResultContract.Output> {
    override fun createIntent(context: Context, description: IdentHubSessionDescription): Intent {
        return Router().to(context, description.firstStep, description.sessionUrl)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): IdentHubResultContract.Output {
        val identificationId = intent?.getStringExtra(IDENTIFICATION_ID_KEY)
        val stepIndex = intent?.getIntExtra(LAST_COMPLETED_STEP_KEY, -1) ?: -1
        return Output(identificationId, IdentHubSession.Step.getEnum(stepIndex))
    }


    data class Output(val data: String?, val step: IdentHubSession.Step?)
}