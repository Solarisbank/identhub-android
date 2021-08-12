package de.solarisbank.sdk.fourthline.domain.kyc.delete

import android.content.Context
import timber.log.Timber

class DeleteKycInfoUseCase(private val applicationContext: Context) {

    fun clearPersonDataCaches() {
        try {
            applicationContext
                .cacheDir
                .listFiles()?.let { list ->
                    list.filter { it.isDirectory && it.name.contains("fourthline") }
                        .forEach { file ->
                            file.deleteRecursively()
                        }
                    Timber.d("deleteFourthlineUserDataCaches() 1")
                }
        } catch (e: Exception) {
            Timber.e(e,"deleteFourthlineUserDataCaches() 2")
        }
    }
}