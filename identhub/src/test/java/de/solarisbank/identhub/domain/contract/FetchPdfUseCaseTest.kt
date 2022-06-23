package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.data.contract.ContractSignRetrofitDataSource
import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.identhub.file.FileController
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.identhub.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.DocumentDto
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.BufferedSource
import retrofit2.Response
import java.io.File

class FetchPdfUseCaseTest : StringSpec({

    val absentDocument = "absentDocument"
    val absentDocumentName = "absentDocumentName"
    val networkDocumentId = "networkDocumentId"
    val pdfContent = "pdfContent"

    val mockkDocument = mockk<DocumentDto>(relaxed = true) {
        every { id } returns networkDocumentId
        every { name } returns absentDocumentName
    }

    val mockkFile = mockk<File>(relaxed = true)

    val mockkFileController = mockk<FileController>() {
        every { retrieve(absentDocumentName) } returns null
        every { save(any(), any()) } returns mockkFile
     }

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("FetchPdfUseCaseTest, dispatch, request : ${request.body}, request.path : $request.path")
            return if (request.path!!.contains("sign_documents/networkDocumentId/download")) {

                    //todo add logforj to tests
                    println("FetchPdfUseCaseTest, dispatch")
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(pdfContent)
                    }
                } else {
                    MockResponse().apply {
                        setResponseCode(300)
                        setBody(pdfContent)
                    }
            }
        }
    }

    val mockkBufferedSource = mockk<BufferedSource>()

    val mockkResponseBody = mockk<ResponseBody>() {
        every { source() } returns mockkBufferedSource
    }
    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    var contractSignRepository: ContractSignRepository
    var fetchPdfUseCase: FetchPdfUseCase? = null

   beforeSpec {
       mockkConstructor(ContractSignRetrofitDataSource::class)
       mockkConstructor(IdentificationInMemoryDataSource::class)

       every { anyConstructed<IdentificationInMemoryDataSource>().saveIdentification(any()) }returns
               Completable.complete()

       every { anyConstructed<ContractSignRetrofitDataSource>().fetchDocumentFile(any()) } returns
               Single.just(Response.success(200, mockkResponseBody))


       println("FetchPdfUseCaseTest, beforeSpec")
       mockWebServer.start(0)
       contractSignRepository = IdentHubTestComponent
           .getTestInstance(
               networkModule = NetworkModuleTestFactory(mockWebServer).provideNetworkModule(),
           )
           .contractSignRepositoryProvider
           .get()
       fetchPdfUseCase = FetchPdfUseCase(contractSignRepository, mockkFileController)

   }

    afterSpec {
        println("FetchPdfUseCaseTest, afterSpecSpec")
        mockWebServer.shutdown()
    }


    //todo add comment and more cases
    "fetchDocumentRemotely"{
        fetchPdfUseCase!!.execute(mockkDocument)
            .subscribe(
                { print("FetchPdfUseCaseTest result 1") },
                { print("FetchPdfUseCaseTest result 2") }
            )
        verify { mockkFileController.save(any(), any()) }
        verify { anyConstructed<ContractSignRetrofitDataSource>().fetchDocumentFile(any()) }
    }

})