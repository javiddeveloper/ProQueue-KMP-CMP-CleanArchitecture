package xyz.sattar.javid.proqueue.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FileStorageIos : FileStorage {
    override suspend fun saveImage(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        // No-op for iOS
        ""
    }
}
