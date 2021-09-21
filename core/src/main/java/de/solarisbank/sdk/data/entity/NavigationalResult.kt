package de.solarisbank.sdk.data.entity

data class NavigationalResult<T>(
        val data: T,
        val nextStep: String? = null)
