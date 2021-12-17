package de.solarisbank.identhub.file

import android.content.Context
import de.solarisbank.identhub.file.OkioKotlinAdapter.fillFileIn
import okio.BufferedSource
import java.io.File
import java.io.IOException

class FileController(context: Context) {
    private val pdfDirectoryPath: String = context.filesDir.toString() + "/pdf"

    @Throws(IOException::class)
    fun save(fileName: String, bufferedSource: BufferedSource): File? {
        val pdfDirectory = File(pdfDirectoryPath)
        pdfDirectory.mkdirs()
        val destinationFile = File(pdfDirectoryPath, fileName)
        return fillFileIn(bufferedSource, destinationFile)
    }

    fun retrieve(fileName: String): File? {
        val destinationFile = File(pdfDirectoryPath, fileName)
        return if (destinationFile.exists()) {
            destinationFile
        } else null
    }

}