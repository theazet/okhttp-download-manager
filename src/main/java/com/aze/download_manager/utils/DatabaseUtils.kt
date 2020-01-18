package com.aze.download_manager.utils

import android.content.ContentValues
import com.aze.download_manager.DownloadManager
import com.aze.download_manager.task.TaskBean

object DatabaseUtils {
    private val context = DownloadManager.instance?.context!!
    private val databaseHelper = DatabaseHelper(context)

    fun insert(
        url: String,
        path: String,
        name: String,
        priority: Int,
        status: TaskBean.Status
    ): TaskBean {
        queryByUrl(url).let {
            if (it.isNotEmpty()) {
                for (i in it){
                    if (name == i.fileName|| path == i.path){
                        return i
                    }
                }
            }
        }
        val writer = databaseHelper.writableDatabase
        val value = ContentValues()
        value.put("url", url)
        val time = System.currentTimeMillis()
        value.put("time", time)
        value.put("status", status.value)
        value.put("path", path)
        value.put("name", name)
        value.put("priority", priority)
        value.put("support", 1)
        value.put("total", 0)
        value.put("progress", 0)
        val id = writer.insert("tasks", null, value)
        writer.close()
        return TaskBean(
            id.toInt(), time, url, name, path, status, priority, -1, 0, 0
        )
    }

    private fun query(selection: String, selectionArgs: Array<String>): ArrayList<TaskBean> {
        val list = ArrayList<TaskBean>()
        val reader = databaseHelper.readableDatabase
        val cursor = reader.query("tasks", null, selection, selectionArgs, null, null, null)
        while (cursor.moveToNext()) {
            list.add(
                TaskBean(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getLong(cursor.getColumnIndex("time")),
                    cursor.getString(cursor.getColumnIndex("url")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    intToStatus(cursor.getInt(cursor.getColumnIndex("status"))),
                    cursor.getInt(cursor.getColumnIndex("priority")),
                    cursor.getShort(cursor.getColumnIndex("support")),
                    cursor.getLong(cursor.getColumnIndex("total")),
                    cursor.getLong(cursor.getColumnIndex("progress"))
                )
            )
        }
        cursor.close()
        reader.close()
        return list
    }

    fun modify(taskBean: TaskBean) {
        val writer = databaseHelper.writableDatabase
        val value = ContentValues()
        value.put("url", taskBean.url)
        value.put("time", taskBean.timestamp)
        value.put("status", taskBean.status.value)
        value.put("path", taskBean.path)
        value.put("name", taskBean.fileName)
        value.put("priority", taskBean.priority)
        value.put("support", taskBean.support)
        value.put("total", taskBean.total)
        value.put("progress", taskBean.progress)
        writer.update("tasks", value, "id=?", arrayOf("${taskBean.id}"))
        writer.close()
    }

    private fun delete(whereClause: String, whereArgs: Array<String>) {
        val writer = databaseHelper.writableDatabase
        writer.delete("tasks", whereClause, whereArgs)
        writer.close()
    }

    fun deleteById(id: Int) {
        delete("id=?", arrayOf("$id"))
    }

    fun deleteByUrl(url: String) {
        delete("url=?", arrayOf(url))
    }

    fun queryById(id: Int): TaskBean? {
        query("id=?", arrayOf("$id")).let {
            return if (it.isEmpty()) null
            else it[0]
        }
    }

    fun queryByUrl(url: String): ArrayList<TaskBean> {
        return query("url=?", arrayOf(url))
    }

    fun queryUnfinishedTasks(): ArrayList<TaskBean> {
        val list = ArrayList<TaskBean>()
        val reader = databaseHelper.readableDatabase
        val cursor = reader.query("tasks", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            list.add(
                TaskBean(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getLong(cursor.getColumnIndex("time")),
                    cursor.getString(cursor.getColumnIndex("url")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    intToStatus(cursor.getInt(cursor.getColumnIndex("status"))),
                    cursor.getInt(cursor.getColumnIndex("priority")),
                    cursor.getShort(cursor.getColumnIndex("support")),
                    cursor.getLong(cursor.getColumnIndex("total")),
                    cursor.getLong(cursor.getColumnIndex("progress"))
                )
            )
        }
        cursor.close()
        reader.close()
        return list
    }

    fun intToStatus(value: Int): TaskBean.Status {
        for (i in TaskBean.Status.values()) {
            if (i.value == value) {
                return i
            }
        }
        return TaskBean.Status.UNKNOWN_ERROR
    }
}