package de.solarisbank.sdk.data

enum class IdentificationStep(val destination: String) {
    MOBILE_NUMBER("mobile_number"),
    TERMS_AND_CONDITIONS("solaris_terms"),
    BANK_IBAN("bank/iban"),
    BANK_ID_IBAN("bank_id/iban"),
    BANK_ID_FOURTHLINE("bank_id/fourthline"),
    BANK_QES("bank/qes"),
    BANK_ID_QES("bank_id/qes"),
    FOURTHLINE_SIMPLIFIED("fourthline/simplified"),
    FOURTHLINE_SIGNING("fourthline_signing"),
    FOURTHLINE_SIGNING_QES("fourthline_signing/qes"),
    QES("qes"),
    ABORT("abort");

    companion object {
        fun from(destination: String): IdentificationStep? {
            for (step in values()) {
                if (step.destination == destination)
                    return step
            }
            return null
        }
    }
}