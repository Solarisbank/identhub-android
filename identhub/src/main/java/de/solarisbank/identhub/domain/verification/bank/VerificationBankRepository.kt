package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.verification.bank.model.IBan
import io.reactivex.Completable
import io.reactivex.Single

interface VerificationBankRepository {
    fun getVerificationStatus(identificationId: String): Single<IdentificationDto>
    fun postVerify(iBan: IBan): Single<IdentificationDto>
    fun postBankIdIdentification(iBan: IBan): Single<IdentificationDto>
    fun save(identificationDto: IdentificationDto): Completable
}