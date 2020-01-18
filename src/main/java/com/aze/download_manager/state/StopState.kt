package com.aze.download_manager.state

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils

class StopState : State {
    override fun execute(taskBean: TaskBean) {
        DatabaseUtils.modify(taskBean)
        DownloadManager.instance?.publisher?.onUpdate(taskBean,TaskBean.Status.DESTROY)
    }
}