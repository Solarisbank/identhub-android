package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.data.verification.bank.model.IBan
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.usecase.SingleUseCase
import de.solarisbank.sdk.core.result.data
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException
import java.net.HttpURLConnection

class VerifyIBanUseCase(
        private val getIdentificationUseCase: GetIdentificationUseCase,
        private val verificationBankRepository: VerificationBankRepository
) : SingleUseCase<String, String>() {

    override fun invoke(iBan: String): Single<NavigationalResult<String>> {
        return verificationBankRepository.postVerify(IBan(iBan))
                .onErrorResumeNext { t ->
                    if (t is HttpException && t.code() == HttpURLConnection.HTTP_PRECON_FAILED) {
                        return@onErrorResumeNext verificationBankRepository.postBankIdIdentification(IBan(iBan))
                    } else {
                        return@onErrorResumeNext Single.error(t)
                    }
                }
                .flatMapCompletable { identificationDto: IdentificationDto -> verificationBankRepository.save(identificationDto) }
                .andThen(
                        getIdentificationUseCase.execute(Unit)
                                .map { NavigationalResult(it.data!!.url, it.data!!.nextStep) }
                ).observeOn(AndroidSchedulers.mainThread())
    }
}