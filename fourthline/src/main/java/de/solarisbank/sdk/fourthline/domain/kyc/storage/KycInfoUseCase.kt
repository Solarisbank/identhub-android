package de.solarisbank.sdk.fourthline.domain.kyc.storage

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.core.Gender
import com.fourthline.core.mrz.MrtdMrzInfo
import com.fourthline.kyc.*
import com.fourthline.kyc.zipper.Zipper
import com.fourthline.kyc.zipper.ZipperError
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.getDateFromMRZ
import timber.log.Timber
import java.net.URI

class KycInfoUseCase {

    private val kycInfo = KycInfo().also { it.person = Person() }
    private val docPagesMap = LinkedHashMap<DocPageKey, Attachment.Document>()
    private var _selfieResultCroppedBitmapLiveData: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    private var _personDataDto: PersonDataDto? = null

    var selfieResultCroppedBitmapLiveData = _selfieResultCroppedBitmapLiveData as LiveData<Bitmap>


    fun updateWithPersonDataDto(personDataDto: PersonDataDto) {
        Timber.d("updateWithPersonDataDto : ${personDataDto}")
        _personDataDto = personDataDto
        kycInfo.provider = Provider(
                name = "SolarisBankSamsung", //todo should be different for different types of staging
                clientNumber = personDataDto.personUid
        )

        kycInfo.address = Address().also {
            personDataDto.address?.apply{
                it.street = street
                it.streetNumber = streetNumber
                it.city = city
                it.countryCode = personDataDto.address?.country //todo change with country
                it.postalCode = postalCode
            }
        }

        kycInfo.contacts = Contacts().also {
            it.mobile = personDataDto.mobileNumber
            it.email = personDataDto.email
        }

        kycInfo.person.also {
            it.nationalityCode = personDataDto.nationality
            it.birthPlace = personDataDto.birthPlace
            fillRecognizablePersonDataFromResponse(personDataDto)
        }
    }

    fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        Timber.d("updateKycWithSelfieScannerResult : " +
                "\ntimestamp:${result.metadata.timestamp}" +
                "\nlocation?.first: ${result.metadata.location?.first}" +
                "\nlocation?.second: ${result.metadata.location?.second}"
        )
        kycInfo.selfie = Attachment.Selfie(
                image = result.image.full,
                timestamp = result.metadata.timestamp.time,
                location = result.metadata.location,
                videoUrl = result.videoUrl
        )
        _selfieResultCroppedBitmapLiveData.value = result.image.cropped
    }

    fun getSelfieFullImage(): Bitmap? {
        return kycInfo.selfie?.image
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

        docPagesMap[DocPageKey(docType, result.metadata.fileSide, result.metadata.isAngled)] =
                Attachment.Document(
                        image = result.image.full,
                        fileSide = result.metadata.fileSide,
                        isAngled = result.metadata.isAngled,
                        timestamp = result.metadata.timestamp.time,
                        location = result.metadata.location
                )
    }


    /**
     * Retains recognized String data of the documents
     */
    fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        obtainDocument(docType, result)
        updateKycPerson(result)
    }

    private fun obtainDocument(docType: DocumentType, result: DocumentScannerResult) {
        val mrtd = (result.mrzInfo as? MrtdMrzInfo)
        kycInfo.document = Document(
                images = docPagesMap.entries.filter { it.key.docType == docType }.map { it.value }.toList(),
                videoUrl = result.videoUrl,
                number = mrtd?.documentNumber,
                expirationDate = mrtd?.expirationDate?.getDateFromMRZ(),
                type = docType
        )
    }

    /**
     * A part of kycInfo.person is obtained in updateWithPersonDataDto.
     * Here provides person data that has been recognized from document scanning
     */
    private fun updateKycPerson(result: DocumentScannerResult) {
        val mrtd = result.mrzInfo as? MrtdMrzInfo
        Timber.d("updateKycPerson : " +
                "\nmrtd.nationality:${mrtd?.nationality}" +
                "\nlmrtd.gender: ${mrtd?.gender}" +
                "\nlmrtd.firstNames: ${mrtd?.firstNames?.joinToString(separator = " ")}" +
                "\nlmrtd.lastNames: ${mrtd?.lastNames?.joinToString(separator = " ")}" +
                "\nlmrtd.birthDate.getDate(): ${mrtd?.birthDate?.getDateFromMRZ()}"
        )

        kycInfo.person.apply {
            val firstNames = mrtd?.firstNames?.joinToString(separator = " ")
            if (!firstNames.isNullOrBlank()) {
                firstName =firstNames
            }
            val lastNames = mrtd?.firstNames?.joinToString(separator = " ")
            if (!lastNames.isNullOrBlank()) {
                lastName = mrtd?.lastNames?.joinToString(separator = " ")
            }
            mrtd?.gender?.let {
                gender = it
            }
            mrtd?.birthDate?.getDateFromMRZ()?.let {
                birthDate = it
            }
        }

    }

    /**
     * Provides @Document for display
     */
    fun getKycDocument(): Document {
        return kycInfo.document!!
    }

    fun updateKycLocation(resultLocation: Location) {
        kycInfo.metadata = DeviceMetadata()
                .apply { location = Pair(resultLocation.latitude, resultLocation.longitude) }
    }

    fun getKycUriZip(applicationContext: Context): URI? {
        Timber.d("getKycUriZip : ${kycInfo}")
        Timber.d("getKycUriZip : ${kycInfo.person.birthDate}, ${kycInfo.person.nationalityCode}, ${kycInfo.person}")
        Timber.d("getKycUriZip : ${kycInfo.document}")
        val errorList = kycInfo.validate()
        if (errorList.isNullOrEmpty()) {
            errorList.forEach { Timber.d("errorList: ${it.name}") }
        }

        var uri: URI? = null
        try {
            uri = Zipper().createZipFile(kycInfo, applicationContext)
        } catch (zipperError: ZipperError) {
            when (zipperError) {
                ZipperError.KycNotValid -> { Timber.d("Error in kyc object") }
                ZipperError.CannotCreateZip -> {
                    Timber.d("Error creating zip file")
                    fillRecognizablePersonDataFromResponse(_personDataDto)
                    try {
                        uri = Zipper().createZipFile(kycInfo, applicationContext)
                    } catch (zipperError: ZipperError) {
                        when (zipperError) {
                            ZipperError.KycNotValid -> Timber.d("2 Error in kyc object")
                            ZipperError.CannotCreateZip -> Timber.d("2 Error creating zip file")
                            ZipperError.NotEnoughSpace -> Timber.d("2 There are not enough space in device")
                            ZipperError.ZipExceedMaximumSize -> Timber.d("2 Zip file exceed 100MB")
                        }
                    }
                }
                ZipperError.NotEnoughSpace -> Timber.d("There are not enough space in device")
                ZipperError.ZipExceedMaximumSize -> Timber.d("Zip file exceed 100MB")
            }
        }
        return uri
    }

    private fun fillRecognizablePersonDataFromResponse(personDataDto: PersonDataDto?) {
        Timber.d("fillRecognizablePersonDataFromResponse, $personDataDto")
        kycInfo.person.apply {
            firstName = _personDataDto?.firstName
            lastName = _personDataDto?.lastName
            gender = when (_personDataDto?.gender?.toLowerCase()) {
                Gender.MALE.name.toLowerCase() -> Gender.MALE
                Gender.FEMALE.name.toLowerCase() -> Gender.FEMALE
                else -> Gender.UNKNOWN
            }
            birthDate = _personDataDto?.birthDate?.getDateFromMRZ()
        }
    }

    private data class DocPageKey(val docType: DocumentType, val docSide: DocumentFileSide, val isAngled: Boolean)
}
