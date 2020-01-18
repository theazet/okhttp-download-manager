package com.aze.download_manager.task

data class TaskBean(
    var id: Int,
    var timestamp: Long,
    var url: String,
    var fileName: String,
    var path: String,
    var status: Status,
    var priority: Int,
    var support: Short,
    var total: Long,
    var progress: Long
) : Comparable<TaskBean> {
    enum class Status(val value: Int) {
        CREATE(1),
        START(2),
        RESUME(3),
        PAUSE(4),
        STOP(5),
        DESTROY(6),
        RESTART(7),
        ERROR(8),
        UNKNOWN_ERROR(9)
    }

    override fun compareTo(other: TaskBean): Int {
        return other.priority - priority
    }
}