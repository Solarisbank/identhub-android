package de.solarisbank.sdk.fourthline.feature.service.kyc.upload

import android.content.Context
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor
import de.solarisbank.identhub.data.room.IdentityRoomDatabase
import de.solarisbank.identhub.data.session.SessionModule
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.data.session.factory.ProvideSessionUrlRepositoryFactory
import de.solarisbank.identhub.data.session.factory.SessionUrlLocalDataSourceFactory
import de.solarisbank.identhub.di.database.DatabaseModule
import de.solarisbank.identhub.di.database.DatabaseModuleProvideIdentificationDaoFactory
import de.solarisbank.identhub.di.database.DatabaseModuleProvideRoomFactory
import de.solarisbank.identhub.di.network.*
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.core.di.internal.DoubleCheck
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationApi
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationApiFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRetrofitDataSourceFactory
import de.solarisbank.sdk.fourthline.data.identification.factory.ProvideFourthlineIdentificationRoomDataSourceFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadApi
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProvideKycUploadDataSourceFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProviderKycUploadApiFactory
import de.solarisbank.sdk.fourthline.data.kyc.upload.factory.ProviderKycUploadRepositoryFactory
import de.solarisbank.sdk.fourthline.di.FourthlineComponent
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCaseFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class KycUploadServiceComponent private constructor(
        private val libraryComponent: LibraryComponent,
        private val databaseModule: DatabaseModule,
        private val networkModule: NetworkModule,
        private val sessionModule: SessionModule,
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val kycUploadModule: KycUploadModule
) {


    private lateinit var applicationContextProvider: Provider<Context>
    private lateinit var retrofitProvider: Provider<Retrofit>
    private lateinit var moshiConverterFactoryProvider: Provider<MoshiConverterFactory>
    private lateinit var okHttpClientProvider: Provider<OkHttpClient>
    private lateinit var rxJavaCallAdapterFactoryProvider: Provider<CallAdapter.Factory>

    private lateinit var fourthlineIdentificationApiProvider: Provider<FourthlineIdentificationApi>
    private lateinit var identificationDaoProvider: Provider<IdentificationDao>
    private lateinit var fourthlineIdentificationRoomDataSourceProvider: Provider<FourthlineIdentificationRoomDataSource>
    private lateinit var fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>
    private lateinit var sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
    private lateinit var sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
    private lateinit var dynamicBaseUrlInterceptorProvider: Provider<DynamicBaseUrlInterceptor>
    private lateinit var httpLoggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    private lateinit var userAgentInterceptorProvider: Provider<UserAgentInterceptor>

    private lateinit var identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
    private lateinit var kycUploadApiProvider: Provider<KycUploadApi>
    private lateinit var kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>
    private lateinit var kycUploadRepositoryProvider: Provider<KycUploadRepository>
    private lateinit var kycUploadUseCaseProvider: Provider<KycUploadUseCase>

    init {
        initialize()
    }

    private fun initialize() {
        applicationContextProvider = FourthlineComponent.ApplicationContextProvider(libraryComponent)
        moshiConverterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideMoshiConverterFactory.create(networkModule))
        sessionUrlLocalDataSourceProvider = DoubleCheck.provider(SessionUrlLocalDataSourceFactory.create(sessionModule))
        sessionUrlRepositoryProvider = DoubleCheck.provider(ProvideSessionUrlRepositoryFactory.create(sessionModule, sessionUrlLocalDataSourceProvider))
        dynamicBaseUrlInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideDynamicUrlInterceptorFactory.create(networkModule, sessionUrlRepositoryProvider))
        userAgentInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideUserAgentInterceptorFactory.create(networkModule))
        httpLoggingInterceptorProvider = DoubleCheck.provider(NetworkModuleProvideHttpLoggingInterceptorFactory.create(networkModule))
        okHttpClientProvider = DoubleCheck.provider(NetworkModuleProvideOkHttpClientFactory.create(networkModule, dynamicBaseUrlInterceptorProvider, userAgentInterceptorProvider, httpLoggingInterceptorProvider))
        rxJavaCallAdapterFactoryProvider = DoubleCheck.provider(NetworkModuleProvideRxJavaCallAdapterFactory.create(networkModule))
        retrofitProvider = DoubleCheck.provider(NetworkModuleProvideRetrofitFactory.create(networkModule, moshiConverterFactoryProvider, okHttpClientProvider, rxJavaCallAdapterFactoryProvider))
        fourthlineIdentificationApiProvider = DoubleCheck.provider(ProvideFourthlineIdentificationApiFactory.create(fourthlineIdentificationModule, retrofitProvider))
        fourthlineIdentificationRetrofitDataSourceProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRetrofitDataSourceFactory.create(fourthlineIdentificationModule, fourthlineIdentificationApiProvider))
        kycUploadApiProvider = DoubleCheck.provider(ProviderKycUploadApiFactory.create(kycUploadModule, retrofitProvider))
        kycUploadRetrofitDataSourceProvider = DoubleCheck.provider(ProvideKycUploadDataSourceFactory.create(kycUploadModule, kycUploadApiProvider))
        identityRoomDatabaseProvider = DoubleCheck.provider(DatabaseModuleProvideRoomFactory.create(databaseModule, applicationContextProvider))
        identificationDaoProvider = DoubleCheck.provider(DatabaseModuleProvideIdentificationDaoFactory.create(databaseModule, identityRoomDatabaseProvider))
        fourthlineIdentificationRoomDataSourceProvider = DoubleCheck.provider(ProvideFourthlineIdentificationRoomDataSourceFactory.create(fourthlineIdentificationModule, identificationDaoProvider))
        kycUploadRepositoryProvider = DoubleCheck.provider(ProviderKycUploadRepositoryFactory.create(kycUploadModule, fourthlineIdentificationRetrofitDataSourceProvider, fourthlineIdentificationRoomDataSourceProvider, kycUploadRetrofitDataSourceProvider, sessionUrlLocalDataSourceProvider))
        kycUploadUseCaseProvider = KycUploadUseCaseFactory.create(kycUploadRepositoryProvider, sessionUrlRepositoryProvider)
    }

    fun inject(kycUploadService: KycUploadService) {
        kycUploadService.kycUploadUseCase = kycUploadUseCaseProvider.get()
    }

    companion object {
        private val lock = Any()
        private var kycUploadServiceComponent: KycUploadServiceComponent? = null

        fun getInstance(libraryComponent: LibraryComponent): KycUploadServiceComponent {
            synchronized(lock) {
                if (kycUploadServiceComponent == null) {
                    kycUploadServiceComponent = KycUploadServiceComponent(
                            networkModule = NetworkModule(),
                            sessionModule = SessionModule(),
                            fourthlineIdentificationModule = FourthlineIdentificationModule(),
                            kycUploadModule = KycUploadModule(),
                            databaseModule = DatabaseModule(),
                            libraryComponent = libraryComponent
                    )
                }
            }
            return kycUploadServiceComponent!!
        }
    }
}