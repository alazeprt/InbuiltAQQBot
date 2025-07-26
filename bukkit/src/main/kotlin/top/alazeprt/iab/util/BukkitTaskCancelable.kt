package top.alazeprt.iab.util

import org.bukkit.scheduler.BukkitTask
import top.alazeprt.iab.task.Cancelable

class BukkitTaskCancelable(private val task: BukkitTask): Cancelable {
    override fun cancel() {
        if (!task.isCancelled) task.cancel()
    }
}