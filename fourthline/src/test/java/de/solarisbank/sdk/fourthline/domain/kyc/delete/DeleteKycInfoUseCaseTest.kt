package de.solarisbank.sdk.fourthline.domain.kyc.delete

import android.content.Context
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.di.FourthlineTestComponent
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

class DeleteKycInfoUseCaseTest : StringSpec ({

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("request.path : ${request.path}")
            return when (request.path) {
                else -> {
                    MockResponse().apply {
                        setResponseCode(400)
                    }
                }
            }
        }
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    val directoryMockk = mockk<File>() {
        every { isDirectory } returns true
        every { name } returns "fourthline"
        every { deleteRecursively() } returns true
    }

    val contextMockk = mockk<Context>() {
        every { cacheDir } returns directoryMockk
    }

    fun initDependencies(
        mockedIdentificationId: String = "",
        defaultToFallbackStepParam: Boolean = false
    ): DeleteKycInfoUseCase {

        val identificationDtoMockk =  mockk<IdentificationDto>(relaxed = true) {
            every { id } returns mockedIdentificationId
            every { status = Status.UPLOAD.label } returns Unit
        }

        mockkConstructor(DeleteKycInfoUseCaseFactory::class)
        every { anyConstructed<DeleteKycInfoUseCaseFactory>().get() } returns
                DeleteKycInfoUseCase(contextMockk)
        mockkConstructor(FourthlineTestComponent.ContextProvider::class)
        every { anyConstructed<FourthlineTestComponent.ContextProvider>().get() } returns
                contextMockk

        mockkConstructor(IdentificationInMemoryDataSource::class)
        every { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() } returns
                Single.just(identificationDtoMockk)
        every { anyConstructed<IdentificationInMemoryDataSource>().saveIdentification(any()) } returns
                Completable.complete()

        mockkConstructor(LocationDataSourceFactory::class)
        every { anyConstructed<LocationDataSourceFactory>().get() } returns
                mockk<LocationDataSourceImpl>()

        return FourthlineTestComponent.getInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer)
                .provideNetworkModule()
        ).deleteKycInfoUseCaseProvider.get()
    }

    "checkClearPersonDataCaches" {
        val deleteKycInfoUseCase = initDependencies()
        deleteKycInfoUseCase.clearPersonDataCaches()
        verify { contextMockk.cacheDir }
        //todo checking could be extended
//        verify { directoryMockk.deleteRecursively() }
    }

})