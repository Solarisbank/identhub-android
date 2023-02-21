package de.solarisbank.sdk.data.initial

import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.dto.PartnerSettingsDto
import de.solarisbank.sdk.data.dto.StyleDto
import io.reactivex.Single
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
)

interface ReadOnlyStorage<T> {
    fun get(): T
}

abstract class InMemoryReadOnlyStorage<T>(private val data: T): ReadOnlyStorage<T> {
    override fun get(): T {
        return data
    }
}

interface InitialConfigUseCase {
    fun createInitialConfigStorage(): Single<InitialConfigStorage>
}

class InitialConfigStorage(data: IdenthubInitialConfig):
    InMemoryReadOnlyStorage<IdenthubInitialConfig>(data)

class InitialConfigUseCaseImpl(
    private val dataSource: InitializationDataSource
): InitialConfigUseCase {
    override fun createInitialConfigStorage(): Single<InitialConfigStorage> {
        return fetchConfig()
            .map { InitialConfigStorage(it) }
    }

    private fun fetchConfig(): Single<IdenthubInitialConfig> {
        return Single.zip(
            dataSource.fetchInitialization(),
            dataSource.fetchInitializationInfo()
        ) { initData, infoData ->
            IdenthubInitialConfig(
                isTermsPreAccepted = infoData.termsAccepted,
                isPhoneNumberVerified = infoData.phoneVerified,
                isRemoteLoggingEnabled = infoData.sdkLogging ?: false,
                isSecondaryDocScanRequired = infoData.secondaryDocScanRequired == false,
                firstStep = initData.firstStep,
                defaultFallbackStep = initData.fallbackStep,
                allowedRetries = initData.allowedRetries,
                fourthlineProvider = initData.fourthlineProvider,
                partnerSettings = initData.partnerSettings,
                style = infoData.style
            )
        }
    }
}

interface InitializationDataSource {
    fun fetchInitialization(): Single<InitializationDto>
    fun fetchInitializationInfo(): Single<InitializationInfoDto>
}

interface InitializationApi {
    @GET("/info")
    fun getInitializationInfo(): Single<InitializationInfoDto>

    @GET(".")
    fun getInitialization(): Single<InitializationDto>
}

class InitialConfigRetrofitDataSource(
    private val api: InitializationApi
): InitializationDataSource {

    override fun fetchInitialization() = api.getInitialization()

    override fun fetchInitializationInfo() = api.getInitializationInfo()

}