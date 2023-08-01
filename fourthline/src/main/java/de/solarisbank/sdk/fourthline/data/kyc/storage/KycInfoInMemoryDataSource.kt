package de.solarisbank.sdk.fourthline.data.kyc.storage

import android.annotation.SuppressLint
import com.fourthline.core.DeviceMetadata
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.core.Gender
import com.fourthline.core.location.Coordinate
import com.fourthline.core.mrz.MrtdMrzInfo
import com.fourthline.kyc.*
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.parseDateFromString
import de.solarisbank.sdk.fourthline.streetNumber
import de.solarisbank.sdk.fourthline.streetSuffix
import kotlinx.coroutines.sync.Mutex
import timber.log.Timber
import java.util.*

interface KycInfoDataSource {
    suspend fun finalizeAndGetKycInfo(): KycInfo
    suspend fun getKycDocument(): Document
    suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto, providerName: String)
    suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult)
    suspend fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult)
    suspend fun updateKycInfoWithDocumentSecondaryScan(docType: DocumentType, result: DocumentScannerStepResult)
    suspend fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult)
    suspend fun updateIpAddress(ipAddress: String)
    suspend fun updateKycLocation(resultLocation: Location)
    suspend fun updateExpireDate(expireDate: Date)
    suspend fun updateDocumentNumber(number: String)
    suspend fun overrideKycInfo(kycInfo: KycInfo)
}

class KycInfoInMemoryDataSource: KycInfoDataSource {

    private val mutex: Mutex = Mutex()
    private var kycInfo = KycInfo().also {
        it.person = Person()
        it.metadata = DeviceMetadata()
    }
    private val docPagesMap = LinkedHashMap<DocPageKey, Attachment.Document>()
    private val secondaryPagesMap = LinkedHashMap<DocPageKey, Attachment.Document>()
    private var _personDataDto: PersonDataDto? = null

