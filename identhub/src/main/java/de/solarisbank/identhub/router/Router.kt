package de.solarisbank.identhub.router

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity

class Router {
    fun to(context: Context, route: String, sessionUrl: String? = null): Intent {
        val activity = when(route) {
            "bank/iban" -> VerificationBankActivity::class.java
            "qes" -> ContractActivity::class.java
            "fourthline/simplified" -> IntroActivity::class.java
            else -> IntroActivity::class.java
        }
        val intent = Intent(context, activity)
        if (sessionUrl != null) {
            intent.putExtra(IdentHub.SESSION_URL_KEY, sessionUrl)
        }
        return intent
    }
}
