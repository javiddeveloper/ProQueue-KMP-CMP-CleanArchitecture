package xyz.sattar.javid.proqueue.core.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class FileStorageAndroid(private val context: Context) : FileStorage {
    override suspend fun saveImage(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val directory = File(context.filesDir, "images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        file.writeBytes(bytes)
        file.absolutePath
    }
}
