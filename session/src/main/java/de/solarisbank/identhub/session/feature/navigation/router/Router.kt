package de.solarisbank.identhub.session.feature.navigation.router

import de.solarisbank.sdk.data.dto.IdentificationDto

const val FIRST_STEP_KEY = "FIRST_STEP_KEY"


fun isIdentificationIdCreationRequired(identificationDto: IdentificationDto): Boolean {
    return isIdentificationIdCreationRequired(
        method = identificationDto.method,
        nextStep = identificationDto.nextStep
    )
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
    FOURTHLINE_SIGNING("fourthline_signing")
}
enum class NEXT_STEP_DIRECTION(val destination: String) {
    MOBILE_NUMBER("mobile_number"), //todo remove IdentityActivity and create MobileActivity
    BANK_IBAN("bank/iban"),
    BANK_ID_IBAN("bank_id/iban"),
    BANK_ID_FOURTHLINE("bank_id/fourthline"),
    BANK_QES("bank/qes"),
    BANK_ID_QES("bank_id/qes"),
    FOURTHLINE_SIMPLIFIED("fourthline/simplified"),
    FOURTHLINE_SIGNING("fourthline_signing"),
    FOURTHLINE_SIGNING_QES("fourthline_signing/qes"),
    ABORT("abort")
}

//todo should be refactored or removed
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
