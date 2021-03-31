package de.solarisbank.sdk.core.result

sealed class Type {
    object ResourceNotFound : Type()
    object Unauthorized : Type()
    object BadRequest : Type()
    object ServerError : Type()
    object Unknown : Type()
}