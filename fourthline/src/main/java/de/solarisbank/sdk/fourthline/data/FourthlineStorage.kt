package de.solarisbank.sdk.fourthline.data

import de.solarisbank.sdk.feature.storage.PersistentStorage
import de.solarisbank.sdk.feature.storage.get
import de.solarisbank.sdk.feature.storage.put
import de.solarisbank.sdk.fourthline.data.dto.TermsAndConditions

private const val IdCardSelectedKey = "isIdCardSelected"
private const val NamirialTermsKey = "namirialTermsKey"
private const val RawDocumentListKey = "rawCountryList"

class FourthlineStorage(private val storage: PersistentStorage) {
    var isIdCardSelected: Boolean
    get() = storage.getBoolean(IdCardSelectedKey, false)
    set(value) = storage.putBoolean(IdCardSelectedKey, value)

    var namirialTerms: TermsAndConditions?
    get() = storage.get(NamirialTermsKey)
    set(value) = storage.put(NamirialTermsKey, value)

    var rawDocumentList: String?
    get() = storage.getString(RawDocumentListKey)
    set(value) = storage.putString(RawDocumentListKey, value)
}