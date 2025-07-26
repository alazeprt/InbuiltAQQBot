package top.alazeprt.iab.util

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object CompressUtil {
    fun decompress(file: File, folder: File): File? {
        require(folder.isDirectory || folder.mkdirs()) { "Failed to create directory: ${folder.absolutePath}" }

        when (file.extension.lowercase()) {
            "zip" -> return unzip(file, folder)
            "gz" -> return untarGz(file, folder)
            else -> throw IllegalArgumentException("Unsupported file extension: ${file.name}")
        }
    }

    private fun unzip(zipFile: File, outputDir: File): File? {
        FileInputStream(zipFile).use { fis ->
            ZipArchiveInputStream(fis).use { zipIn ->
                return extractArchive(zipIn, outputDir)
            }
        }
    }

    private fun untarGz(gzFile: File, outputDir: File): File? {
        FileInputStream(gzFile).use { fis ->
            GzipCompressorInputStream(fis).use { gzIn ->
                TarArchiveInputStream(gzIn).use { tarIn ->
                    return extractArchive(tarIn, outputDir)
                }
            }
        }
    }

    private fun extractArchive(archiveIn: ArchiveInputStream<*>, outputDir: File): File? {
        var entry = archiveIn.nextEntry
        var file: File? = null
        while (entry != null) {
            val outputFile = outputDir.resolve(entry.name).normalize()
            require(outputFile.toPath().startsWith(outputDir.toPath())) {
                "Invalid path: ${entry.name}"
            }

            if (entry.isDirectory) {
                outputFile.mkdirs()
            } else {
                outputFile.parentFile?.mkdirs()
                FileOutputStream(outputFile).use { fos ->
                    archiveIn.copyTo(fos)
                }
            }
            if (outputFile.isFile) {
                file = outputFile
            }
            entry = archiveIn.nextEntry
        }
        return file
    }
}