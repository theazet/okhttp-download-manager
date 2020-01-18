package com.aze.download_manager.utils

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_ADD_CALL
import com.aze.download_manager.task.TaskBean
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object UrlChecker {

    fun getContentLength(url: String): Long {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        try {
            val response: Response? = DownloadManager.instance?.mClient?.newCall(request)?.execute()
            if (response != null && response.isSuccessful) {
                val contentLength: Long = response.body!!.contentLength()
                response.close()
                return if (contentLength == 0L) -1 else contentLength
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }
}