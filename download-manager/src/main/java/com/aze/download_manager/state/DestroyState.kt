package com.aze.download_manager.state

import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils

class DestroyState :State {
    override fun execute(taskBean: TaskBean) {
        DatabaseUtils.deleteById(taskBean.id)
    }
}