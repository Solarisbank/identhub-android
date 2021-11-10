package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
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
        tan: TransactionAuthenticationNumber
    ): Single<IdentificationDto> {
        return contractSignApi.postConfirm(identificationId, tan)
            .subscribeOn(Schedulers.io())
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return contractSignApi.getDocumentFile(documentId)
            .subscribeOn(Schedulers.io())
    }

}