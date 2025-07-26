package top.alazeprt.iab.backend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import top.alazeprt.iab.InbuiltAQQBot
import top.alazeprt.iab.util.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.function.Consumer

class BackendLoader(val plugin: InbuiltAQQBot) {

    var outputWriter: BufferedWriter? = null
    var process: Process? = null

    fun download(consumer: Consumer<File> = Consumer { run {} }) {
        plugin.taskList.add(plugin.submitAsync {
            plugin.log(LogLevel.INFO, "Getting latest release of Lagrange.OneBot ...")
            val releases = RemoteUtil.getReleases("LagrangeDev", "Lagrange.Core")
            val list = mutableListOf<SystemInfo>()
            if (!File(plugin.dataDir, "Lagrange").listFiles().isNullOrEmpty()) {
                val executableFile = File(plugin.dataDir, "Lagrange").listFiles()!!.first { it.name.contains("Lagrange") }
                plugin.log(LogLevel.INFO, "Found Lagrange.OneBot executable file in data directory: ${executableFile.absolutePath}")
                val sha256 = VerifyUtil.calculateFileHash(executableFile)
                if (plugin.config.get("file_checksum") == null) {
                    plugin.config.addProperty("file_checksum", "")
                }
                if (sha256 == plugin.config.get("file_checksum").asString) {
                    plugin.log(LogLevel.INFO, "The Lagrange.OneBot executable file was verified successfully, skipping download ...")
                    consumer.accept(executableFile)
                    return@submitAsync
                } else {
                    plugin.log(LogLevel.WARN, "The Lagrange.OneBot executable file wasn't verified successfully, downloading a new one ...")
                }
            }
            releases.first().asJsonObject.getAsJsonArray("assets").forEach {
                val assetName = it.asJsonObject.get("name").asString
                val assetSystem = if (assetName.contains("win") && assetName.contains("x64")) {
                    SystemInfo(System.WINDOWS, Arch.x86_64)
                } else if (assetName.contains("win") && assetName.contains("x86")) {
                    SystemInfo(System.WINDOWS, Arch.x86)
                } else if (assetName.contains("osx") && assetName.contains("x64")) {
                    SystemInfo(System.MAC, Arch.x86_64)
                } else if (assetName.contains("osx") && assetName.contains("arm64")) {
                    SystemInfo(System.MAC, Arch.arm64)
                } else if (assetName.contains("linux") && assetName.contains("x64")) {
                    SystemInfo(System.LINUX, Arch.x86_64)
                } else if (assetName.contains("linux") && assetName.contains("arm64")) {
                    SystemInfo(System.LINUX, Arch.arm64)
                } else if (assetName.contains("linux") && assetName.contains("arm")) {
                    SystemInfo(System.LINUX, Arch.arm32)
                } else {
                    SystemInfo(null, null)
                }
                list.add(assetSystem)
                var file: File? = null;
                if (assetSystem.arch == plugin.system.arch && assetSystem.os == plugin.system.os) {
                    plugin.log(LogLevel.INFO, "Downloading Lagrange.OneBot ...")
                    val url = it.asJsonObject.get("browser_download_url").asString
                    plugin.log(LogLevel.INFO, "Trying to use the faster download url: ghproxy.net")
                    try {
                        file = RemoteUtil.downloadToFile(url.replace("github.com", "ghproxy.net/github.com"), plugin.dataDir)
                    } catch (e: Exception) {
                        plugin.log(
                            LogLevel.WARN, "Failed to use the faster download url: ghproxy.net, " +
                                "trying another faster download url: edgeone.gh-proxy.com")
                        try {
                            file = RemoteUtil.downloadToFile(url.replace("github.com", "edgeone.gh-proxy.com/github.com"),
                                plugin.dataDir)
                        } catch (e: Exception) {
                            plugin.log(
                                LogLevel.WARN, "Failed to use the faster download url: edgeone.gh-proxy.com, " +
                                    "trying another faster download url: gh-proxy.com")
                            try {
                                file = RemoteUtil.downloadToFile(url.replace("github.com", "gh-proxy.com/github.com"),
                                    plugin.dataDir)
                            } catch (e: Exception) {
                                plugin.log(
                                    LogLevel.ERROR, "Failed to use the faster download url: gh-proxy.com, " +
                                        "trying the original download url")
                                try {
                                    RemoteUtil.downloadToFile(url, plugin.dataDir)
                                } catch (e: Exception) {
                                    plugin.log(
                                        LogLevel.ERROR, "Failed to download Lagrange.OneBot, " +
                                            "please check your network connection or try again later")
                                    return@submitAsync
                                }
                            }
                        }
                    }
                    plugin.log(LogLevel.INFO, "Decompressing Lagrange.OneBot ...")
                    val executableFile = CompressUtil.decompress(file!!, plugin.dataDir)
                    plugin.log(LogLevel.INFO, "Moving executable Lagrange.OneBot file to new directory ...")
                    if (executableFile == null) {
                        plugin.log(LogLevel.ERROR, "Failed to find Lagrange.OneBot executable file!")
                        return@submitAsync
                    }
                    val newDir = File(plugin.dataDir, "Lagrange")
                    newDir.mkdirs()
                    Files.move(executableFile.toPath(), newDir.toPath().resolve(executableFile.name), StandardCopyOption.REPLACE_EXISTING)
                    plugin.log(LogLevel.INFO, "Recording the SHA256 checksum of the file ...")
                    val sha256 = VerifyUtil.calculateFileHash(newDir.toPath().resolve(executableFile.name).toFile())
                    plugin.config.addProperty("file_checksum", sha256)
                    plugin.log(LogLevel.INFO, "Deleting the temp files ...")
                    plugin.dataDir.listFiles()!!.forEach { f ->
                        if (f.name != "Lagrange" && f.name != "config.json") {
                            if (f.isFile) {
                                f.delete()
                            } else {
                                f.deleteRecursively()
                            }
                        }
                    }
                    plugin.log(LogLevel.INFO, "Lagrange.OneBot has been downloaded successfully")
                    consumer.accept(executableFile)
                    return@submitAsync
                }
            }
            plugin.log(
                LogLevel.ERROR, "No matching release found for Lagrange.OneBot, the supported system os and arch are: " +
                    list.map { "${it.os}-${it.arch}" }.joinToString(", ")
            )
        })
    }

