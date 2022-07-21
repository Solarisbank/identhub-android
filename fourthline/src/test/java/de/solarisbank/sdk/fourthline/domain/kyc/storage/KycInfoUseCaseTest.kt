package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.fourthline.core.DocumentType
import com.fourthline.kyc.KycInfo
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationInMemoryDataSource
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.Address
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.dto.SupportedDocument
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.di.FourthlineTestComponent
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.*

class KycInfoUseCaseTest : StringSpec ({

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

    val fourthlineProviderTestValue = "testPartner"

    mockkConstructor(IdentityInitializationInMemoryDataSource::class)
    every { anyConstructed<IdentityInitializationInMemoryDataSource>().getInitializationDto() } returns
            mockk {
                every { partnerSettings } returns
                        mockk {
                            every { defaultToFallbackStep } returns
                                    false
                        }
                every { fourthlineProvider } returns
                        fourthlineProviderTestValue
            }

    fun initKycInfoUseCase(): KycInfoUseCase {

        mockkConstructor(LocationDataSourceFactory::class)
        every { anyConstructed<LocationDataSourceFactory>().get() } returns
                mockk<LocationDataSourceImpl>()

        mockkConstructor(MutableLiveData::class)
        every { anyConstructed<MutableLiveData<Bitmap>>().setValue(any()) } returns Unit



        return FourthlineTestComponent.getInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer)
                .provideNetworkModule()
        ).kycInfoUseCaseProvider.get()
    }

    "checkUpdateWithPersonDataDto" {
        val personDataDto =
            mockk<PersonDataDto> {
                every { firstName } returns "firstName"
                every { lastName } returns "lastName"
                every { address } returns
                        mockk<Address>() {
                            every { street } returns "street"
                            every { streetNumber } returns "2"
                            every { city } returns "city"
                            every { country } returns "country"
                            every { city } returns "city"
                            every { country } returns "country"
                            every { postalCode } returns "11111"
                        }
                every { email } returns "email@email.com"
                every { mobileNumber } returns "+000000000000"
                every { nationality } returns "DE"
                every { birthDate } returns "25.11.1989"
                every { birthPlace } returns "birthPlace"
                every { personUid } returns "personUid1234"
                every { supportedDocuments } returns
                        listOf(SupportedDocument("docType", listOf("DE")) )
                every { gender } returns "male"
            }

        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateWithPersonDataDto(personDataDto)

//        verify { identityInitializationInMemoryDataSource.getInitializationDto() }
        verify { personDataDto.firstName }
        verify { personDataDto.lastName }
        verify { personDataDto.gender }
        verify { personDataDto.birthDate }
        verify { personDataDto.nationality }
        verify { personDataDto.birthPlace }
    }

    "checkUpdateKycWithSelfieScannerResult"{
        val metadataMockk = mockk<com.fourthline.vision.selfie.SelfieScannerMetadata>() {
            every { timestamp } returns Date()
            every { location } returns mockk() {
                every { first } returns 1111.0
                every { second } returns 1111.0
            }
        }
        val selfieScannerResult = mockk<SelfieScannerResult>() {
            every { image } returns mockk<com.fourthline.vision.ScannerImage>() {
                every { full } returns mockk<Bitmap>()
                every { cropped } returns mockk()
            }
            every { metadata } returns metadataMockk
            every { videoRecording } returns mockk()
        }
        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateKycWithSelfieScannerResult(selfieScannerResult)

        verify { metadataMockk.timestamp }
        //todo extend verifying
    }

    "checkUpdateKycInfoWithDocumentScannerStepResult" {
        val metadataMockk =
            mockk<com.fourthline.vision.document.DocumentScannerStepMetadata>() {
                every { timestamp } returns Date()
                every { location } returns mockk() {
                    every { first } returns 1111.0
                    every { second } returns 2222.0
                }
                every { fileSide } returns mockk<com.fourthline.core.DocumentFileSide>()
                every { isAngled } returns false andThen true andThen false andThen true
            }
        val documentScannerStepResultMockk = mockk<DocumentScannerStepResult>() {
            every { metadata } returns metadataMockk
            every { image } returns mockk<com.fourthline.vision.ScannerImage>() {
                every { full } returns mockk()
                every { cropped } returns mockk()
            }
        }
        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateKycInfoWithDocumentScannerStepResult(
            DocumentType.PASSPORT,
            documentScannerStepResultMockk
        )

        verify { metadataMockk.timestamp }
        verify { metadataMockk.location }
        verify { documentScannerStepResultMockk.image }
    }

    "checkUpdateKycLocation" {
        val metadataMockk = mockk<com.fourthline.kyc.DeviceMetadata>() {
            every { location = any() } returns Unit
        }
        mockkConstructor(KycInfo::class)
        every { anyConstructed<KycInfo>().metadata } returns metadataMockk

        val locationMockk = mockk<Location>() {
            every { latitude } returns 2222.0
            every { longitude } returns 1111.0
        }

        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateKycLocation(locationMockk)
        verify { metadataMockk.location = any() }
    }

    "checkUpdateIssueDate" {
        val documentMockk = mockk<com.fourthline.kyc.Document>() {
            every { issueDate = any() } returns Unit
        }
        mockkConstructor(KycInfo::class)
        every { anyConstructed<KycInfo>().document } returns documentMockk


        val issueDateMockk = mockk<Date>()
        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateIssueDate(issueDateMockk)
        verify { documentMockk.issueDate = issueDateMockk }
    }

    "checkUpdateExpirationDate" {
        val documentMockk = mockk<com.fourthline.kyc.Document>() {
            every { expirationDate = any() } returns Unit
        }
        mockkConstructor(KycInfo::class)
        every { anyConstructed<KycInfo>().document } returns documentMockk


        val expirationDateMockk = mockk<Date>()
        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateExpireDate(expirationDateMockk)
        verify { documentMockk.expirationDate = expirationDateMockk }
    }

    "checkUpdateDocumentNumber" {
        val documentMockk = mockk<com.fourthline.kyc.Document>() {
            every { number = any() } returns Unit
        }
        mockkConstructor(KycInfo::class)
        every { anyConstructed<KycInfo>().document } returns documentMockk

        val documentNumber = "AS123456"

        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateDocumentNumber(documentNumber)
        verify { documentMockk.number = documentNumber }
    }

})