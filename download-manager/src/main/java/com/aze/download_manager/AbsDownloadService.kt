package com.aze.download_manager

import android.util.Log
import com.aze.download_manager.task.TaskBean

abstract class AbsDownloadService : IDownloadService {

    override fun onCreate(taskBean: TaskBean) {
        Log.d(DownloadManager.TAG, "Download Task ${taskBean.id}:${taskBean.fileName} Created !")
    }

    override fun onStart(taskBean: TaskBean) {
        Log.d(DownloadManager.TAG, "Download Task ${taskBean.id}:${taskBean.fileName} Started !")
    }

    override fun onResume(taskBean: TaskBean) {}

    override fun onPause(taskBean: TaskBean) {
        Log.d(DownloadManager.TAG, "Download Task ${taskBean.id}:${taskBean.fileName} Paused !")
    }

    override fun onStop(taskBean: TaskBean) {
        Log.d(DownloadManager.TAG, "Download Task ${taskBean.id}:${taskBean.fileName} Stopped !")
    }

    override fun onDestroy(taskBean: TaskBean) {}

    override fun onRestart(taskBean: TaskBean) {}

    override fun onError() {}

    override fun onKnowError() {}
}