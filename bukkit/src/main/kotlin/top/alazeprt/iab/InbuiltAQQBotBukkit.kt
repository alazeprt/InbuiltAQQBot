package top.alazeprt.iab

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.alazeprt.iab.backend.BackendLoader
import top.alazeprt.iab.task.Cancelable
import top.alazeprt.iab.util.BukkitTaskCancelable
import top.alazeprt.iab.util.LogLevel
import top.alazeprt.iab.util.SystemInfo
import java.io.File

class InbuiltAQQBotBukkit : JavaPlugin(), InbuiltAQQBot {
    override lateinit var dataDir: File

    override fun log(level: LogLevel, message: String) {
        when (level) {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warning(message)
            LogLevel.ERROR -> logger.severe(message)
        }
    }

    override lateinit var config: JsonObject

    override lateinit var system: SystemInfo

    override lateinit var backendLoader: BackendLoader

    override var taskList: MutableList<Cancelable> = mutableListOf()

    override fun submit(task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTask(this, task))
    }

    override fun submitAsync(task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTaskAsynchronously(this, task))
    }

    override fun submitLater(delay: Long, task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTaskLater(this, task, delay * 50L))
    }

    override fun submitLaterAsync(delay: Long, task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay * 50L))
    }

    override fun submitTimer(delay: Long, period: Long, task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTaskTimer(this, task, delay * 50L, period * 50L))
    }

    override fun submitTimerAsync(delay: Long, period: Long, task: Runnable): Cancelable {
        return BukkitTaskCancelable(Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, delay * 50L, period * 50L))
    }

    override fun onEnable() {
        dataDir = dataFolder
        enable()
    }

    override fun onDisable() {
        disable()
    }
}