package de.solarisbank.sdk.data.customization

import de.solarisbank.sdk.data.dto.Customization

interface CustomizationRepository {
    fun get(): Customization
}