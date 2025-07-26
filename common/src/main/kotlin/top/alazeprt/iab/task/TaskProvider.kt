package top.alazeprt.iab.task

interface TaskProvider {
    fun submit(task: Runnable): Cancelable

    fun submitAsync(task: Runnable): Cancelable

    fun submitLater(delay: Long, task: Runnable): Cancelable

    fun submitLaterAsync(delay: Long, task: Runnable): Cancelable

    fun submitTimer(delay: Long, period: Long, task: Runnable): Cancelable

    fun submitTimerAsync(delay: Long, period: Long, task: Runnable): Cancelable
}