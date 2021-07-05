package de.solarisbank.sdk.core.network.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.solarisbank.sdk.core.data.dto.ErrorResponseDto
import retrofit2.HttpException
import timber.log.Timber

fun HttpException.parseErrorResponseDto(): ErrorResponseDto? {
    val jsonString = this.response().errorBody()?.string()
    Timber.d("parseErrorResponseDto: $jsonString")
    return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter(ErrorResponseDto::class.java)
            .fromJson(jsonString)
}