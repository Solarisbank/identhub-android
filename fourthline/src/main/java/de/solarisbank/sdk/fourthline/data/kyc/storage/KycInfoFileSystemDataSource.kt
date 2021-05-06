package de.solarisbank.sdk.fourthline.data.kyc.storage

import android.content.Context
import com.fourthline.kyc.KycInfo
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class KycInfoFileSystemDataSource(context: Context) {

    private val kycInfo = KycInfo()
    private val file = File(context.filesDir, FILE_NAME)

    fun serialize() {
//        val fos: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
        val fos: FileOutputStream = FileOutputStream("")
        val os = ObjectOutputStream(fos)
        os.writeObject(kycInfo)
        os.close()
        fos.close()
    }

    companion object {
        const val FILE_NAME = "KYC"
    }

}