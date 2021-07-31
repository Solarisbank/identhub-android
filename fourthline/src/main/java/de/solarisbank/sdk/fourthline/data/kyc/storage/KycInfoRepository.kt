package de.solarisbank.sdk.fourthline.data.kyc.storage

import android.graphics.Bitmap
import android.location.Location
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.kyc.KycInfo
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import java.util.*

class KycInfoRepository(private val kycInfoInMemoryDataSource: KycInfoInMemoryDataSource) {

    fun updateWithPersonDataDto(personDataDto: PersonDataDto, providerName: String) {
        kycInfoInMemoryDataSource.updateWithPersonDataDto(personDataDto, providerName)
    }
    fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        kycInfoInMemoryDataSource.updateKycWithSelfieScannerResult(result)
    }

    fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        kycInfoInMemoryDataSource.updateKycInfoWithDocumentScannerStepResult(docType, result)
    }

    fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        kycInfoInMemoryDataSource.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    fun updateKycLocation(resultLocation: Location) {
        kycInfoInMemoryDataSource.updateKycLocation(resultLocation)
    }

    fun updateIssueDate(issueDate: Date) {
        kycInfoInMemoryDataSource.updateIssueDate(issueDate)
    }

    fun updateExpireDate(expireDate: Date) {
        kycInfoInMemoryDataSource.updateExpireDate(expireDate)
    }

    fun updateDocumentNumber(number: String) {
        kycInfoInMemoryDataSource.updateDocumentNumber(number)
    }

    fun getPersonData(): PersonDataDto? {
        return kycInfoInMemoryDataSource.getPersonData()
    }

    fun getSelfieFullImage(): Bitmap? {
        return kycInfoInMemoryDataSource.getSelfieFullImage()
    }

    fun getKycDocument(): Document {
        return kycInfoInMemoryDataSource.getKycDocument()
    }

    fun getKycInfo(): KycInfo {
        return kycInfoInMemoryDataSource.getKycInfo()
    }

}