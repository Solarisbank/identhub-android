package de.solarisbank.sdk.logger.data

import de.solarisbank.sdk.logger.domain.model.LogContent
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface LoggerAPI {

    @POST("/sdk_logging")
    fun postLog(@Body logContent: LogContent): Single<Boolean>
}