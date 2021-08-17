package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
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
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepository
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import timber.log.Timber
import java.util.*

class KycInfoUseCase(
        private val kycInfoRepository: KycInfoRepository,
        private val identityInitializationRepository: IdentityInitializationRepository
) {

    private var _selfieResultCroppedBitmapLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    var selfieResultCroppedBitmapLiveData = _selfieResultCroppedBitmapLiveData as LiveData<Bitmap>


    fun updateWithPersonDataDto(personDataDto: PersonDataDto) {
        val initializationDto = identityInitializationRepository.getInitializationDto()
        Timber.d("updateWithPersonDataDto : ${personDataDto}, initialization data: $initializationDto")
        kycInfoRepository.updateWithPersonDataDto(personDataDto, initializationDto!!.fourthlineProvider!!)
    }

    fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        Timber.d("updateKycWithSelfieScannerResult : " +
                "\ntimestamp:${result.metadata.timestamp}" +
                "\nlocation?.first: ${result.metadata.location?.first}" +
                "\nlocation?.second: ${result.metadata.location?.second}"
        )
        kycInfoRepository.updateKycWithSelfieScannerResult(result)
        _selfieResultCroppedBitmapLiveData.value = result.image.cropped
    }

    fun getSelfieFullImage(): Bitmap? {
        return kycInfoRepository.getSelfieFullImage()
    }

    /**
     * Retains document pages' photos and stores them to map
     * Called from DocScanFragment.onStepSuccess()
     */
    fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        Timber.d("updateKycInfoWithDocumentScannerStepResult : " +
                "\ntimestamp:${result.metadata.timestamp}" +
                "\nlocation?.first: ${result.metadata.location?.first}" +
                "\nlocation?.second: ${result.metadata.location?.second}"
        )
        kycInfoRepository.updateKycInfoWithDocumentScannerStepResult(docType, result)
    }


    /**
     * Retains recognized String data of the documents
     */
    fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        kycInfoRepository.updateKycInfoWithDocumentScannerResult(docType, result)
    }

    fun updateIssueDate(issueDate: Date) {
        kycInfoRepository.updateIssueDate(issueDate)
    }

    fun updateExpireDate(expireDate: Date) {
        kycInfoRepository.updateExpireDate(expireDate)
    }

    fun updateDocumentNumber(number: String) {
        kycInfoRepository.updateDocumentNumber(number)
    }

    fun updateIpAddress(ipAddress: String) {
        kycInfoRepository.updateIpAddress(ipAddress)
    }

    /**
     * Provides @Document for display
     */
    fun getKycDocument(): Document {
        return kycInfoRepository.getKycDocument()
    }

    fun updateKycLocation(resultLocation: Location) {
        kycInfoRepository.updateKycLocation(resultLocation)
    }

    private fun validateFycInfo(kycInfo: KycInfo): Boolean {
        val documentValidationError = kycInfo.document?.validate()
        val personValidationError = kycInfo.person.validate()
        val providerValidationError = kycInfo.provider.validate()
        val metadataValidationError = kycInfo.metadata?.validate()
        val selfieValidationError = kycInfo.selfie?.validate()
        val secondaryValidationError = kycInfo.secondaryDocument?.validate()
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

        return documentValidationError.isNullOrEmpty()
                && personValidationError.isNullOrEmpty()
                && providerValidationError.isNullOrEmpty()
                && metadataValidationError.isNullOrEmpty()
                && selfieValidationError.isNullOrEmpty()
                && secondaryValidationError.isNullOrEmpty()
                && contactsValidationError.isNullOrEmpty()
                && addressValidationError.isNullOrEmpty()
    }

    fun createKycZip(applicationContext: Context): ZipCreationStateDto {
        val kycInfo = kycInfoRepository.getKycInfo()
        Timber.d("getKycUriZip : $kycInfo")
        var zipCreationStateDto: ZipCreationStateDto = ZipCreationStateDto.ERROR
        if (validateFycInfo(kycInfo)) {
            try {
                zipCreationStateDto = ZipCreationStateDto.SUCCESS(Zipper().createZipFile(kycInfo, applicationContext))
            } catch (zipperError: ZipperError) {
                when (zipperError) {
                    ZipperError.KycNotValid -> Timber.d("Error in kyc object")
                    ZipperError.CannotCreateZip -> Timber.d("Error creating zip file")
                    ZipperError.NotEnoughSpace -> Timber.d("There are not enough space in device")
                    ZipperError.ZipExceedMaximumSize -> Timber.d("Zip file exceed 100MB")
                }
            }
        }
        Timber.d("uri: $zipCreationStateDto")
        return zipCreationStateDto
    }

}
