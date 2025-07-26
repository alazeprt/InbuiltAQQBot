package top.alazeprt.iab

import com.google.gson.JsonObject
import net.md_5.bungee.api.plugin.Plugin
import top.alazeprt.iab.backend.BackendLoader
import top.alazeprt.iab.bstats.Metrics
import top.alazeprt.iab.task.Cancelable
import top.alazeprt.iab.util.BungeecordTaskCancelable
import top.alazeprt.iab.util.LogLevel
import top.alazeprt.iab.util.SystemInfo
import java.io.File
import java.util.concurrent.TimeUnit

class InbuiltAQQBotBungeecord : InbuiltAQQBot, Plugin() {
    override lateinit var dataDir: File

    override var taskList: MutableList<Cancelable> = mutableListOf()

    override lateinit var backendLoader: BackendLoader

    override fun onEnable() {
        dataDir = dataFolder
    }

    override fun log(level: LogLevel, message: String) {
        when (level) {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warning(message)
            LogLevel.ERROR -> logger.severe(message)
        }
    }

    override fun enableStats() {
        val metrics = Metrics(this, 26662)
    }

    override lateinit var config: JsonObject

    override lateinit var system: SystemInfo

    override fun submit(task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.schedule(this, task, 0L, TimeUnit.SECONDS))
    }

    override fun submitAsync(task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.runAsync(this, task))
    }

    override fun submitLater(delay: Long, task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.schedule(this, task, delay, TimeUnit.SECONDS))
    }

    override fun submitLaterAsync(delay: Long, task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.schedule(this, { proxy.scheduler.runAsync(this, task) }, delay, TimeUnit.SECONDS))
    }

    override fun submitTimer(delay: Long, period: Long, task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.schedule(this, task, delay, period, TimeUnit.SECONDS))
    }

    override fun submitTimerAsync(delay: Long, period: Long, task: Runnable): Cancelable {
        return BungeecordTaskCancelable(proxy.scheduler.schedule(this, { proxy.scheduler.runAsync(this, task) }, delay, period, TimeUnit.SECONDS))
    }
}