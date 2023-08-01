package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.content.Context
import com.fourthline.kyc.KycInfo
import com.fourthline.kyc.zipper.Zipper
import com.fourthline.kyc.zipper.ZipperError
import de.solarisbank.sdk.logger.IdLogger
import java.net.URI

interface KycInfoZipper {
    fun zip(kycInfo: KycInfo): URI
}

class KycInfoZipperImpl(private val applicationContext: Context): KycInfoZipper {

    @Throws(ZipperError::class)
    override fun zip(kycInfo: KycInfo): URI {
        if (validate(kycInfo)) {
            Zipper().createZipFile(kycInfo, applicationContext).let {
                IdLogger.info("Zip creation successful")
                return it
            }
        } else {
            throw ZipperError.KycNotValid
        }
    }

    private fun validate(kycInfo: KycInfo): Boolean {
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
}