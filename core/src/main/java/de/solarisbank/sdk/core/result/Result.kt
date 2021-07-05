package de.solarisbank.sdk.core.result

sealed class Result<out R> {

    open val nextStep: String? = null

    data class Success<out T>(val data: T, override val nextStep: String? = null) : Result<T>()
    data class Error(val type: Type, val throwable: Throwable, override val nextStep: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[throwable=$throwable]"
            Loading -> "Loading"
        }
    }

    companion object {
        fun createUnknown(throwable: Throwable): Result.Error {
            return Result.Error(Type.Unknown, throwable)
        }

        fun createEmptySuccess(): Result.Success<Any> {
            return Result.Success<Any>(Unit)
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

val <T> Result<T>.throwable: Throwable?
    get() = (this as? Result.Error)?.throwable

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}



