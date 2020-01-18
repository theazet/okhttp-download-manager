package com.aze.download_manager

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aze.download_manager.observer.Observer
import com.aze.download_manager.task.Task

import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        DownloadManager.init(appContext)
        val id = Task.Builder().url("http://xiazai.vpszuida.com/20200113/15378_9a4f2f01/交涉人电影版.BD日双语中字.mp4").build()


    }
}
