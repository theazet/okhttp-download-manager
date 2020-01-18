package com.aze.download_manager.state

import com.aze.download_manager.task.TaskBean

interface State {
    fun execute(taskBean: TaskBean)
}