package top.alazeprt.iab.util

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object RemoteUtil {
    // 下载URL内容到字符串
    fun download(url: String): String {
        val connection = createConnection(url)
        return connection.inputStream.use { inputStream ->
            validateResponse(connection)
            inputStream.bufferedReader().use { it.readText() }
        }
    }

    // 下载URL内容到指定文件
    fun downloadToFile(url: String, outputFile: File): File {
        var file = outputFile.apply {
            parentFile?.mkdirs() // 确保目录存在
        }

        if (file.isDirectory) {
            file = File(file, url.split("/").last())
        }

        val connection = createConnection(url)
        connection.inputStream.use { inputStream ->
            validateResponse(connection)
            BufferedInputStream(inputStream).use { bis ->
                FileOutputStream(file).use { fos ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (bis.read(buffer).also { bytesRead = it } != -1) {
                        fos.write(buffer, 0, bytesRead)
                    }
                    fos.flush()
                }
            }
        }

        return file
    }

    // 创建并配置HTTP连接
    private fun createConnection(url: String): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            if (url.contains("api.github.com")) setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Mozilla/5.0")
            connectTimeout = 15_000
            readTimeout = 30_000
            instanceFollowRedirects = true // 跟随重定向
        }
        return connection
    }

    // 验证HTTP响应
    private fun validateResponse(connection: HttpURLConnection) {
        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            val errorStream = connection.errorStream?.use {
                it.bufferedReader().readText()
            } ?: "Unknown error"
            throw Exception("HTTP Error $responseCode: ${connection.responseMessage}\n$errorStream")
        }
    }

    // 获取GitHub仓库的所有发行版信息
    fun getReleases(owner: String, repo: String): JsonArray {
        try {
            val apiUrl = "https://api.github.com/repos/$owner/$repo/releases"
            val jsonResponse = download(apiUrl)
            return Gson().fromJson(jsonResponse, JsonArray::class.java)
        } catch (e: Exception) {
            val jsonResponse = JsonArray()
            val jsonObject = JsonObject()
            val assetArray = JsonArray()
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_win-x64_net9.0_SelfContained.zip")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_win-x64_net9.0_SelfContained.zip")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_win-x86_net9.0_SelfContained.zip")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_win-x86_net9.0_SelfContained.zip")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_osx-x64_net9.0_SelfContained.tar.gz")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_osx-x64_net9.0_SelfContained.tar.gz")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_osx-arm64_net9.0_SelfContained.tar.gz")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_osx-arm64_net9.0_SelfContained.tar.gz")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_linux-x64_net9.0_SelfContained.tar.gz")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_linux-x64_net9.0_SelfContained.tar.gz")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_linux-arm64_net9.0_SelfContained.tar.gz")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_linux-arm64_net9.0_SelfContained.tar.gz")
            })
            assetArray.add(JsonObject().apply {
                addProperty("name", "Lagrange.OneBot_linux-arm_net9.0_SelfContained.tar.gz")
                addProperty("browser_download_url", "https://github.com/LagrangeDev/Lagrange.Core/releases/download/nightly/Lagrange.OneBot_linux-arm_net9.0_SelfContained.tar.gz")
            })
            jsonObject.add("assets", assetArray)
            jsonResponse.add(jsonObject)
            return jsonResponse
        }
    }
}