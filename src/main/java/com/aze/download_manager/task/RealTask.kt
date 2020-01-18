package com.aze.download_manager.task

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.dispatcher.Dispatcher
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_DOWNLOAD_FINISH
import com.aze.download_manager.utils.FileChecker
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class RealTask(var taskBean: TaskBean) : Runnable {
    override fun run() {
        val instance = DownloadManager.instance
        val url = taskBean.url
        val publisher = instance?.publisher
        var progress: Long = FileChecker.checkExist(taskBean)
        if (taskBean.total <= progress) {
            publisher?.onUpdate(taskBean, TaskBean.Status.STOP)
            return
        }
        publisher?.onUpdate(taskBean, TaskBean.Status.RESUME)
        val request: Request = Request.Builder()
            .addHeader("RANGE", "bytes=$progress-${taskBean.total}")
            .url(url)
            .build()
        val call: Call = instance?.mClient?.newCall(request)!!

        instance.dispatcher?.sendMessage(
            Dispatcher.MSG_ADD_CALL,
            taskBean.id,
            call
        )
        val response: Response = call.execute()
        val file = File(taskBean.path, taskBean.fileName)
        var inputStream: InputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            if (response.body == null) {
                publisher?.onError(taskBean.id, response.message, TaskBean.Status.ERROR)
            } else {
                inputStream = response.body!!.byteStream()
                fileOutputStream = FileOutputStream(file, true)
                val buffer = ByteArray(2048)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    fileOutputStream.write(buffer, 0, len)
                    progress += len.toLong()
                    taskBean.progress = progress
                    publisher?.onUpdate(taskBean, TaskBean.Status.RESUME)
                }
                fileOutputStream.flush()
                publisher?.onUpdate(taskBean, TaskBean.Status.STOP)
            }
        } finally {
            inputStream?.close()
            fileOutputStream?.close()
            instance.dispatcher?.sendMessage(MSG_DOWNLOAD_FINISH, taskBean)
        }
    }


}