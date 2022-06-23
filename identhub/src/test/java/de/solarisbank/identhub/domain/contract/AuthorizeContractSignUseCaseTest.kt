package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.identhub.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class AuthorizeContractSignUseCaseTest : StringSpec({

    val mockedIdentificationInMemoryDataSource = mockk<IdentificationInMemoryDataSource>() {
        every { obtainIdentificationDto() } returns Single.just(mockk<IdentificationDto>())
    }

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            return MockResponse()
        }
    }

    val identificationModule = mockk<IdentificationModule>(relaxed = true) {
        every { provideIdentificationLocalDataSource() } returns
                DoubleCheck.provider(object : Factory<IdentificationInMemoryDataSource> {
                    override fun get(): IdentificationInMemoryDataSource {
                        return mockedIdentificationInMemoryDataSource
                    }
                })
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    val authorizeContractSignUseCase = IdentHubTestComponent
        .getTestInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer).provideNetworkModule(),
            identificationModule = identificationModule,
        )
        .authorizeContractSignUseCaseProvider
        .get()

    //todo think about involving here an usecase that stores the identification Dto in this cache
    "Getting strored identificationDto" {
        authorizeContractSignUseCase.execute(Unit)
        verify { mockedIdentificationInMemoryDataSource.obtainIdentificationDto() }
    }

})