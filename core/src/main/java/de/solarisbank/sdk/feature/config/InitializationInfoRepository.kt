package de.solarisbank.sdk.feature.config

import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.dto.StyleDto
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import io.reactivex.Single

interface InitializationInfoRepository {
    fun isTermsAgreed(): Boolean
    fun isPhoneVerified(): Boolean
    fun getStyle(): StyleDto?

    fun obtainInfo(): Single<InitializationInfoDto>
}

class InitializationInfoRepositoryImpl(
    private val initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
    private val sessionUrlRepository: SessionUrlRepository
    ): InitializationInfoRepository {

    private var cached: InitializationInfoDto? = null

    override fun obtainInfo(): Single<InitializationInfoDto> {
        return initializationInfoRetrofitDataSource.getInfo(sessionUrlRepository.get()!!)
            .doOnSuccess {
                cached = it
            }
    }

    override fun isTermsAgreed(): Boolean {
        return cached?.termsAccepted ?: false
    }

    override fun isPhoneVerified(): Boolean {
        return cached?.phoneVerified ?: false
    }

    override fun getStyle(): StyleDto? {
        return cached?.style
    }
}