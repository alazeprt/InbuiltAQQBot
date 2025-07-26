package top.alazeprt.iab.util

import com.velocitypowered.api.scheduler.ScheduledTask
import top.alazeprt.iab.task.Cancelable

class VelocityTaskCancelable(val task: ScheduledTask): Cancelable {
    override fun cancel() {
        try {
            task.cancel()
        } catch (ignored: Exception) {}
    }
}