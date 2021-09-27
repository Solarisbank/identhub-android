package de.solarisbank.identhub.session.feature.navigation.router

import android.content.Context
import android.content.Intent
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.utils.*
import de.solarisbank.sdk.data.entity.Identification
import timber.log.Timber

const val STATUS_KEY = "STATUS_KEY"
const val FIRST_STEP_KEY = "FIRST_STEP_KEY"
const val NEXT_STEP_KEY = "NEXT_STEP_KEY"
const val COMPLETED_STEP_KEY = "COMPLETED_STEP_KEY"

fun toFirstStep(context: Context, route: String, sessionUrl: String? = null): Intent {
    return when(route) {
        FIRST_STEP_DIRECTION.BANK_IBAN.destination -> provideActivityIntent(context, VERIFICATION_BANK_ACTIVITY_REFERENCE_CLASS)
        FIRST_STEP_DIRECTION.BANK_ID_IBAN.destination -> provideActivityIntent(context, VERIFICATION_BANK_ACTIVITY_REFERENCE_CLASS)
            .also { it.putExtra(FIRST_STEP_KEY, FIRST_STEP_DIRECTION.BANK_ID_IBAN.destination) }
        FIRST_STEP_DIRECTION.QES.destination -> provideActivityIntent(context, CONTRACT_ACTIVITY_REFERENCE_CLASS)
        FIRST_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination -> provideActivityIntent(context, FOURTHLINE_ACTIVITY_REFERENCE_CLASS)
                .apply { action = FOURTHLINE_FLOW_ACTIVITY_ACTION } //todo remove action
        FIRST_STEP_DIRECTION.FOURTHLINE_UPLOADING.destination -> provideActivityIntent(context, FOURTHLINE_ACTIVITY_REFERENCE_CLASS)
                .also { it.putExtra(SHOW_UPLOADING_SCREEN, true) }
        else -> throw IllegalStateException()
    }.apply {
        if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
    }
}

private fun provideActivityIntent(context: Context, ref: String): Intent {
    if (
        (ref == FOURTHLINE_ACTIVITY_REFERENCE_CLASS && isFourthlineModuleAvailable(context))
        || ref != FOURTHLINE_ACTIVITY_REFERENCE_CLASS
    ) {
        return  Intent(context, Class.forName(ref))
    } else {
        throw IllegalStateException("Fourthline identification is impossible, module is absent")
    }
}


fun toNextStep(context: Context, route: String, sessionUrl: String? = null): Intent? {
    Timber.d("toNextStep, route : $route")
    return when(route) {
        NEXT_STEP_DIRECTION.BANK_IBAN.destination -> provideActivityIntent(context, VERIFICATION_BANK_ACTIVITY_REFERENCE_CLASS)
        NEXT_STEP_DIRECTION.BANK_QES.destination -> provideActivityIntent(context, CONTRACT_ACTIVITY_REFERENCE_CLASS)
        FIRST_STEP_DIRECTION.QES.destination -> provideActivityIntent(context, CONTRACT_ACTIVITY_REFERENCE_CLASS) //todo check with backend
        NEXT_STEP_DIRECTION.BANK_ID_QES.destination -> provideActivityIntent(context, CONTRACT_ACTIVITY_REFERENCE_CLASS)
        NEXT_STEP_DIRECTION.FOURTHLINE_SIMPLIFIED.destination -> provideActivityIntent(context, FOURTHLINE_ACTIVITY_REFERENCE_CLASS)
        NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination ->provideActivityIntent(context, FOURTHLINE_ACTIVITY_REFERENCE_CLASS)
                .apply {
                    putExtra(NEXT_STEP_KEY, NEXT_STEP_DIRECTION.BANK_ID_FOURTHLINE.destination)
                }
        NEXT_STEP_DIRECTION.ABORT.destination -> null
        else -> throw IllegalArgumentException("wrong NextStep route: $route")
    }?.apply {
        if (sessionUrl != null) { putExtra(IdentHub.SESSION_URL_KEY, sessionUrl) }
    }
}

fun isIdentificationIdCreationRequired(entity: Identification): Boolean {
    return isIdentificationIdCreationRequired(method = entity.method, nextStep = entity.nextStep)
}

fun isIdentificationIdCreationRequired(method: String?, nextStep: String?): Boolean {
    val hasBankIdNextStep = nextStep?.contains(METHOD.BANK_ID.strValue) == true
    val hasBankNextStep = nextStep?.contains(METHOD.BANK.strValue) == true
    return if (method == METHOD.BANK_ID.strValue && hasBankIdNextStep) {
        return false
    } else if (method == METHOD.BANK.strValue && !hasBankIdNextStep && hasBankNextStep) {
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
    BANK_ID_IBAN("bank_id/iban"),
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
    FOURTHLINE_SIMPLIFIED("fourthline/simplified"),
    ABORT("abort")
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
