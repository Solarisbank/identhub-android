package de.solarisbank.identhub.data.iban

import android.content.SharedPreferences
import de.solarisbank.identhub.domain.data.dto.InitializationDto
import de.solarisbank.identhub.router.FIRST_STEP_KEY

class IdentityInitializationSharedPrefsDataSource(private val sharedPreferences: SharedPreferences) {

    fun saveInitializationDto(initializationDto: InitializationDto) {
        with(sharedPreferences.edit()) {
            putString(FIRST_STEP_KEY, initializationDto.firstStep)
            putString(FALLBACK_STEP, initializationDto.fallbackStep)
            putInt(ALLOWED_ATTEMPTS_AMOUNT, initializationDto.allowedRetries)
            commit()
        }
    }

    fun getInitializationDto(): InitializationDto? {
        return try {
            InitializationDto(
                    firstStep = sharedPreferences.getString(FIRST_STEP_KEY, null)!!,
                    fallbackStep = sharedPreferences.getString(FALLBACK_STEP, null)!!,
                    allowedRetries = sharedPreferences.getInt(ALLOWED_ATTEMPTS_AMOUNT, -1)
                    )
        } catch (npe: NullPointerException) {
            null
        }

    }

    fun deleteInitializationDto() {
        with(sharedPreferences.edit()) {
            remove(ALLOWED_ATTEMPTS_AMOUNT)
            commit()
        }
    }

    companion object {
        private const val ALLOWED_ATTEMPTS_AMOUNT = "ALLOWED_ATTEMPTS_AMOUNT"
        private const val FALLBACK_STEP = "fallback_step"
    }
}