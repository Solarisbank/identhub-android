package de.solarisbank.identhub.router

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity

class Router {
    fun to(context: Context, route: String, sessionUrl: String? = null): Intent {
        return when(route) {
            "bank/iban" -> Intent(context, VerificationBankActivity::class.java)
            "qes" -> Intent(context, ContractActivity::class.java)
            "fourthline/simplified" -> provideFourthlineActivityIntent(context)
                    .apply { action = FOURTHLINE_FLOW_ACTIVITY_ACTION }
            else -> Intent(context, IntroActivity::class.java)
        }.apply {
            if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
        }
    }

    private fun provideFourthlineActivityIntent(context: Context): Intent {
        val isFourthlineModuleAvailable = context.packageManager.queryIntentActivities(
                Intent(FOURTHLINE_FLOW_ACTIVITY_ACTION, null).apply { addCategory(Intent.CATEGORY_DEFAULT) }, 0
        )
                .last()
                .activityInfo.targetActivity == FOURTHLINE_ACTIVITY_REFERENCE_CLASS
        if (isFourthlineModuleAvailable) {
            return  Intent(context, Class.forName(FOURTHLINE_ACTIVITY_REFERENCE_CLASS))
        } else {
            throw IllegalStateException("Fourthline identification is impossible, module is absent")
        }
    }


    companion object {
        private const val FOURTHLINE_FLOW_ACTIVITY_ACTION = "de.solarisbank.identhub.FOURTHLINE_FLOW"
        private const val FOURTHLINE_ACTIVITY_REFERENCE_CLASS = "de.solarisbank.sdk.fourthline.feature.FourthlineActivity"
    }

}
