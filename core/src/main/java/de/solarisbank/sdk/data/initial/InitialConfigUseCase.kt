package de.solarisbank.sdk.data.initial

import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.dto.PartnerSettingsDto
import de.solarisbank.sdk.data.dto.StyleDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.http.GET

data class IdenthubInitialConfig(
    val isTermsPreAccepted: Boolean,
    val isPhoneNumberVerified: Boolean,
    val isRemoteLoggingEnabled: Boolean,
    val isSecondaryDocScanRequired: Boolean,
    val firstStep: String,
    val defaultFallbackStep: String?,
    val allowedRetries: Int,
    val fourthlineProvider: String?,
    val partnerSettings: PartnerSettingsDto?,
    val style: StyleDto?
) {
    constructor(initData: InitializationDto, infoData: InitializationInfoDto) : this(
        isTermsPreAccepted = infoData.termsAccepted,
        isPhoneNumberVerified = infoData.phoneVerified,
        isRemoteLoggingEnabled = infoData.sdkLogging ?: false,
        isSecondaryDocScanRequired = infoData.secondaryDocScanRequired ?: false,
        firstStep = initData.firstStep,
        defaultFallbackStep = initData.fallbackStep,
        allowedRetries = initData.allowedRetries,
        fourthlineProvider = initData.fourthlineProvider,
        partnerSettings = initData.partnerSettings,
        style = infoData.style
    )
}

interface ReadOnlyStorage<T> {
    fun get(): T
}

abstract class InMemoryReadOnlyStorage<T>(private val data: T): ReadOnlyStorage<T> {
    override fun get(): T {
        return data
    }
}

interface InitialConfigUseCase {
    suspend fun createInitialConfigStorage(): InitialConfigStorage
}

class InitialConfigStorage(data: IdenthubInitialConfig):
    InMemoryReadOnlyStorage<IdenthubInitialConfig>(data)

class InitialConfigUseCaseImpl(
    private val dataSource: InitializationDataSource
): InitialConfigUseCase {
    override suspend fun createInitialConfigStorage(): InitialConfigStorage {
        return InitialConfigStorage(fetchConfig())
    }

    private suspend fun fetchConfig(): IdenthubInitialConfig {
        return coroutineScope {
            val initDataCall = async { dataSource.fetchInitialization() }
            val infoDataCall = async { dataSource.fetchInitializationInfo() }
            IdenthubInitialConfig(initDataCall.await(), infoDataCall.await())
        }
    }
}

interface InitializationDataSource {
    suspend fun fetchInitialization(): InitializationDto
    suspend fun fetchInitializationInfo(): InitializationInfoDto
}

interface InitializationApi {
    @GET("/info")
    suspend fun getInitializationInfo(): InitializationInfoDto

    @GET(".")
    suspend fun getInitialization(): InitializationDto
}

class InitialConfigRetrofitDataSource(
    private val api: InitializationApi
): InitializationDataSource {

    override suspend fun fetchInitialization() = api.getInitialization()

    override suspend fun fetchInitializationInfo() = api.getInitializationInfo()

}