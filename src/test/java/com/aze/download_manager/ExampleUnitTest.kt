package com.aze.download_manager

import android.util.Log
import com.aze.download_manager.observer.Observer
import com.aze.download_manager.task.Task
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import java.util.concurrent.Executors

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a = Runnable {
            try {
                repeat(1000) {
                    if (!Thread.interrupted()) {
                        println(it)
                        Thread.sleep(200)
                    }else{
                        print("stop")
                    }
                }
            }catch (ignore:InterruptedException){
                print("stop")
            }
        }
        val pool = Executors.newFixedThreadPool(10)
        val task = pool.submit(a)
        task.cancel(true)
        print(task.isCancelled)
        Thread.sleep(10000)
    }

}
