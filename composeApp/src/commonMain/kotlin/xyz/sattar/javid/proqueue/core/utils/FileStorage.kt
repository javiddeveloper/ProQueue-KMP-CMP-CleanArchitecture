package xyz.sattar.javid.proqueue.core.utils

interface FileStorage {
    suspend fun saveImage(bytes: ByteArray): String
}
