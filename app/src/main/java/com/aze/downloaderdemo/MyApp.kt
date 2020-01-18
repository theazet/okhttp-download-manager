package com.aze.downloaderdemo

import android.app.Application
import com.aze.download_manager.DownloadManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DownloadManager.init(this)
    }
}