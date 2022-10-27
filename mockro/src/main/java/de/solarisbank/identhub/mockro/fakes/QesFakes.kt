package de.solarisbank.identhub.mockro.fakes

import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.mockro.Mockro
import de.solarisbank.identhub.mockro.shared.IdentificationChange
import de.solarisbank.identhub.mockro.shared.MockroIdentification
import de.solarisbank.sdk.data.di.koin.MockroPersona
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeContractSignNetworkDataSource: ContractSignNetworkDataSource {
    override fun postAuthorize(identificationId: String): Single<IdentificationDto> {
        return Single.just(
            MockroIdentification.change(IdentificationChange.Custom { copy(status = "confirmation_required")})
        )
    }

    override fun postConfirm(identificationId: String, tan: String): Single<IdentificationDto> {
        val ident = MockroIdentification.change(IdentificationChange.Confirmed)
        if (Mockro.currentPersona == MockroPersona.BankHappyPath)
            MockroIdentification.changeWithDelay(IdentificationChange.Success)
        return Single.just(
            ident
        )
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return Single.just(Response.success("Some body!".toResponseBody()))
    }
}