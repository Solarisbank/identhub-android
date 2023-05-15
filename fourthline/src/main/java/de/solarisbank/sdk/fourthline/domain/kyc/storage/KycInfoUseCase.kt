package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthline.core.DocumentType
import com.fourthline.kyc.Document
import com.fourthline.kyc.KycInfo
import com.fourthline.kyc.zipper.Zipper
import com.fourthline.kyc.zipper.ZipperError
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepository
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.logger.IdLogger
import timber.log.Timber
import java.util.*

interface KycInfoUseCase {
    val selfieResultCroppedBitmapLiveData: LiveData<Bitmap>
    suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto)
    suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult)
    suspend fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult, isSecondaryDocument: Boolean)
    suspend fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult)
    suspend fun updateIssueDate(issueDate: Date)
    suspend fun updateExpireDate(expireDate: Date)
    suspend fun updateDocumentNumber(number: String)
    suspend fun updateIpAddress(ipAddress: String)
    suspend fun getKycDocument(): Document
    suspend fun updateKycLocation(resultLocation: Location)
    suspend fun createKycZip(applicationContext: Context): ZipCreationStateDto
}

class KycInfoUseCaseImpl(
        private val kycInfoRepository: KycInfoRepository,
        private val initialConfigStorage: InitialConfigStorage
): KycInfoUseCase {
    //todo remove livaedata from usecase
    private var _selfieResultCroppedBitmapLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    override val selfieResultCroppedBitmapLiveData = _selfieResultCroppedBitmapLiveData as LiveData<Bitmap>


    override suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto) {
        kycInfoRepository.updateWithPersonDataDto(personDataDto, initialConfigStorage.get().fourthlineProvider!!)
    }

    @SuppressLint("BinaryOperationInTimber")
    override suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        Timber.d("updateKycWithSelfieScannerResult : " +
                "\ntimestamp:${result.metadata.timestamp}" +
                "\nlocation?.first: ${result.metadata.location?.first}" +
                "\nlocation?.second: ${result.metadata.location?.second}"
        )
        kycInfoRepository.updateKycWithSelfieScannerResult(result)
        _selfieResultCroppedBitmapLiveData.value = result.image.cropped
    }

    /**
     * Retains document pages' photos and stores them to map
     * Called from DocScanFragment.onStepSuccess()
     */
    @SuppressLint("BinaryOperationInTimber")
    override suspend fun updateKycInfoWithDocumentScannerStepResult(
        docType: DocumentType,
        result: DocumentScannerStepResult,
        isSecondaryDocument: Boolean
    ) {
        kycInfoRepository.updateKycInfoWithDocumentScannerStepResult(docType, result, isSecondaryDocument)
    }


    /**
     * Retains recognized String data of the documents
     */
    override suspend fun updateKycInfoWithDocumentScannerResult(
        docType: DocumentType,
        result: DocumentScannerResult
    ) {
        kycInfoRepository.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    override suspend fun updateIssueDate(issueDate: Date) {
        kycInfoRepository.updateIssueDate(issueDate)
    }

    override suspend fun updateExpireDate(expireDate: Date) {
        kycInfoRepository.updateExpireDate(expireDate)
    }

    override suspend fun updateDocumentNumber(number: String) {
        kycInfoRepository.updateDocumentNumber(number)
    }

    override suspend fun updateIpAddress(ipAddress: String) {
        kycInfoRepository.updateIpAddress(ipAddress)
    }

    /**
     * Provides @Document for display
     */
    override suspend fun getKycDocument(): Document {
        return kycInfoRepository.getKycDocument()
    }

    override suspend fun updateKycLocation(resultLocation: Location) {
        kycInfoRepository.updateKycLocation(resultLocation)
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun validateKycInfo(kycInfo: KycInfo): Boolean {
        val documentValidationError = kycInfo.document?.validate()
        val personValidationError = kycInfo.person.validate()
        val providerValidationError = kycInfo.provider.validate()
        val metadataValidationError = kycInfo.metadata.validate()
        val selfieValidationError = kycInfo.selfie?.validate()
        val secondaryValidationError = kycInfo.secondaryDocuments
            .map { it.validate() }
            .flatten()
        val contactsValidationError = kycInfo.contacts.validate()
        val addressValidationError = kycInfo.address?.validate()

        Timber.d("validateFycInfo" +
                "\n documentValidationError : $documentValidationError" +
                "\n personValidationError  : $personValidationError " +
                "\n providerValidationError : $providerValidationError" +
                "\n metadataValidationError : $metadataValidationError" +
                "\n selfieValidationError : $selfieValidationError" +
                "\n secondaryValidationError : $secondaryValidationError" +
                "\n contactsValidationError : $contactsValidationError" +
                "\n addressValidationError : $addressValidationError"
        )

        IdLogger.info("validateFycInfo" +
            "\n documentValidationError : $documentValidationError" +
                    "\n personValidationError  : $personValidationError " +
                    "\n providerValidationError : $providerValidationError" +
                    "\n metadataValidationError : $metadataValidationError" +
                    "\n selfieValidationError : $selfieValidationError" +
                    "\n secondaryValidationError : $secondaryValidationError" +
                    "\n contactsValidationError : $contactsValidationError" +
                    "\n addressValidationError : $addressValidationError")

        return documentValidationError.isNullOrEmpty()
                && personValidationError.isEmpty()
                && providerValidationError.isEmpty()
                && metadataValidationError.isEmpty()
                && selfieValidationError.isNullOrEmpty()
                && secondaryValidationError.isEmpty()
                && contactsValidationError.isEmpty()
                && addressValidationError.isNullOrEmpty()
    }

    override suspend fun createKycZip(applicationContext: Context): ZipCreationStateDto {
        val kycInfo = kycInfoRepository.finalizeAndGetKycInfo()
        Timber.d("getKycUriZip : $kycInfo")
        IdLogger.info("Zipping of Kyc info started")
        var zipCreationStateDto: ZipCreationStateDto = ZipCreationStateDto.ERROR
        if (validateKycInfo(kycInfo)) {
            try {
                Zipper().createZipFile(kycInfo, applicationContext).let {
                    zipCreationStateDto = ZipCreationStateDto.SUCCESS(it)
                    IdLogger.info("Zip creation successful")
                }
            } catch (zipperError: ZipperError) {
                IdLogger.error("Zipper error : ${zipperError::class.java.name}")
                when (zipperError) {
                    ZipperError.KycNotValid -> Timber.d("Error in kyc object")
                    ZipperError.CannotCreateZip -> Timber.d("Error creating zip file")
                    ZipperError.NotEnoughSpace -> Timber.d("There are not enough space in device")
                    ZipperError.ZipExceedMaximumSize -> Timber.d("Zip file exceed 100MB")
                }
            }
        } else {
            IdLogger.error("Zip creation failed")
        }
        Timber.d("uri: $zipCreationStateDto")
        return zipCreationStateDto
    }

}
