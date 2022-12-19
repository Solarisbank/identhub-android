package de.solarisbank.sdk.fourthline.data

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.getPartFile(): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
            "document",
            this.name,
            this.asRequestBody(null)
    )
}

