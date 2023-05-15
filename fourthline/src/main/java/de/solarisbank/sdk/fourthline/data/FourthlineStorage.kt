package de.solarisbank.sdk.fourthline.data

import de.solarisbank.sdk.feature.storage.PersistentStorage

private const val IdCardSelectedKey = "isIdCardSelected"

class FourthlineStorage(private val storage: PersistentStorage) {
    var isIdCardSelected: Boolean
    get() = storage.getBoolean(IdCardSelectedKey, false)
    set(value) = storage.putBoolean(IdCardSelectedKey, value)
}