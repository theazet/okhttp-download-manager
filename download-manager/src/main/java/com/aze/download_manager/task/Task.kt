package com.aze.download_manager.task

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_UPDATE_STATUS
import com.aze.download_manager.utils.DatabaseUtils
import java.lang.Exception


class Task private constructor(var taskBean: TaskBean) {
    companion object {
        @JvmStatic
        val PRIORITY_NORMAL = 10 //优先级默认为10(排队模式 默认)

        @JvmStatic
        val PRIORITY_START_IMMEDIATELY = 100 //立即开始，不再加入队列
    }

    class Builder {
        private var path: String? =
            DownloadManager.instance?.context?.filesDir?.path  //默认下载地址在: 包名/files/
        private var url: String? = null             //*必设项*
            get() {
                return if (field == null) throw Exception("Have not already set an url")
                else field
            }

        private var fileName: String? = null        //默认名称为url中的资源名
            get() {
                return if (!field.isNullOrEmpty()) {
                    field
                } else {
                    url?.let {
                        it.substring(it.lastIndexOf("/")+1)
                    }
                }
            }

        private var priority: Int =
            if (DownloadManager.instance!!.byPriority) PRIORITY_NORMAL else PRIORITY_START_IMMEDIATELY

        fun priority(priority: Int) = apply {
            this.priority = priority
        }

        fun url(url: String) = apply {
            this.url = url
        }

        fun path(path: String) = apply {
            this.path = path
        }

        fun fileName(fileName: String) = apply {
            this.fileName = fileName
        }

        fun build(): Int {
            val bean = DatabaseUtils.insert(
                url!!,
                path!!,
                fileName!!,
                priority,
                TaskBean.Status.CREATE
            )
            DownloadManager.instance?.dispatcher?.sendMessage(
                MSG_UPDATE_STATUS,
                bean,
                TaskBean.Status.CREATE
            )
            return bean.id
        }
    }


}