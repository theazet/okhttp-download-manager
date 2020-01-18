package com.aze.download_manager.state

import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils

class CreateState : State {

    override fun execute(taskBean: TaskBean) {
        DatabaseUtils.modify(taskBean)
    }
}