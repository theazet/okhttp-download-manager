package com.aze.download_manager.observer

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.aze.download_manager.DownloadManager
import com.aze.download_manager.AbsDownloadService
import com.aze.download_manager.IDownloadService
import com.aze.download_manager.task.TaskBean


class Observer
private constructor(var taskId: Int, var mService: IDownloadService) {
    private val handler: Handler = Handler(Looper.getMainLooper()) {
        val bean = it.obj as TaskBean
        when (it.what) {
            TaskBean.Status.CREATE.value -> mService.onCreate(bean)
            TaskBean.Status.START.value -> mService.onStart(bean)
            TaskBean.Status.RESUME.value -> mService.onResume(bean)
            TaskBean.Status.PAUSE.value -> mService.onPause(bean)
            TaskBean.Status.STOP.value -> mService.onStop(bean)
            TaskBean.Status.DESTROY.value -> mService.onDestroy(bean)
            TaskBean.Status.RESTART.value -> mService.onRestart(bean)
        }
        return@Handler true
    }

    fun callback(taskBean: TaskBean, status: TaskBean.Status) {
        val msg = Message()
        msg.what = status.value
        msg.obj = taskBean
        handler.sendMessage(msg)
    }

    fun destory() {
        DownloadManager.instance?.publisher?.unSubscribe(taskId)
    }

    internal fun error(reason: String, status: TaskBean.Status) {
        when (status) {
            TaskBean.Status.ERROR -> mService.onError()
            TaskBean.Status.UNKNOWN_ERROR -> mService.onKnowError()
        }
    }

    internal fun update(taskBean: TaskBean, status: TaskBean.Status) {
        val msg = Message()
        msg.what = status.value
        msg.obj = taskBean
        handler.sendMessage(msg)
    }

    fun create(service: AbsDownloadService): Observer {
        mService = service
        DownloadManager.instance?.publisher?.subscribe(taskId, this)
        return this
    }
    fun setService(service: IDownloadService){
        this.mService = service
    }
    class Builder(private val taskId: Int) {
        fun create(service: IDownloadService): Observer {
            val instance = Observer(taskId, service)
            DownloadManager.instance?.publisher?.subscribe(taskId, instance)
            return instance
        }

        fun get(): Observer? {
            return DownloadManager.instance?.publisher?.observers?.get(taskId)
        }
    }

}