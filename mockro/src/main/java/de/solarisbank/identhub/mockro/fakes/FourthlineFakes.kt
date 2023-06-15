package de.solarisbank.identhub.mockro.fakes

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.identhub.mockro.shared.IdentificationChange
import de.solarisbank.identhub.mockro.shared.MockroIdentification
import de.solarisbank.identhub.mockro.shared.baseFakeIdent
import de.solarisbank.sdk.data.dto.*
import de.solarisbank.sdk.fourthline.data.dto.*
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadDataSource
import de.solarisbank.sdk.fourthline.data.location.LocationDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataSource
import de.solarisbank.sdk.fourthline.data.terms.TermsAndConditionsUseCase
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import io.reactivex.Single
import kotlinx.coroutines.delay
import java.net.URI
import java.util.*

class FakeFourthlineIdentificationDataSource : FourthlineIdentificationDataSource {
    override suspend fun createFourthlineIdentification(): IdentificationDto {
        return baseFakeIdent
    }

    override suspend fun createFourthlineSigningIdentification(): IdentificationDto {
        return baseFakeIdent
    }
}

class FakePersonDataSource : PersonDataSource {
    override suspend fun getPersonData(identificationId: String): PersonDataDto {
        return PersonDataDto(
            firstName = "Jane",
            lastName = "Doe",
            address = Address(
                "Baker Str.", "21", "London",
                "GB", "10001"
            ),
            email = "jane@doe.com",
            mobileNumber = "+15551234567",
            nationality = "GB",
            birthDate = "1988-01-01",
            birthPlace = "London",
            personUid = "personId",
            gender = "FEMALE",
            supportedDocuments = listOf(
                SupportedDocument("Passport", issuingCountries = listOf("GB")),
                SupportedDocument("National ID Card", issuingCountries = listOf("GB"))
            ),
            taxIdentification = TaxIdentification("ITA", "1234567890")
        )
    }
}

class FakeTermsUseCase: TermsAndConditionsUseCase {
    override suspend fun getNamirialTerms(): TermsAndConditions? {
        return TermsAndConditions("fakeDocId", "https://solarisgroup.com")
    }

    override suspend fun acceptNamirialTerms(documentId: String) {
        delay(1000)
    }
}

class FakeIpObtainingUseCase: IpObtainingUseCase {
    override suspend fun getMyIp(): IpDto {
        return IpDto("0.0.0.0")
    }
}

class FakeKycUploadDataSource : KycUploadDataSource {
    override fun uploadKYC(identificationId: String, fileUri: URI): Single<KycUploadResponseDto> {
        MockroIdentification.change(IdentificationChange.GoToQes)
        return Single.just(
            KycUploadResponseDto(
                id = "uploadedKycId",
                name = "Uploaded Kyc Data",
                contentType = "zip",
                documentType = "Passport",
                size = 10000,
                customerAccessible = false
            )
        )
    }
}

class FakeKycInfoUseCase : KycInfoUseCase {
    override val selfieResultCroppedBitmapLiveData = MutableLiveData<Bitmap>()

    override suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto) {}

    override suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        selfieResultCroppedBitmapLiveData.value = result.image.cropped
    }

    override suspend fun updateKycInfoWithDocumentScannerStepResult(
        docType: DocumentType,
        result: DocumentScannerStepResult,
        isSecondaryDocument: Boolean,
    ) {
    }

    override suspend fun updateKycInfoWithDocumentScannerResult(
        docType: DocumentType,
        result: DocumentScannerResult,
    ) {
    }

    override suspend fun updateIssueDate(issueDate: Date) {}

    override suspend fun updateExpireDate(expireDate: Date) {}

    override suspend fun updateDocumentNumber(number: String) {}

    override suspend fun updateIpAddress(ipAddress: String) {}

    override suspend fun getKycDocument(): Document {
        return Document()
    }

    override suspend fun updateKycLocation(resultLocation: Location) {}

    override suspend fun createKycZip(applicationContext: Context): ZipCreationStateDto {
        return ZipCreationStateDto.SUCCESS(URI.create(""))
    }
}

class FakeLocationDataSource : LocationDataSource {
    override fun getLocation(): Single<LocationResult> {
        return Single.just(
            LocationResult.Success(Location(0.0, 0.0))
        )
    }
}