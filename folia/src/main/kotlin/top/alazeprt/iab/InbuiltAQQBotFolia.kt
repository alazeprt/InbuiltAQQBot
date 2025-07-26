package top.alazeprt.iab

import com.google.gson.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import top.alazeprt.iab.backend.BackendLoader
import top.alazeprt.iab.bstats.Metrics
import top.alazeprt.iab.task.Cancelable
import top.alazeprt.iab.util.FoliaTaskCancelable
import top.alazeprt.iab.util.LogLevel
import top.alazeprt.iab.util.SystemInfo
import java.io.File
import java.util.concurrent.TimeUnit

class InbuiltAQQBotFolia : JavaPlugin(), InbuiltAQQBot {
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
        return FoliaTaskCancelable(server.globalRegionScheduler.run(this, ) { task.run() })
    }

    override fun submitAsync(task: Runnable): Cancelable {
        return FoliaTaskCancelable(server.asyncScheduler.runNow(this) { task.run() })
    }

    override fun submitLater(delay: Long, task: Runnable): Cancelable {
        return FoliaTaskCancelable(server.globalRegionScheduler.runDelayed(this, { task.run() }, delay * 20L))
    }

    override fun submitLaterAsync(delay: Long, task: Runnable): Cancelable {
        return FoliaTaskCancelable(server.asyncScheduler.runDelayed(this, { task.run() }, delay, TimeUnit.SECONDS))
    }

    override fun submitTimer(delay: Long, period: Long, task: Runnable): Cancelable {
        return FoliaTaskCancelable(server.globalRegionScheduler.runAtFixedRate(this, { task.run() }, delay * 20L, period * 20L))
    }

    override fun submitTimerAsync(delay: Long, period: Long, task: Runnable): Cancelable {
        return FoliaTaskCancelable(server.asyncScheduler.runAtFixedRate(this, { task.run() }, delay, period, TimeUnit.SECONDS))
    }

    override fun onEnable() {
        dataDir = dataFolder
        enable()
    }

    override fun onDisable() {
        disable()
    }

    override fun enableStats() {
        val metrics = Metrics(this, 26662)
    }
}