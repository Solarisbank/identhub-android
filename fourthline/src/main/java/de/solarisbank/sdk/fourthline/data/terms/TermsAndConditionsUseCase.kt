package de.solarisbank.sdk.fourthline.data.terms

import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig
import de.solarisbank.sdk.fourthline.data.dto.AcceptTermsBody
import de.solarisbank.sdk.fourthline.data.dto.TermsAndConditions
import java.util.*

private const val NamirialEntity = "namirial"

interface TermsAndConditionsUseCase {
    suspend fun getNamirialTerms(): TermsAndConditions?
    suspend fun acceptNamirialTerms(documentId: String)
}

class TermsAndConditionsUseCaseImpl(
    private val api: TermsAndConditionsApi,
    private val config: FourthlineIdentificationConfig
    ): TermsAndConditionsUseCase {

    /**
     * Returns Namirial TermsAndConditions only if the identification invloves signing with
     * Namirial. Otherwise returns null.
     */
    override suspend fun getNamirialTerms(): TermsAndConditions? {
        return if (config.shouldShowNamirialTerms) {
            api.getTermsAndConditions(NamirialEntity, Locale.getDefault().language)
        } else {
            null
        }
    }

    override suspend fun acceptNamirialTerms(documentId: String) {
        api.acceptTerms(NamirialEntity, AcceptTermsBody(documentId))
    }


}