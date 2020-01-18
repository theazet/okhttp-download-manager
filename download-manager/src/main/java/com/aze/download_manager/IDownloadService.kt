package com.aze.download_manager

import com.aze.download_manager.task.TaskBean

interface IDownloadService {
    /**
     * 任务创建，分配TASK_ID
     */
    fun onCreate(taskBean: TaskBean)

    /**
     * 开始连接，下载即将开始，此阶段文件被创建
     */
    fun onStart(taskBean: TaskBean)

    /**
     * 连接成功，下载开始
     */
    fun onResume(taskBean: TaskBean)

    /**
     * 下载暂停，若想恢复下载，首先通过TASK_ID查询内存是否存在TASK，如果不存在则从数据库查询重新构建
     */
    fun onPause(taskBean: TaskBean)

    /**
     * 下载结束
     */
    fun onStop(taskBean: TaskBean)

    /**
     * 任务完全结束，销毁TASK_ID
     */
    fun onDestroy(taskBean: TaskBean)

    /**
     * 由STOP态返回，如果ID存在则直接开始
     */
    fun onRestart(taskBean: TaskBean)

    /**
     * 已知原因的错误，如网络错误，无访问权限等
     */
    fun onError()

    /**
     * 未知错误，数据库不再可靠，尽最大努力恢复数据，修复记录
     */
    fun onKnowError()

}