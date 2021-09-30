package de.solarisbank.identhub.session.data.contract

import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class ContractSignDataSourceRepository(
    private val contractSignNetworkDataSource: ContractSignNetworkDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource
) : ContractSignRepository {
    override fun authorize(identificationId: String): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postAuthorize(identificationId)
    }

    override fun confirmToken(identificationId: String, token: TransactionAuthenticationNumber): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postConfirm(identificationId, token)
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return contractSignNetworkDataSource.fetchDocumentFile(documentId)
    }

    override fun deleteAll(): Completable {
        return identificationLocalDataSource.deleteIdentification()
    }

    override fun getIdentification(): Single<IdentificationDto> {
        return identificationLocalDataSource.getIdentificationDto()
    }

    override fun save(identificationDto: IdentificationDto): Completable {
        return identificationLocalDataSource.saveIdentification(identificationDto)
    }
}