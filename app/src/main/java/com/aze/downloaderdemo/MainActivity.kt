package com.aze.downloaderdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aze.download_manager.DownloadManager
import com.aze.download_manager.observer.Observer
import com.aze.download_manager.AbsDownloadService
import com.aze.download_manager.task.Task
import com.aze.download_manager.task.TaskBean

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        DownloadManager.init(this)
        var id = Task.Builder()
            .url("下载链接")    //必设项
            .path("下载位置")         //非必设,默认下载地址在: data/data/包名/files/
            .fileName("文件名")    //非必设,默认名称为url中的资源名
            .priority(10)       //可以设置下载优先级,默认为10
            .build()
        setContentView(R.layout.activity_main)
//        var id = 0
        start.setOnClickListener {
            id = Task.Builder().url("http://xiazai.vpszuida.com/20200113/15378_9a4f2f01/交涉人电影版.BD日双语中字.mp4").build()
            Observer.Builder(id).create(object :
                AbsDownloadService(){
                override fun onResume(taskBean: TaskBean) {
                    super.onResume(taskBean)
                    total.text = taskBean.total.toString()
                    progress.text = taskBean.progress.toString()
                    progressBar.progress = ((taskBean.progress * 100) / taskBean.total).toInt()
                }
            })
            DownloadManager.start(id)
        }
        stop.setOnClickListener {
            DownloadManager.pause(id)
        }

    }



}
