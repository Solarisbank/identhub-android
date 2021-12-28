package de.solarisbank.sdk.data.customization

import de.solarisbank.sdk.core_ui.data.dto.Customization

interface CustomizationRepository {
    fun get(): Customization
}