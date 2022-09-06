package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.qes.data.dto.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class ContractSignRetrofitDataSource(
    private val contractSignApi: ContractSignApi
    ) : ContractSignNetworkDataSource {

    override fun postAuthorize(identificationId: String): Single<IdentificationDto> {
        return contractSignApi.postAuthorize(identificationId)
            .subscribeOn(Schedulers.io())
    }

    override fun postConfirm(
        identificationId: String,
        tan: String
    ): Single<IdentificationDto> {
        return contractSignApi.postConfirm(
            identificationId,
            TransactionAuthenticationNumber(tan)
        ).subscribeOn(Schedulers.io())
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return contractSignApi.getDocumentFile(documentId)
            .subscribeOn(Schedulers.io())
    }

}