package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.identhub.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.domain.model.result.data
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class GetDocumentsUseCaseTest : StringSpec ({

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            return MockResponse().apply {
                setResponseCode(200)
            }
        }
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    val mockkDocumentDto = mockk<DocumentDto>()

    val docList = listOf<DocumentDto>(mockkDocumentDto, mockkDocumentDto, mockkDocumentDto)

    val mockkIdentificationDtoListContained = mockk<IdentificationDto>() {
        every { documents } returns listOf<DocumentDto>() andThen listOf<DocumentDto>() andThen docList andThen docList
    }

    beforeSpec {

    }

    "documentsAbsentInIdentificationInMemoryDataSource"{

        mockkConstructor(IdentificationInMemoryDataSource::class)

        val mockkIdentificationDtoListContained = mockk<IdentificationDto>() {
            every { documents } returns listOf<DocumentDto>()
        }

        every {
            anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto()
        } returns Single.just(mockkIdentificationDtoListContained)
        var getDocumentsUseCase = IdentHubTestComponent
            .getTestInstance(
                networkModule = NetworkModuleTestFactory(mockWebServer)
                    .provideNetworkModule(),
            ).getDocumentsUseCaseProvider.get()

        val resultList = getDocumentsUseCase!!.execute(Unit).blockingGet().data
        assertTrue(resultList!!.isEmpty())
        verify { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() }
    }
    "documentsPresentInIdentificationInMemoryDataSource"{

        mockkConstructor(IdentificationInMemoryDataSource::class)

        val mockkIdentificationDtoListContained = mockk<IdentificationDto>() {
            every { documents } returns docList
        }

        every {
            anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto()
        } returns Single.just(mockkIdentificationDtoListContained)
        var getDocumentsUseCase = IdentHubTestComponent
            .getTestInstance(
                networkModule = NetworkModuleTestFactory(mockWebServer)
                    .provideNetworkModule(),
            ).getDocumentsUseCaseProvider.get()

        val resultList = getDocumentsUseCase!!.execute(Unit).blockingGet().data
        assertEquals(docList, resultList)
        verify { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() }
    }

    afterSpec {
//        mockWebServer.shutdown()
    }


})