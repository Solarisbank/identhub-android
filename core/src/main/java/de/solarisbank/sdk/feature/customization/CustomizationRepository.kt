package de.solarisbank.sdk.feature.customization

import io.reactivex.Single

interface CustomizationRepository {
    fun get(): Customization
    fun initialize(): Single<Customization>
}