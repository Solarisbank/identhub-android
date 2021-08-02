package de.solarisbank.sdk.fourthline.data.kyc.storage

import android.graphics.Bitmap
import android.location.Location
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.core.Gender
import com.fourthline.core.mrz.MrtdMrzInfo
import com.fourthline.kyc.*
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.document.DocumentScannerStepResult
import com.fourthline.vision.selfie.SelfieScannerResult
import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.parseDateFromMrtd
import de.solarisbank.sdk.fourthline.parseDateFromString
import timber.log.Timber
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.LinkedHashMap

class KycInfoInMemoryDataSource {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private val kycInfo = KycInfo().also {
        it.person = Person()
        it.metadata = DeviceMetadata()
    }
    private val docPagesMap = LinkedHashMap<DocPageKey, Attachment.Document>()
    private var _personDataDto: PersonDataDto? = null

    fun getKycInfo(): KycInfo {
        lock.readLock().lock()
        try {
            return kycInfo
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Provides @Document for display
     */
    fun getKycDocument(): Document {
        lock.readLock().lock()
        try {
            return kycInfo.document!!
        } finally {
            lock.readLock().unlock()
        }
    }

    fun getSelfieFullImage(): Bitmap? {
        lock.readLock().lock()
        try {
            return kycInfo.selfie?.image
        } finally {
            lock.readLock().unlock()
        }
    }

    fun getPersonData(): PersonDataDto? {
        lock.readLock().lock()
        try {
            return _personDataDto
        } finally {
            lock.readLock().unlock()
        }
    }

    fun updateWithPersonDataDto(personDataDto: PersonDataDto, providerName: String) {
        lock.writeLock().lock()
        Timber.d("updateWithPersonDataDto : ${personDataDto}")
        try {
            _personDataDto = personDataDto
            kycInfo.provider = Provider(
                    name = providerName,
                    clientNumber = personDataDto.personUid
            )

            kycInfo.address = Address().also {
                personDataDto.address?.apply {
                    it.street = street
                    it.streetNumber = streetNumber
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
                it.gender = when (_personDataDto?.gender?.toLowerCase()) {
                    Gender.MALE.name.toLowerCase() -> Gender.MALE
                    Gender.FEMALE.name.toLowerCase() -> Gender.FEMALE
                    else -> Gender.UNKNOWN
                }
                it.birthDate = _personDataDto?.birthDate?.parseDateFromString()
                it.nationalityCode = personDataDto.nationality
                it.birthPlace = personDataDto.birthPlace
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateKycWithSelfieScannerResult(result: SelfieScannerResult) {
        lock.writeLock().lock()
        try {
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
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateIpAddress(ipAddress: String) {
        kycInfo.metadata!!.ipAddress = ipAddress
    }

    /**
     * Retains document pages' photos and stores them to map
     * Called from DocScanFragment.onStepSuccess()
     */
    fun updateKycInfoWithDocumentScannerStepResult(docType: DocumentType, result: DocumentScannerStepResult) {
        lock.writeLock().lock()
        try {
            Timber.d("updateKycInfoWithDocumentScannerStepResult : " +
                    "\ntimestamp:${result.metadata.timestamp}" +
                    "\nlocation?.first: ${result.metadata.location?.first}" +
                    "\nlocation?.second: ${result.metadata.location?.second}"
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
            lock.writeLock().unlock()
        }
    }


    /**
     * Retains recognized String data of the documents
     */
    fun updateKycInfoWithDocumentScannerResult(docType: DocumentType, result: DocumentScannerResult) {
        lock.writeLock().lock()
        try {
            obtainDocument(docType, result)
            updateKycPerson(result)
        } finally {
            lock.writeLock().unlock()
        }
    }

    private fun obtainDocument(docType: DocumentType, result: DocumentScannerResult) {
        lock.writeLock().lock()
        try {
            val mrtd = (result.mrzInfo as? MrtdMrzInfo)
            kycInfo.document = Document(
                    images = docPagesMap.entries.filter { it.key.docType == docType }.map { it.value }.toList(),
                    videoUrl = result.videoUrl,
                    number = mrtd?.documentNumber,
                    expirationDate = mrtd?.expirationDate?.parseDateFromMrtd(),
                    type = docType
            )
        } finally {
            lock.writeLock().unlock()
        }
    }

    /**
     * A part of kycInfo.person is obtained in updateWithPersonDataDto.
     * Here provides person data that has been recognized from document scanning
     */
    private fun updateKycPerson(result: DocumentScannerResult) {
        lock.writeLock().lock()
        try {
            val mrtd = result.mrzInfo as? MrtdMrzInfo

            kycInfo.person.apply {
                val recognizedFirstNames = mrtd?.firstNames?.joinToString(separator = " ")
                val recognizedLastNames = mrtd?.lastNames?.joinToString(separator = " ")
                val recognizedBirthDate = mrtd?.birthDate?.parseDateFromMrtd()
                Timber.d("updateKycPerson : " +
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
                if ((gender == null || gender == Gender.UNKNOWN) && mrtd?.gender != null && mrtd.gender != Gender.UNKNOWN) {
                    gender = mrtd.gender
                }
                if (birthDate == null && recognizedBirthDate != null) {
                    birthDate = recognizedBirthDate
                }
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateKycLocation(resultLocation: Location) {
        lock.writeLock().lock()
        try {
            kycInfo.metadata!!.location = Pair(resultLocation.latitude, resultLocation.longitude)
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateIssueDate(issueDate: Date) {
        lock.writeLock().lock()
        try {
            kycInfo.document!!.issueDate = issueDate
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateExpireDate(expireDate: Date) {
        lock.writeLock().lock()
        try {
            kycInfo.document!!.expirationDate = expireDate
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateDocumentNumber(number: String) {
        lock.writeLock().lock()
        try {
            kycInfo.document!!.number = number
        } finally {
            lock.writeLock().unlock()
        }
    }

    private data class DocPageKey(val docType: DocumentType, val docSide: DocumentFileSide, val isAngled: Boolean)

}