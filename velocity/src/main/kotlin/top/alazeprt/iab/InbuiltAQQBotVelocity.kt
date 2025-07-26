package top.alazeprt.iab

import com.google.gson.JsonObject
import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import top.alazeprt.iab.backend.BackendLoader
import top.alazeprt.iab.bstats.Metrics
import top.alazeprt.iab.task.Cancelable
import top.alazeprt.iab.util.LogLevel
import top.alazeprt.iab.util.SystemInfo
import top.alazeprt.iab.util.VelocityTaskCancelable
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Plugin(id = "inbuiltaqqbot", name = "InbuiltAQQBot", version = "1.0-beta.1", authors = ["alazeprt"])
class InbuiltAQQBotVelocity : InbuiltAQQBot {

    private var server: ProxyServer
    private var logger: Logger

    @Inject
    constructor(server: ProxyServer, logger: Logger, dataDirectory: Path) {
        this.server = server
        this.logger = logger
        this.dataDir = dataDirectory.toFile()
    }

    override var dataDir: File

    override var taskList: MutableList<Cancelable> = mutableListOf()

    override lateinit var backendLoader: BackendLoader

    override fun log(level: LogLevel, message: String) {
        when (level) {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warn(message)
            LogLevel.ERROR -> logger.error(message)
        }
    }

    override fun enableStats() {}

    override lateinit var config: JsonObject

    override lateinit var system: SystemInfo

    override fun submit(task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, task).schedule())
    }

    override fun submitAsync(task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, Runnable { Thread(task).start() }).schedule())
    }

    override fun submitLater(delay: Long, task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, task).delay(delay, TimeUnit.SECONDS).schedule())
    }

    override fun submitLaterAsync(delay: Long, task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, Runnable { Thread(task).start() }).delay(delay, TimeUnit.SECONDS).schedule())
    }

    override fun submitTimer(delay: Long, period: Long, task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, task).delay(delay, TimeUnit.SECONDS).repeat(period, TimeUnit.SECONDS).schedule())
    }

    override fun submitTimerAsync(delay: Long, period: Long, task: Runnable): Cancelable {
        return VelocityTaskCancelable(server.scheduler.buildTask(this, Runnable { Thread(task).start() }).delay(delay, TimeUnit.SECONDS).repeat(period, TimeUnit.SECONDS).schedule())
    }
}