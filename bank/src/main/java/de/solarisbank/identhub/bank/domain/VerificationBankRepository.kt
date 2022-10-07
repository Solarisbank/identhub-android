package de.solarisbank.identhub.bank.domain

import de.solarisbank.identhub.bank.data.Iban
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single

interface VerificationBankRepository {
    fun getIdentification(): Single<IdentificationDto>
    fun getVerificationStatus(identificationId: String): Single<IdentificationDto>
    fun postVerify(iBan: Iban): Single<IdentificationDto>
    fun postBankIdIdentification(iBan: Iban): Single<IdentificationDto>
    fun save(identificationDto: IdentificationDto): Completable
    fun deleteAll(): Completable
}