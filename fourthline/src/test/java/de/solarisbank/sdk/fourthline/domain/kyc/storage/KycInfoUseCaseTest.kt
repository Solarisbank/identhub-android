package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.fourthline.core.DocumentType
import com.fourthline.kyc.KycInfo
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.data.dto.PartnerSettingsDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.dto.SupportedDocument
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoInMemoryDataSource
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import java.util.*

class KycInfoUseCaseTest : StringSpec ({

    val fourthlineProviderTestValue = "testPartner"

    fun initKycInfoUseCase(): KycInfoUseCase {

        mockkConstructor(MutableLiveData::class)
        every { anyConstructed<MutableLiveData<Bitmap>>().setValue(any()) } returns Unit

        val storage = InitialConfigStorage(
            IdenthubInitialConfig(
                isTermsPreAccepted = true,
                isPhoneNumberVerified = true,
                isRemoteLoggingEnabled = false,
                isSecondaryDocScanRequired = false,
                isOrcaEnabled = false,
                firstStep = "",
                defaultFallbackStep = null,
                allowedRetries = 5,
                fourthlineProvider = fourthlineProviderTestValue,
                partnerSettings = PartnerSettingsDto(false),
                style = null
            )
        )

        return KycInfoUseCase(KycInfoInMemoryDataSource(), KycInfoZipperImpl(mockk()), storage)
    }

    "checkUpdateWithPersonDataDto" {
        val personDataDto =
            mockk<PersonDataDto> {
                every { firstName } returns "firstName"
                every { lastName } returns "lastName"
                every { address } returns
                        mockk {
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
                every { taxIdentification } returns null
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
        val metadataMockk = mockk<com.fourthline.vision.selfie.SelfieScannerMetadata> {
            every { timestamp } returns Date()
            every { location } returns mockk {
                every { latitude } returns 1111.0
                every { longitude } returns 1111.0
            }
        }
        val selfieScannerResult = mockk<SelfieScannerResult> {
            every { image } returns mockk {
                every { full } returns mockk()
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
            mockk<com.fourthline.vision.document.DocumentScannerStepMetadata> {
                every { timestamp } returns Date()
                every { location } returns mockk {
                    every { latitude } returns 1111.0
                    every { longitude } returns 2222.0
                }
                every { fileSide } returns mockk()
                every { isAngled } returns false andThen true andThen false andThen true
            }
        val documentScannerStepResultMockk = mockk<DocumentScannerStepResult> {
            every { metadata } returns metadataMockk
            every { image } returns mockk {
                every { full } returns mockk()
                every { cropped } returns mockk()
            }
        }
        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateKycInfoWithDocumentScannerStepResult(
            DocumentType.PASSPORT,
            documentScannerStepResultMockk,
            false
        )

        verify { metadataMockk.timestamp }
        verify { metadataMockk.location }
        verify { documentScannerStepResultMockk.image }
    }

    "checkUpdateKycLocation" {
        val metadataMockk = mockk<com.fourthline.core.DeviceMetadata> {
            every { location = any() } returns Unit
        }
        mockkConstructor(KycInfo::class)
        every { anyConstructed<KycInfo>().metadata } returns metadataMockk

        val location = Location(2222.0, 1111.0)

        val kycInfoUseCase = initKycInfoUseCase()
        kycInfoUseCase.updateKycLocation(location)
        verify { metadataMockk.location = any() }
    }

    "checkUpdateExpirationDate" {
        val documentMockk = mockk<com.fourthline.kyc.Document> {
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
        val documentMockk = mockk<com.fourthline.kyc.Document> {
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