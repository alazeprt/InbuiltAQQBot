package top.alazeprt.iab.util

import net.md_5.bungee.api.scheduler.ScheduledTask
import top.alazeprt.iab.task.Cancelable

class BungeecordTaskCancelable(val task: ScheduledTask): Cancelable {
    override fun cancel() {
        try {
            task.cancel()
        } catch (ignored: Exception) {}
    }
}