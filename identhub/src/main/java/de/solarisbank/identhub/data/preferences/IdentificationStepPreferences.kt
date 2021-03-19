package de.solarisbank.identhub.data.preferences

import android.content.SharedPreferences
import androidx.annotation.RestrictTo
import de.solarisbank.identhub.session.IdentHubSession

@RestrictTo(RestrictTo.Scope.LIBRARY)
class IdentificationStepPreferences(private val sharedPreferences: SharedPreferences) {
    fun clear() {
        with(sharedPreferences.edit()) {
            remove(CURRENT_IDENTIFICATION_STEP)
            commit()
        }
    }

    fun get(): IdentHubSession.Step? {
        val step = sharedPreferences.getInt(CURRENT_IDENTIFICATION_STEP, -1)
        return IdentHubSession.Step.getEnum(step)
    }

    fun save(step: IdentHubSession.Step) {
        with(sharedPreferences.edit()) {
            putInt(CURRENT_IDENTIFICATION_STEP, step.index)
            commit()
        }
    }

    companion object {
        private const val CURRENT_IDENTIFICATION_STEP = "CURRENT_IDENTIFICATION_STEP"

    }
}