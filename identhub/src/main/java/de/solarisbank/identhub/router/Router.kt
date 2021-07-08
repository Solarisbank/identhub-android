package de.solarisbank.identhub.router

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.utils.FOURTHLINE_FLOW_ACTIVITY_ACTION
import de.solarisbank.identhub.session.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.identhub.session.utils.provideFourthlineActivityIntent
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import timber.log.Timber

//todo move to core module


//todo should be done using db
const val STATUS_KEY = "STATUS_KEY"
const val FIRST_STEP_KEY = "FIRST_STEP_KEY"
const val NEXT_STEP_KEY = "NEXT_STEP_KEY"
const val COMPLETED_STEP_KEY = "COMPLETED_STEP_KEY"

fun toFirstStep(context: Context, route: String, sessionUrl: String? = null): Intent {
    return when(route) {
        FIRST_STEP_DIRECTION.BANK_IBAN.destination -> Intent(context, VerificationBankActivity::class.java)
        FIRST_STEP_DIRECTION.QES.destination -> Intent(context, ContractActivity::class.java)
        FIRST_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination -> provideFourthlineActivityIntent(context)
                .apply { action = FOURTHLINE_FLOW_ACTIVITY_ACTION } //todo remove action
        FIRST_STEP_DIRECTION.FOURTHLINE_UPLOADING.destination -> provideFourthlineActivityIntent(context)
                .also { it.putExtra(SHOW_UPLOADING_SCREEN, true) }
        else -> Intent(context, IntroActivity::class.java)
    }.apply {
        if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
    }
}


fun toNextStep(context: Context, route: String, sessionUrl: String? = null): Intent {
    Timber.d("toNextStep, route : $route")
    return when(route) {
        NEXT_STEP_DIRECTION.BANK_IBAN.destination -> Intent(context, VerificationBankActivity::class.java)
        NEXT_STEP_DIRECTION.BANK_QES.destination -> Intent(context, ContractActivity::class.java)
        FIRST_STEP_DIRECTION.QES.destination -> Intent(context, ContractActivity::class.java) //todo check with backend
        NEXT_STEP_DIRECTION.BANK_ID_QES.destination -> Intent(context, ContractActivity::class.java)
        NEXT_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination -> provideFourthlineActivityIntent(context)
        NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination -> provideFourthlineActivityIntent(context)
                .apply {
                    putExtra(NEXT_STEP_KEY, NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination)
                }
        else -> throw IllegalArgumentException("wrong NextStep route: $route")
    }.apply {
        if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
    }
}

fun isIdentificationIdCreationRequired(dto: IdentificationDto): Boolean {
    return isIdentificationIdCreationRequired(method = dto.method, nextStep = dto.nextStep)
}

fun isIdentificationIdCreationRequired(entity: Identification): Boolean {
    return isIdentificationIdCreationRequired(method = entity.method, nextStep = entity.nextStep)
}

fun isIdentificationIdCreationRequired(method: String?, nextStep: String?): Boolean {
    return if (method == METHOD.BANK_ID.strValue && nextStep != null && nextStep!!.contains(METHOD.BANK_ID.strValue)) {
        return false
    } else if (method == METHOD.BANK.strValue && nextStep != null && !nextStep!!.contains(METHOD.BANK_ID.strValue) && nextStep!!.contains(METHOD.BANK.strValue)){
        return false
    } else {
        true
    }
}

enum class METHOD(val strValue: String) {
    BANK_ID("bank_id"),
    BANK("bank")
}

enum class FIRST_STEP_DIRECTION(val destination: String) {
    BANK_IBAN("bank/iban"),
    QES("qes"),
    FOURTHLINE_SIMPLIFIED("fourthline/simplified"),
    FOURTHLINE_UPLOADING("fourthline/uploading")

}

enum class NEXT_STEP_DIRECTION(val destination: String) {
    MOBILE_NUMBER("mobile_number"), //todo remove IdentityActivity and create MobileActivity
    BANK_IBAN("bank/iban"),
    BANK_ID_IBAN("bank_id/iban"),
    BANK_ID_FOURTHLINE("bank_id/fourthline"),
    BANK_QES("bank/qes"),
    BANK_ID_QES("bank_id/qes"),
    FOURTHLINE_SIMPLIFIED("fourthline/simplified")
}

enum class COMPLETED_STEP(val index: Int) {
    VERIFICATION_PHONE(1), VERIFICATION_BANK(2), CONTRACT_SIGNING(3);

    companion object {
        fun getEnum(index: Int): COMPLETED_STEP? {
            for (step in values()) {
                if (step.index == index) {
                    return step
                }
            }
            return null
        }
    }
}