    override suspend fun finalizeAndGetKycInfo(): KycInfo {
        mutex.lock()
        try {
            finalizeSecondaryDocuments()
            return kycInfo
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun overrideKycInfo(kycInfo: KycInfo) {
        mutex.lock()
        try {
            this.kycInfo = kycInfo
        } finally {
            mutex.unlock()
        }
    }

    /**
     * Provides @Document for display
     */
    override suspend fun getKycDocument(): Document {
        mutex.lock()
        try {
            return kycInfo.document!!
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateWithPersonDataDto(personDataDto: PersonDataDto, providerName: String) {
        mutex.lock()
        Timber.d("updateWithPersonDataDto : $personDataDto")
        try {
            _personDataDto = personDataDto
            kycInfo.provider = Provider(
                    name = providerName,
                    clientNumber = personDataDto.personUid
            )

            kycInfo.address = Address().also {
                personDataDto.address?.apply {
                    it.street = street
                    /* Fourthline indicated that this field is mandatory on their API even though
                    It's optional here. They asked us to send 0 if no streetNumber was available */
                    it.streetNumber = streetNumber?.streetNumber() ?: 0
                    it.streetNumberSuffix = streetNumber?.streetSuffix()
                    it.city = city
                    it.countryCode = personDataDto.address?.country
                    it.postalCode = postalCode
                }
            }

            kycInfo.contacts = Contacts().also {
                it.mobile = personDataDto.mobileNumber
                it.email = personDataDto.email
            }

            kycInfo.person.also {
                it.firstName = _personDataDto?.firstName
                it.lastName = _personDataDto?.lastName
                it.gender = when (_personDataDto?.gender?.lowercase(Locale.US)) {
                    Gender.MALE.name.lowercase(Locale.US) -> Gender.MALE
                    Gender.FEMALE.name.lowercase(Locale.US) -> Gender.FEMALE
                    else -> Gender.UNKNOWN
                }
                it.birthDate = _personDataDto?.birthDate?.parseDateFromString()
                it.nationalityCode = personDataDto.nationality
                it.birthPlace = personDataDto.birthPlace
            }

            personDataDto.taxIdentification?.let {
                kycInfo.taxInfo = TaxInfo(
                    taxpayerIdentificationNumber = it.number,
                    taxationCountryCode = it.country
                )
            }
        } finally {
            mutex.unlock()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    override suspend fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        mutex.lock()
        try {
            Timber.d("updateKycWithSelfieScannerResult : " +
                    "\ntimestamp:${result.metadata.timestamp}" +
                    "\nlocation.latitude: ${result.metadata.location?.latitude}" +
                    "\nlocation.longitude: ${result.metadata.location?.longitude}"
            )
            kycInfo.selfie = Attachment.Selfie(
                    image = result.image.full,
                    timestamp = result.metadata.timestamp.time,
                    location = result.metadata.location,
                    videoRecording = result.videoRecording
            )
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateIpAddress(ipAddress: String) {
        mutex.lock()
        try {
            kycInfo.metadata.ipAddress = ipAddress
        } finally {
            mutex.unlock()
        }
    }

    /**
     * Retains document pages' photos and stores them to map
     * Called from DocScanFragment.onStepSuccess()
     */
    @SuppressLint("BinaryOperationInTimber")
    override suspend fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        mutex.lock()
        try {
            Timber.d("updateKycInfoWithDocumentScannerStepResult : " +
                    "\ntimestamp:${result.metadata.timestamp}" +
                    "\nlocation.latitude: ${result.metadata.location?.latitude}" +
                    "\nlocation.longitude: ${result.metadata.location?.longitude}"
            )

            val resultMap =
                    docPagesMap
                            .entries
                            .filter { it.key.docType == docType }
                            .associateByTo(LinkedHashMap(), { it.key}, { it.value})
            docPagesMap.clear()
            docPagesMap.putAll(resultMap)


            docPagesMap[DocPageKey(docType, result.metadata.fileSide, result.metadata.isAngled)] =
                    Attachment.Document(
                            image = result.image.full,
                            fileSide = result.metadata.fileSide,
                            isAngled = result.metadata.isAngled,
                            timestamp = result.metadata.timestamp.time,
                            location = result.metadata.location
                    )
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateKycInfoWithDocumentSecondaryScan(docType: DocumentType, result: DocumentScannerStepResult) {
        mutex.lock()
        try {
            secondaryPagesMap[DocPageKey(docType, result.metadata.fileSide, result.metadata.isAngled)] = Attachment.Document(
                image = result.image.full,
                fileSide = result.metadata.fileSide,
                isAngled = result.metadata.isAngled,
                timestamp = result.metadata.timestamp.time,
                location = result.metadata.location
            )
        } finally {
            mutex.unlock()
        }
    }


    /**
     * Retains recognized String data of the documents
     */
    override suspend fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        mutex.lock()
        try {
            obtainDocument(docType, result)
            updateKycPerson(result)
        } finally {
            mutex.unlock()
        }
    }

    private fun obtainDocument(docType: DocumentType, result: DocumentScannerResult) {
        val mrtd = (result.mrzInfo as? MrtdMrzInfo)
        kycInfo.document = Document(
                images = docPagesMap.entries.filter { it.key.docType == docType }.map { it.value }.toList(),
                videoRecording = result.videoRecording,
                number = mrtd?.documentNumber,
                expirationDate = mrtd?.expirationDate,
                type = docType
        )
    }

    private fun finalizeSecondaryDocuments() {
        if (secondaryPagesMap.isEmpty())
            return

        kycInfo.secondaryDocuments = listOf(
            SecondaryDocument(
                type = secondaryPagesMap.entries.first().key.docType,
                images = secondaryPagesMap.values.toList()
            )
        )
    }

    /**
     * A part of kycInfo.person is obtained in updateWithPersonDataDto.
     * Here provides person data that has been recognized from document scanning
     */
    @SuppressLint("BinaryOperationInTimber")
    private fun updateKycPerson(result: DocumentScannerResult) {
        val mrtd = result.mrzInfo as? MrtdMrzInfo

        kycInfo.person.apply {
            val recognizedFirstNames = mrtd?.firstNames?.joinToString(separator = " ")
            val recognizedLastNames = mrtd?.lastNames?.joinToString(separator = " ")
            val recognizedBirthDate = mrtd?.birthDate
            Timber.d(
                "updateKycPerson : " +
                    "\nrecognizedFirstNames: $recognizedFirstNames}" +
                    "\nrecognizedLastNames: $recognizedLastNames" +
                    "\nlrecognizedBirthDate: $recognizedBirthDate" +
                    "\nlmrtd.gender: ${mrtd?.gender}"
            )

            if (!recognizedFirstNames.isNullOrBlank()) {
                firstName = recognizedFirstNames
            }
            if (!recognizedLastNames.isNullOrBlank()) {
                lastName = recognizedLastNames
            }
            if (
                (gender == null || gender == Gender.UNKNOWN) &&
                mrtd?.gender != null &&
                mrtd.gender != Gender.UNKNOWN
            ) {
                gender = mrtd.gender
            }
            if (birthDate == null && recognizedBirthDate != null) {
                birthDate = recognizedBirthDate
            }
        }
    }

    override suspend fun updateKycLocation(resultLocation: Location) {
        mutex.lock()
        try {
            kycInfo.metadata.location = Coordinate(resultLocation.latitude, resultLocation.longitude)
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateExpireDate(expireDate: Date) {
        mutex.lock()
        try {
            kycInfo.document!!.expirationDate = expireDate
        } finally {
            mutex.unlock()
        }
    }

    override suspend fun updateDocumentNumber(number: String) {
        mutex.lock()
        try {
            kycInfo.document!!.number = number
        } finally {
            mutex.unlock()
        }
    }

    private data class DocPageKey(
        val docType: DocumentType,
        val docSide: DocumentFileSide,
        val isAngled: Boolean
        )
}