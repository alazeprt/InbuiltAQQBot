package top.alazeprt.iab.util

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import top.alazeprt.iab.task.Cancelable

class FoliaTaskCancelable(val task: ScheduledTask): Cancelable {

    override fun cancel() {
        if (!task.isCancelled) task.cancel()
    }
}