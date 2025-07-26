package top.alazeprt.iab.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import top.alazeprt.iab.InbuiltAQQBot
import top.alazeprt.iab.util.Arch
import top.alazeprt.iab.util.SystemInfo
import java.io.File

interface ConfigProvider {
    var config: JsonObject
    var system: SystemInfo

    fun loadConfig(plugin: InbuiltAQQBot) {
        plugin.dataDir.mkdirs()
        val configFile = File(plugin.dataDir, "config.json")
        if (!configFile.exists()) {
            configFile.createNewFile()
            config = JsonObject()
            config.addProperty("os_name", System.getProperty("os.name").lowercase())
            config.addProperty("os_arch", System.getProperty("os.arch").lowercase())
            configFile.writeText(Gson().toJson(config))
        } else {
            config = Gson().fromJson(configFile.readText(), JsonObject::class.java)
            if (!config.has("os_name")) {
                config.addProperty("os_name", System.getProperty("os.name").lowercase())
            }
            if (!config.has("os_arch")) {
                config.addProperty("os_arch", System.getProperty("os.arch").lowercase())
            }
        }
        parseOS()
    }

    fun parseOS() {
        val system = config.get("os_name").asString
        val arch = config.get("os_arch").asString
        if (system.contains("win") && arch.contains("64") && !arch.contains("aarch") && !arch.contains("arm")) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.WINDOWS, Arch.x86_64)
        } else if (system.contains("win") && (arch.contains("86") || arch.contains("32") && !arch.contains("aarch") && !arch.contains("arm"))) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.WINDOWS, Arch.x86)
        } else if (system.contains("linux") && arch.contains("64") && !arch.contains("aarch") && !arch.contains("arm")) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.LINUX, Arch.x86_64)
        } else if (system.contains("linux") && (arch.contains("aarch") || arch.contains("arm")) && arch.contains("64")) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.LINUX, Arch.arm64)
        } else if ((system.contains("mac") || system.contains("osx")) && arch.contains("64") && !arch.contains("aarch") && !arch.contains("arm")) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.MAC, Arch.x86_64)
        } else if ((system.contains("mac") || system.contains("osx")) && arch.contains("64") && (arch.contains("aarch") || arch.contains("arm"))) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.MAC, Arch.arm64)
        } else if (system.contains("linux") && (arch.contains("86") || arch.contains("32") && (arch.contains("aarch") || arch.contains("arm")))) {
            this.system = SystemInfo(top.alazeprt.iab.util.System.LINUX, Arch.arm32)
        } else {
            this.system = SystemInfo(null, null)
        }
    }

    fun saveConfig(plugin: InbuiltAQQBot) {
        val configFile = File(plugin.dataDir, "config.json")
        configFile.writeText(Gson().toJson(config))
    }
}