package com.aze.download_manager.state

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils
import com.aze.download_manager.utils.FileChecker

class PauseState : State {
    override fun execute(taskBean: TaskBean) {
        taskBean.progress = FileChecker.checkExist(taskBean)
        DatabaseUtils.modify(taskBean)
    }
}