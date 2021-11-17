package de.solarisbank.sdk.fourthline.domain.identification

import de.solarisbank.sdk.domain.usecase.CompletableUseCase
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class IdentificationUseCase(val fourthlineIdentificationRepository: FourthlineIdentificationRepository) : CompletableUseCase<Unit> {

    override fun execute(param: Unit): Completable {
        return fourthlineIdentificationRepository.postFourthlineSimplifiedIdentication()
                .flatMapCompletable { identificationDto -> fourthlineIdentificationRepository.save(identificationDto)}
                .observeOn(AndroidSchedulers.mainThread())
    }


}