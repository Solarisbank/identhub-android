package de.solarisbank.sdk.data

import android.os.Bundle
import androidx.core.os.bundleOf

sealed class IdenthubResult {
    data class Confirmed(val identificationId: String): IdenthubResult()
    data class Failed(val message: String?): IdenthubResult()

    fun toBundle(): Bundle {
        val bundle = bundleOf(KEY_STATUS to this::class.java.name)
        when(this) {
            is Confirmed -> bundle.putString(KEY_IDENTIFICATION_ID, identificationId)
            is Failed -> bundle.putString(KEY_MESSAGE, message)
        }
        return bundle
    }

    companion object {
        const val KEY_STATUS = "key_status"
        const val KEY_IDENTIFICATION_ID = "key_identificationId"
        const val KEY_MESSAGE = "key_message"

        fun fromBundle(bundle: Bundle): IdenthubResult? {
            when (bundle.getString(KEY_STATUS)) {
                Confirmed::class.java.name -> {
                    bundle.getString(KEY_IDENTIFICATION_ID)?.let {
                        return Confirmed(it)
                    }
                }
                Failed::class.java.name -> {
                    return Failed(bundle.getString(KEY_MESSAGE))
                }
                else -> return null
            }
            return null
        }
    }
}