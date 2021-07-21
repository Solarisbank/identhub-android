package de.solarisbank.identhub.domain.session

import android.content.SharedPreferences
import de.solarisbank.identhub.domain.data.dto.InitializationDto
import de.solarisbank.identhub.domain.data.dto.PartnerSettingsDto
import de.solarisbank.identhub.router.FIRST_STEP_KEY
import timber.log.Timber

class IdentityInitializationSharedPrefsDataSource(private val sharedPreferences: SharedPreferences) {

    fun saveInitializationDto(initializationDto: InitializationDto) {
        with(sharedPreferences.edit()) {
            putString(FIRST_STEP_KEY, initializationDto.firstStep)
            putString(FALLBACK_STEP, initializationDto.fallbackStep)
            putInt(ALLOWED_ATTEMPTS_AMOUNT, initializationDto.allowedRetries)
            putString(FOURTHLINE_PROVIDER, initializationDto.fourthlineProvider)
            initializationDto.partnerSettings?.defaultToFallbackStep?.let {
                putBoolean(PARTNER_SETTING_DEFAULT_TO_FALLBACK_STEP, it)
            }
            commit()
        }
        Timber.d("saveInitializationDto() data: $initializationDto")
    }

    fun getInitializationDto(): InitializationDto? {
        return try {
            InitializationDto(
                firstStep = sharedPreferences.getString(FIRST_STEP_KEY, null)!!,
                fallbackStep = sharedPreferences.getString(FALLBACK_STEP, null),
                allowedRetries = sharedPreferences.getInt(ALLOWED_ATTEMPTS_AMOUNT, -1),
                fourthlineProvider = sharedPreferences.getString(FOURTHLINE_PROVIDER, null),
                partnerSettings = PartnerSettingsDto(
                    defaultToFallbackStep = sharedPreferences.getBoolean(
                        PARTNER_SETTING_DEFAULT_TO_FALLBACK_STEP, false
                    )
                )
            )
        } catch (npe: NullPointerException) {
            Timber.d(npe)
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
        private const val FOURTHLINE_PROVIDER = "fourthline_provider"
        private const val PARTNER_SETTING_DEFAULT_TO_FALLBACK_STEP = "partner_setting_default_to_fallback_step"
    }
}