    fun launch() {
        plugin.submitAsync {
            plugin.log(LogLevel.INFO, "Launching Lagrange.OneBot ...")
            val command = if (java.lang.System.getProperty("os.name").startsWith("Windows")) {
                arrayOf("cmd.exe", "/c", "start", "Lagrange.OneBot.exe")
            } else {
                arrayOf("/bin/sh", "-c", "export DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1 && chmod +x Lagrange.OneBot && ./Lagrange.OneBot")
            }

            try {
                process = ProcessBuilder(*command)
                    .directory(File(plugin.dataDir, "Lagrange"))
                    .redirectErrorStream(true)
                    .start()

                BufferedReader(InputStreamReader(process!!.inputStream, if (plugin.system.os == System.WINDOWS) "GBK" else "UTF-8")).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        println(line)
                        if (line?.contains("Edit the appsettings.json") == true) {
                            plugin.log(LogLevel.INFO, "You need to edit the settings of Lagrange.OneBot in the file: plugins/InbuiltAQQBot/Lagrange/appsettings.json")
                            plugin.log(LogLevel.INFO, "Please refer to the documentation for more information: https://lagrangedev.github.io/Lagrange.Doc/v1/Lagrange.OneBot/Config/#%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6")
                            plugin.log(LogLevel.INFO, "When you have finished editing, please restart the server")
                            process!!.destroy()
                            return@submitAsync
                        }
                        plugin.log(LogLevel.INFO, "[Lagrange.OneBot] $line")
                    }
                }

                this.outputWriter = process!!.outputStream.bufferedWriter(StandardCharsets.UTF_8)

                process!!.waitFor()
                plugin.log(LogLevel.INFO, "Lagrange.OneBot has been closed")
            } catch (e: InterruptedException) {
                plugin.log(LogLevel.INFO, "Lagrange.OneBot has been interrupted")
            } catch (e: Exception) {
                if (e.message == "Stream closed") return@submitAsync
                plugin.log(LogLevel.WARN, "Failed to run Lagrange.OneBot, ${e.message}")
            }
        }
    }
}