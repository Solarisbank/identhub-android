package de.solarisbank.sdk.feature.config

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.dto.StyleDto
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.logger.IdLogger
import io.reactivex.Single
import java.io.Serializable
import java.util.concurrent.locks.ReentrantReadWriteLock

interface InitializationInfoRepository {
    fun isTermsAgreed(): Boolean
    fun isPhoneVerified(): Boolean
    fun getStyle(): StyleDto?

    fun obtainInfo(): Single<InitializationInfoDto>
}

class InitializationInfoRepositoryImpl(
    private val savedStateHandle: SavedStateHandle,
    private val initializationInfoRetrofitDataSource: InitializationInfoRetrofitDataSource,
    private val sessionUrlRepository: SessionUrlRepository
    ): InitializationInfoRepository {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    override fun obtainInfo(): Single<InitializationInfoDto> {
        return initializationInfoRetrofitDataSource.getInfo(sessionUrlRepository.get()!!)
            .doOnSuccess {
                saveInitializationInfoDto(it)
                IdLogger.setRemoteLoggingEnabled(enabled = it.sdkLogging)
            }
    }

    private fun saveInitializationInfoDto(initializationInfoDto: InitializationInfoDto?) {
        lock.writeLock().lock()
        try {
            savedStateHandle.set(INITIALIZATION_INFO_DTO, initializationInfoDto)
        } finally {
            lock.writeLock().unlock()
        }
    }

    private fun getInitializationInfoDto(): InitializationInfoDto? {
        lock.readLock().lock()
        return try {
            savedStateHandle.get<Serializable>(INITIALIZATION_INFO_DTO)
                    as? InitializationInfoDto?
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun isTermsAgreed(): Boolean {
        return getInitializationInfoDto()?.termsAccepted ?: false
    }

    override fun isPhoneVerified(): Boolean {
        return getInitializationInfoDto()?.phoneVerified ?: false
    }

    override fun getStyle(): StyleDto? {
        return getInitializationInfoDto()?.style
    }

    companion object {
        private const val INITIALIZATION_INFO_DTO = "INITIALIZATION_INFO_DTO"
    }
}