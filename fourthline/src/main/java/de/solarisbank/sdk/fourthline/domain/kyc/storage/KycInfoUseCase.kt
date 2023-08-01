package de.solarisbank.sdk.fourthline.domain.kyc.storage

import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.kyc.zipper.ZipperError
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoDataSource
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.logger.IdLogger
import java.util.*

class KycInfoUseCase(
    private val kycInfoDataSource: KycInfoDataSource,
    private val kycInfoZipper: KycInfoZipper,
    private val initialConfigStorage: InitialConfigStorage
) {
    suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto) {
        kycInfoDataSource.updateWithPersonDataDto(personDataDto, initialConfigStorage.get().fourthlineProvider!!)
    }

    suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        kycInfoDataSource.updateKycWithSelfieScannerResult(result)
    }

    /**
     * Retains document pages' photos and stores them to map
     * Called from DocScanFragment.onStepSuccess()
     */
    suspend fun updateKycInfoWithDocumentScannerStepResult(
        docType: DocumentType,
        result: DocumentScannerStepResult,
        isSecondaryDocument: Boolean
    ) {
        if (isSecondaryDocument) {
            kycInfoDataSource.updateKycInfoWithDocumentSecondaryScan(docType, result)
        } else {
            kycInfoDataSource.updateKycInfoWithDocumentScannerStepResult(docType, result)
        }
    }


    /**
     * Retains recognized String data of the documents
     */
    suspend fun updateKycInfoWithDocumentScannerResult(
        docType: DocumentType,
        result: DocumentScannerResult
    ) {
        kycInfoDataSource.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    suspend fun updateExpireDate(expireDate: Date) {
        kycInfoDataSource.updateExpireDate(expireDate)
    }

    suspend fun updateDocumentNumber(number: String) {
        kycInfoDataSource.updateDocumentNumber(number)
    }

    suspend fun updateIpAddress(ipAddress: String) {
        kycInfoDataSource.updateIpAddress(ipAddress)
    }

    /**
     * Provides @Document for display
     */
    suspend fun getKycDocument(): Document {
        return kycInfoDataSource.getKycDocument()
    }

    suspend fun updateKycLocation(resultLocation: Location) {
        kycInfoDataSource.updateKycLocation(resultLocation)
    }


    suspend fun createKycZip(): ZipCreationStateDto {
        val kycInfo = kycInfoDataSource.finalizeAndGetKycInfo()
        IdLogger.info("Zipping of Kyc info started")

        try {
            val zipURI = kycInfoZipper.zip(kycInfo)
            return ZipCreationStateDto.Success(zipURI)
        } catch (zipperError: ZipperError) {
            IdLogger.error("Zipper error : ${zipperError::class.java.name}")
        }
        return ZipCreationStateDto.Error
    }

}
