package top.alazeprt.iab.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object VerifyUtil {
    enum class HashAlgorithm(val algorithmName: String) {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512")
    }

    fun calculateFileHash(file: File, algorithm: HashAlgorithm = HashAlgorithm.SHA256): String {
        val digest = MessageDigest.getInstance(algorithm.algorithmName)
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}