package com.aze.download_manager.utils

import com.aze.download_manager.task.TaskBean
import java.io.File

object FileChecker {

    fun checkExist(bean: TaskBean): Long {
        val file = File(bean.path)
        file.mkdirs()
        for (i in file.list()) {
            if (i == bean.fileName) {
                return File(bean.path, bean.fileName).length()
            }
        }
        return 0L
    }


}