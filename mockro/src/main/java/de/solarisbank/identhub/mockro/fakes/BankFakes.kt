package de.solarisbank.identhub.mockro.fakes

import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.identhub.bank.domain.api.VerificationBankNetworkDataSource
import de.solarisbank.identhub.mockro.Mockro
import de.solarisbank.identhub.mockro.shared.IdentificationChange
import de.solarisbank.identhub.mockro.shared.MockroIdentification
import de.solarisbank.identhub.mockro.shared.baseFakeIdent
import de.solarisbank.sdk.data.IdentificationMethod
import de.solarisbank.sdk.data.IdentificationStep
import de.solarisbank.sdk.data.di.koin.MockroPersona
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

class FakeVerificationBankNetworkDataSource: VerificationBankNetworkDataSource {

    override fun postVerify(iBan: Iban?): Single<IdentificationDto> {
        val ident = MockroIdentification.change(IdentificationChange.Custom {
            copy(url = "https://solarisgroup.com", method = IdentificationMethod.BANK.strValue)
        })
        if (Mockro.currentPersona == MockroPersona.BankHappyPath)
            MockroIdentification.change(IdentificationChange.GoToQes)
        return Single.just(ident)
    }

    override fun postBankIdIdentification(iBan: Iban?): Single<IdentificationDto> {
        val ident = MockroIdentification.change(IdentificationChange.Custom {
            copy(url = "https://solarisgroup.com", method = IdentificationMethod.BANK_ID.strValue)
        })
        if (Mockro.currentPersona == MockroPersona.BankIdHappyPath)
            MockroIdentification.change(IdentificationChange.GoToFourthline)
        return Single.just(ident)
    }

    override fun getVerificationStatus(identificationId: String?): Single<IdentificationDto> {
        return Single.just(
            baseFakeIdent.copy(
                status = "identification_data_required",
                nextStep = IdentificationStep.BANK_ID_FOURTHLINE.destination
            )
        )
    }
}