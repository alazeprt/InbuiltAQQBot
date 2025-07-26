package top.alazeprt.iab

import top.alazeprt.iab.backend.BackendLoader
import top.alazeprt.iab.config.ConfigProvider
import top.alazeprt.iab.task.Cancelable
import top.alazeprt.iab.task.TaskProvider
import top.alazeprt.iab.util.LogLevel
import java.io.File

interface InbuiltAQQBot : ConfigProvider, TaskProvider {

    var dataDir: File

    var taskList: MutableList<Cancelable>

    var backendLoader: BackendLoader

    fun enable() {
        log(LogLevel.INFO, "Loading config ...")
        loadConfig(this)
        loadBackend()
    }

    fun loadBackend() {
        backendLoader = BackendLoader(this)
        backendLoader.download {
            backendLoader.launch()
        }
    }

    fun disable() {
        log(LogLevel.INFO, "Canceling all tasks ...")
        backendLoader.process?.destroyForcibly()
        taskList.forEach { it.cancel() }
        log(LogLevel.INFO, "Saving config ...")
        saveConfig(this)
    }

    fun log(level: LogLevel, message: String)
}