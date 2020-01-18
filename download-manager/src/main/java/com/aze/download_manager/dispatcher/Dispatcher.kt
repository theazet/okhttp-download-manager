package com.aze.download_manager.dispatcher

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.aze.download_manager.DownloadManager
import com.aze.download_manager.task.RealTask
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils
import okhttp3.Call
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.HashMap

/**
 * 只做线程切换和任务管理工作，具体下载任务分发给各State
 */
@SuppressLint("UseSparseArrays")
class Dispatcher(name: String) : HandlerThread(name) {
    private val waitingTask = PriorityQueue<TaskBean>()
    private val runningCall = HashMap<Int, Call>()
    private val pool = Executors.newFixedThreadPool(10)
    private val runningTask = HashMap<Int, Future<*>>()

    private val handler by lazy {
        Handler(looper) {
            when (it.what) {
                MSG_SEND_ERROR -> error(it.arg1, it.obj as String)
                MSG_ADD_CALL -> addCall(it.arg1, it.obj as Call)
                MSG_STOP_CALL -> pauseDownload(it.arg2, DatabaseUtils.intToStatus(it.arg1))
                MSG_UPDATE_STATUS -> update(it.obj as TaskBean, DatabaseUtils.intToStatus(it.arg1))
                MSG_UPDATE_STATUS_BY_ID -> update(it.arg2, DatabaseUtils.intToStatus(it.arg1))
                MSG_START_DOWNLOAD -> startDownload(it.obj as TaskBean)
                MSG_DOWNLOAD_FINISH -> downloadFinish(it.obj as TaskBean)
                MSG_DESTROY -> destroyThread()
            }
            return@Handler true
        }
    }

    companion object {
        const val MSG_SEND_ERROR = 3
        const val MSG_ADD_CALL = 4
        const val MSG_STOP_CALL = 5
        const val MSG_UPDATE_STATUS = 6
        const val MSG_UPDATE_STATUS_BY_ID = 7
        const val MSG_START_DOWNLOAD = 8
        const val MSG_DOWNLOAD_FINISH = 9
        const val MSG_DESTROY = 10
    }

    //----------------------------------------------------------------------------
    //----------------------------统一分派与管理任务--------------------------------
    //----------------------------------------------------------------------------

    private fun destroyThread(){
        for ((_,v) in runningCall){
            v.cancel()
        }
        pool.shutdown()
        this.quit()
    }

    /**
     * 下载链接建立
     */
    private fun addCall(id: Int, call: Call) {
        runningCall[id] = call
    }

    /**
     * 判断下载模式，如果按优先级则进入排队，否则直接创建下载线程
     */
    private fun startDownload(taskBean: TaskBean) {
        if (checkRunning(taskBean.id)) {
            return
        }
        DownloadManager.instance?.byPriority?.let {
            if (it && runningTask.isNotEmpty()) {
                waitingTask.offer(taskBean)
            } else {
                val task = pool.submit(
                    RealTask(
                        taskBean
                    )
                )
                runningTask[taskBean.id] = task
            }
        }
    }

    /**
     * 先取消下载链接,后中断下载线程
     */
    private fun pauseDownload(id: Int, status: TaskBean.Status) {
        runningCall[id]?.cancel()
        runningCall.remove(id)
        for (i in waitingTask) {
            if (i.id == id) {
                waitingTask.remove(i)
                break
            }
        }
        runningTask[id]?.cancel(true)
        runningTask.remove(id)
        update(id, status)
    }

    private fun downloadFinish(bean: TaskBean) {
        DownloadManager.instance?.byPriority?.let {
            if (it && waitingTask.isNotEmpty()) {
                val task = pool.submit(
                    RealTask(
                        waitingTask.poll()
                    )
                )
                runningTask[bean.id] = task
            }
        }
    }

    private fun update(task: TaskBean, status: TaskBean.Status) {
        DownloadManager.instance?.publisher?.onUpdate(task, status)
    }

    private fun update(id: Int, status: TaskBean.Status) {
        DownloadManager.instance?.publisher?.onUpdate(id, status)
    }

    private fun error(id: Int, cause: String) {
        DownloadManager.instance?.publisher?.onError(id, cause, TaskBean.Status.ERROR)
    }

    //---------------------------------------------------------------------------
    //---------------------------------线程切换-----------------------------------
    //---------------------------------------------------------------------------
    fun sendMessage(what: Int, task: TaskBean) {
        val msg = Message()
        msg.what = what
        msg.obj = task
        handler.sendMessage(msg)
    }

    fun sendMessage(what: Int) {
        val msg = Message()
        msg.what = what
        handler.sendMessage(msg)
    }

    fun sendMessage(what: Int, id: Int, call: Call) {
        val msg = Message()
        msg.what = what
        msg.obj = call
        msg.arg1 = id
        handler.sendMessage(msg)
    }

    fun sendMessage(what: Int, bean: TaskBean, status: TaskBean.Status) {
        val msg = Message()
        msg.what = what
        msg.obj = bean
        msg.arg1 = status.value
        handler.sendMessage(msg)
    }

    fun sendMessage(what: Int, taskId: Int, status: TaskBean.Status) {
        val msg = Message()
        msg.what = what
        msg.arg1 = status.value
        msg.arg2 = taskId
        handler.sendMessage(msg)
    }



    private fun checkRunning(id: Int): Boolean {
        var result = false
        for (i in waitingTask) {
            if (i.id == id) {
                result = true
            }
        }
        return result || runningTask.containsKey(id) || runningCall.containsKey(id)
    }
}