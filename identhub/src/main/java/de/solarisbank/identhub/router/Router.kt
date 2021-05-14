package de.solarisbank.identhub.router

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.utils.FOURTHLINE_FLOW_ACTIVITY_ACTION
import de.solarisbank.identhub.session.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.identhub.session.utils.provideFourthlineActivityIntent
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity

class Router {
    fun to(context: Context, route: String, sessionUrl: String? = null): Intent {
        return when(route) {
            FLOW_DIRECTION.BANK_IBAN.destination -> Intent(context, VerificationBankActivity::class.java)
            FLOW_DIRECTION.QES.destination -> Intent(context, ContractActivity::class.java)
            FLOW_DIRECTION.FOURTHLINE_SIMPLIFIED.destination -> provideFourthlineActivityIntent(context)
                    .apply { action = FOURTHLINE_FLOW_ACTIVITY_ACTION }
            FLOW_DIRECTION.FOURTHLINE_UPLOADING.destination -> provideFourthlineActivityIntent(context)
                    .also { it.putExtra(SHOW_UPLOADING_SCREEN, true) }
            else -> Intent(context, IntroActivity::class.java)
        }.apply {
            if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
        }
    }

    companion object {
        enum class FLOW_DIRECTION(val destination: String) {
            BANK_IBAN("bank/iban"),
            QES("qes"),
            FOURTHLINE_SIMPLIFIED("fourthline/simplified"),
            FOURTHLINE_UPLOADING("fourthline/uploading")

        }
    }
}
