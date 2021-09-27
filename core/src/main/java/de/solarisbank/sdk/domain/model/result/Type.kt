package de.solarisbank.sdk.domain.model.result

sealed class Type {
    object PreconditionFailed : Type()
    object ResourceNotFound : Type()
    object Unauthorized : Type()
    object BadRequest : Type()
    object ServerError : Type()
    object UnprocessableEntity: Type()
    object Conflict: Type()
    object Unknown : Type()
}