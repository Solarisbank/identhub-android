package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.shared.result.data
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class VerifyIBanUseCase(
        private val getIdentificationUseCase: GetIdentificationUseCase,
        private val verificationBankRepository: VerificationBankRepository
) : SingleUseCase<String, String>() {

    override fun invoke(iBan: String): Single<String> {
        return verificationBankRepository.postVerify(IBan(iBan))
                .flatMapCompletable { identificationDto: IdentificationDto -> verificationBankRepository.save(identificationDto!!) }
                .andThen(
                        getIdentificationUseCase.execute(Unit)
                                .map { it.data!!.url }
                ).observeOn(AndroidSchedulers.mainThread())
    }
}