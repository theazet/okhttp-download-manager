package com.aze.download_manager.state

import com.aze.download_manager.DownloadManager
import com.aze.download_manager.utils.UrlChecker
import com.aze.download_manager.dispatcher.Dispatcher
import com.aze.download_manager.task.TaskBean
import com.aze.download_manager.utils.DatabaseUtils

class StartState : State {
    /**
     * 1 -> RunningTask 已经开始运行，什么也不做
     * 2 -> WaitingTask 已经进入等待队列，什么也不做
     * 3 -> 数据库 已经初始化完毕
     *      1 -> total>0 说明下载有效 ，则将其移入WaitingTask
     *      2 -> total=0 则验证下载有效性
     *          1 -> 有效，则将其移入WaitingTask
     *          2 -> 无效，则抛异常
     * 未找到 -> 未知错误
     */
    override fun execute(taskBean: TaskBean) {
        val len = UrlChecker.getContentLength(taskBean.url)
        if (len <= 0) {
            DownloadManager.instance?.publisher?.onError(
                taskBean.id,
                "Please confirm if the url is valid!",
                TaskBean.Status.UNKNOWN_ERROR
            )
        } else {
            taskBean.total = len
            DatabaseUtils.modify(taskBean)
            DownloadManager.instance?.dispatcher?.sendMessage(
                Dispatcher.MSG_START_DOWNLOAD,
                taskBean,
                TaskBean.Status.RESUME
            )
        }
//        if (taskBean.total > 0) {
//            DownloadManager.instance?.dispatcher?.enqueue(taskBean)
//        }else {
//
//        }
    }


}