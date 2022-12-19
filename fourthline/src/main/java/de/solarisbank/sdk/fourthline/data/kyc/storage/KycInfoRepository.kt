package de.solarisbank.sdk.fourthline.data.kyc.storage

import android.graphics.Bitmap
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.kyc.KycInfo
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.dto.Location
import java.util.*

class KycInfoRepository(private val kycInfoInMemoryDataSource: KycInfoInMemoryDataSource) {

    suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto, providerName: String) {
        kycInfoInMemoryDataSource.updateWithPersonDataDto(personDataDto, providerName)
    }
    suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        kycInfoInMemoryDataSource.updateKycWithSelfieScannerResult(result)
    }

    suspend fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        kycInfoInMemoryDataSource.updateKycInfoWithDocumentScannerStepResult(docType, result)
    }

    suspend fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        kycInfoInMemoryDataSource.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    suspend fun updateKycLocation(resultLocation: Location) {
        kycInfoInMemoryDataSource.updateKycLocation(resultLocation)
    }

    suspend fun updateIssueDate(issueDate: Date) {
        kycInfoInMemoryDataSource.updateIssueDate(issueDate)
    }

    suspend fun updateExpireDate(expireDate: Date) {
        kycInfoInMemoryDataSource.updateExpireDate(expireDate)
    }

    suspend fun updateDocumentNumber(number: String) {
        kycInfoInMemoryDataSource.updateDocumentNumber(number)
    }

    suspend fun updateIpAddress(ipAddress: String) {
        kycInfoInMemoryDataSource.updateIpAddress(ipAddress)
    }

    suspend fun getKycDocument(): Document {
        return kycInfoInMemoryDataSource.getKycDocument()
    }

    suspend fun getKycInfo(): KycInfo {
        return kycInfoInMemoryDataSource.getKycInfo()
    }

}