package com.omni.support.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

/**
 * @author 邱永恒
 *
 * @time 2018/5/28  17:30
 *
 * @desc 线程池管理
 *
 */

object AppExecutors {
    val DISK_IO: Executor by lazy {
        ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
    }
    val WORK_THREAD: Executor by lazy { Executors.newFixedThreadPool(3) }
    val MAIN_THREAD: Executor by lazy { MainThreadExecutor() }


    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}