package com.aze.download_manager

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.aze.download_manager.dispatcher.Dispatcher
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_DESTROY
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_STOP_CALL
import com.aze.download_manager.dispatcher.Dispatcher.Companion.MSG_UPDATE_STATUS_BY_ID
import com.aze.download_manager.observer.Publisher
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils
import okhttp3.OkHttpClient
import java.lang.Exception

class DownloadManager private constructor(val context: Context, val byPriority: Boolean) {
    internal var dispatcher: Dispatcher? = null
    internal val mClient = OkHttpClient()
    internal val publisher = Publisher()


    companion object {
        const val TAG = "DownloadManager_LOG"

        var instance: DownloadManager? = null
            get() {
                return if (field == null) {
                    throw Exception("Please init DownloadManager at Application.onCreate() first!")
                } else {
                    field
                }
            }


        fun init(context: Context) {
            init(context, true)  //默认按照优先级顺序下载(优先级相同时按FIFO规则)
        }

        fun init(context: Context, byPriority: Boolean) {
            instance = DownloadManager(context, byPriority)
            instance?.dispatcher = Dispatcher("download_thread")
            instance?.dispatcher?.start()
        }


        fun pause(taskId: Int) {
            instance?.dispatcher?.sendMessage(
                MSG_STOP_CALL,
                taskId,
                TaskBean.Status.PAUSE
            )
        }

        fun destory() {
            instance?.dispatcher?.quit()
            instance = null
            instance?.dispatcher?.sendMessage(MSG_DESTROY)
        }

        fun start(taskId: Int) {
            instance?.dispatcher?.sendMessage(
                MSG_UPDATE_STATUS_BY_ID,
                taskId,
                TaskBean.Status.START
            )
        }

        fun getTasks(): ArrayList<TaskBean> {
            return DatabaseUtils.queryUnfinishedTasks()
        }

        fun getClient(): OkHttpClient? {
            return instance?.mClient
        }
    }

}