package de.solarisbank.identhub.data.network

import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.sdk.core.result.Result
import de.solarisbank.sdk.core.result.Type
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
                    var errorType: Type = Type.Unknown
                    if (it is HttpException) {
                        errorType = when (it.code()) {
                            HttpURLConnection.HTTP_BAD_REQUEST -> Type.BadRequest
                            HttpURLConnection.HTTP_NOT_FOUND -> Type.ResourceNotFound
                            HttpURLConnection.HTTP_INTERNAL_ERROR -> Type.ServerError
                            HttpURLConnection.HTTP_UNAVAILABLE -> Type.ServerError
                            HttpURLConnection.HTTP_UNAUTHORIZED -> Type.Unauthorized
                            HttpURLConnection.HTTP_PRECON_FAILED -> Type.PreconditionFailed
                            else -> Type.Unknown
                        }
                    }
                    Timber.d("apply, it: $it")
                        return@onErrorReturn Result.Error(errorType, it)
                }
    }
}

fun <T> Single<NavigationalResult<T>>.transformResult(): Single<Result<T>> {
    return compose(ResultSingleTransformer())
}


