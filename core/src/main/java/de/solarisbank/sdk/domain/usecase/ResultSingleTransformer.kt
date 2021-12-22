package de.solarisbank.sdk.domain.usecase

import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.Type
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

class ResultSingleTransformer<U> : SingleTransformer<NavigationalResult<U>, Result<U>> {
    override fun apply(upstream: Single<NavigationalResult<U>>): SingleSource<Result<U>> {
        return upstream.map<Result<U>> { Result.Success(it.data, it.nextStep) }
                .onErrorReturn {
                    val errorType = getErrorType(it)
                    Timber.d("apply, it: $it")
                    return@onErrorReturn Result.Error(errorType, it)
                }
    }
}

fun <T> Single<NavigationalResult<T>>.transformResult(): Single<Result<T>> {
    return compose(ResultSingleTransformer())
}

fun getErrorType(throwable: Throwable): Type {
    var errorType: Type = Type.Unknown
    if (throwable is HttpException) {
        errorType = when (throwable.code()) {
            HttpURLConnection.HTTP_BAD_REQUEST -> Type.BadRequest
            HttpURLConnection.HTTP_NOT_FOUND -> Type.ResourceNotFound
            HttpURLConnection.HTTP_UNAUTHORIZED -> Type.Unauthorized
            HttpURLConnection.HTTP_PRECON_FAILED -> Type.PreconditionFailed
            HttpURLConnection.HTTP_CONFLICT -> Type.Conflict
            UNPROCESSABLE_ENTITY -> Type.UnprocessableEntity

            //server errors
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            HttpURLConnection.HTTP_NOT_IMPLEMENTED,
            HttpURLConnection.HTTP_BAD_GATEWAY,
            HttpURLConnection.HTTP_UNAVAILABLE,
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT,
            HttpURLConnection.HTTP_VERSION,
            506, 507, 508, 509
            -> Type.ServerError

            else -> Type.Unknown
        }
    }
    return errorType
}

const val UNPROCESSABLE_ENTITY = 422


