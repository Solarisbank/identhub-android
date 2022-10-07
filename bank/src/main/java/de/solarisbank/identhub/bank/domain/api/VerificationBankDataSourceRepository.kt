package de.solarisbank.identhub.bank.domain.api

import de.solarisbank.identhub.bank.domain.VerificationBankRepository
import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class VerificationBankDataSourceRepository(
    private val verificationBankNetworkDataSource: VerificationBankNetworkDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource
) : VerificationBankRepository {
    override fun getIdentification(): Single<IdentificationDto> {
        return identificationLocalDataSource.obtainIdentificationDto()
    }

    override fun getVerificationStatus(identificationId: String): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.getVerificationStatus(identificationId)
    }

    override fun postVerify(iBan: Iban): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.postVerify(iBan)
    }

    override fun postBankIdIdentification(iBan: Iban): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.postBankIdIdentification(iBan)
    }

    override fun save(identificationDto: IdentificationDto): Completable {
        Timber.d("save, identificationDto $identificationDto")
        return identificationLocalDataSource.saveIdentification(identificationDto)
    }

    override fun deleteAll(): Completable {
        return identificationLocalDataSource.deleteIdentification()
    }
}