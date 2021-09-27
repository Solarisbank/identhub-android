package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.session.data.verification.bank.model.IBan
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single

interface VerificationBankRepository {
    fun getIdentification(): Single<Identification>
    fun getVerificationStatus(identificationId: String): Single<IdentificationDto>
    fun postVerify(iBan: IBan): Single<IdentificationDto>
    fun postBankIdIdentification(iBan: IBan): Single<IdentificationDto>
    fun save(identificationDto: IdentificationDto): Completable
    fun deleteAll(): Completable
}