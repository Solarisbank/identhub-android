package de.solarisbank.identhub.data.entity

data class NavigationalResult<T>(
        val data: T,
        val nextStep: String? = null)
