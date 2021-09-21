package de.solarisbank.identhub.data.verification.bank

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.identhub.domain.verification.bank.VerificationBankRepository
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class VerificationBankDataSourceRepository(
        private val identificationEntityMapper: Mapper<IdentificationDto, IdentificationWithDocument>,
        private val verificationBankNetworkDataSource: VerificationBankNetworkDataSource,
        private val verificationBankLocalDataSource: VerificationBankLocalDataSource
) : VerificationBankRepository {
    override fun getIdentification(): Single<Identification> {
        return verificationBankLocalDataSource.getIdentificationDto()
    }

    override fun getVerificationStatus(identificationId: String): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.getVerificationStatus(identificationId)
    }

    override fun postVerify(iBan: IBan): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.postVerify(iBan)
    }

    override fun postBankIdIdentification(iBan: IBan): Single<IdentificationDto> {
        return verificationBankNetworkDataSource.postBankIdIdentification(iBan)
    }

    override fun save(identificationDto: IdentificationDto): Completable {
        Timber.d("save, identificationDto $identificationDto")
        val identificationWithDocument = identificationEntityMapper.to(identificationDto)
        return verificationBankLocalDataSource.insert(identificationWithDocument)
                .subscribeOn(Schedulers.io())
    }

    override fun deleteAll(): Completable {
        return verificationBankLocalDataSource.deleteALl()
    }
}