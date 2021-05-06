package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.sdk.fourthline.data.dto.KycUploadResponseDto
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface KycUploadApi {
//    @Headers("Accept-Encoding: gzip, deflate, br")
    @Multipart
    @POST
    fun uploadKYC(@Url url: String, @Part file: MultipartBody.Part): Single<KycUploadResponseDto>

}