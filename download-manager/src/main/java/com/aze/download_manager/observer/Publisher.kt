package com.aze.download_manager.observer

import android.annotation.SuppressLint
import com.aze.download_manager.DownloadManager
import com.aze.download_manager.state.StateFactory
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils
import java.util.*
import kotlin.collections.HashMap

class Publisher {
    @SuppressLint("UseSparseArrays")
    internal val observers = HashMap<Int, Observer>()

    fun subscribe(taskId: Int, observer: Observer) {
        observers[taskId] = observer
    }

    fun unSubscribe(taskId: Int) {
        observers.remove(taskId)
    }

    fun onUpdate(taskId: Int, newStatus: TaskBean.Status) {
        val bean = DatabaseUtils.queryById(taskId) ?: return
        val state = StateFactory.create(newStatus)
        bean.status = newStatus
        state?.execute(bean)
        for ((k, v) in observers) {
            if (k == bean.id && observers[bean.id] != null) {
                v.update(bean, newStatus)
            }
        }
    }


    fun onUpdate(taskBean: TaskBean, newStatus: TaskBean.Status) {
        val state = StateFactory.create(newStatus)
        taskBean.status = newStatus
        state?.execute(taskBean)
        for ((k, v) in observers) {
            if (k == taskBean.id && observers[taskBean.id] != null) {
                v.update(taskBean, newStatus)
            }
        }
    }

    fun onError(id: Int, reason: String, status: TaskBean.Status) {
        for ((k, v) in observers) {
            if (k == id && observers[id] != null) {
                v.error(reason, status)
            }
        }
    }
}