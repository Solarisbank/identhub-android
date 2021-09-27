package de.solarisbank.identhub.session.data.preferences

import android.content.SharedPreferences
import androidx.annotation.RestrictTo
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP

@RestrictTo(RestrictTo.Scope.LIBRARY)
class IdentificationStepPreferences(private val sharedPreferences: SharedPreferences) {
    fun clear() {
        with(sharedPreferences.edit()) {
            remove(CURRENT_IDENTIFICATION_STEP)
            commit()
        }
    }

    fun get(): COMPLETED_STEP? {
        val step = sharedPreferences.getInt(CURRENT_IDENTIFICATION_STEP, -1)
        return COMPLETED_STEP.getEnum(step)
    }

    fun save(step: COMPLETED_STEP) {
        with(sharedPreferences.edit()) {
            putInt(CURRENT_IDENTIFICATION_STEP, step.index)
            commit()
        }
    }

    companion object {
        private const val CURRENT_IDENTIFICATION_STEP = "CURRENT_IDENTIFICATION_STEP"

    }
}