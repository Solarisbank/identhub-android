package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.Status
import de.solarisbank.identhub.data.entity.Status.Companion.getEnum
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class KycUploadRepository(
        private val fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
        private val fourthlineIdentificationRoomDataSource: FourthlineIdentificationRoomDataSource,
        private val kycUploadRetrofitDataSource: KycUploadRetrofitDataSource,
        private val sessionUrlLocalDataSource: SessionUrlLocalDataSource
        ) {

    fun uploadKyc(file: File): Single<KycUploadResponseDto> {
        return fourthlineIdentificationRoomDataSource.getLastIdentification()
                .flatMap { identificationDto -> kycUploadRetrofitDataSource.uploadKYC(identificationDto, file)}
    }

    fun pollIdentificationStatus(): Single<IdentificationDto> {
        var count = 0L
        return fourthlineIdentificationRoomDataSource
                .getLastIdentification()
                .flatMap { identification ->
                    Observable
                            .interval(0, 5, TimeUnit.SECONDS)
                            .doOnNext {
                                count = it
                            }
                            .takeUntil { count > 30}
                            .timeout(30, TimeUnit.SECONDS)
                            .flatMap {
                                return@flatMap Observable.create<IdentificationDto> { emitter ->
                                    emitter.onNext(fourthlineIdentificationRetrofitDataSource.getIdentification(identification.id).blockingGet())
                                    emitter.onComplete()
                                }
                            }
                            .map { t ->
                                Timber.d("pollIdentificationStatus(), status : ${getEnum(t.status)}")
                                t
                            }
                            .takeWhile{ getEnum(it.status) != Status.SUCCESSFUL && getEnum(it.status) != Status.FAILED}
                            .toList()
                            .map { it[0] }
                }
    }

}