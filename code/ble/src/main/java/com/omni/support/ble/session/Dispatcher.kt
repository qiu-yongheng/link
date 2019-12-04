package com.omni.support.ble.session

import com.omni.support.ble.core.IDispatcher
import com.omni.support.ble.core.IResp
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  19:01
 *
 * @desc
 *
 */

class Dispatcher(private var executorService: ExecutorService? = null) : IDispatcher {
    /** 控制同步任务并发 */
    private var lock = ReentrantLock()

    /**
     * 如果ExecutorService == null, 创建默认ExecutorService
     */
    @Synchronized
    private fun executorService(): ExecutorService {
        if (executorService == null) {
            executorService = ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    SynchronousQueue<Runnable>(), threadFactory("Session Dispatcher", false))
        }
        return executorService!!
    }

    private fun threadFactory(name: String, daemon: Boolean): ThreadFactory {
        return ThreadFactory { runnable ->
            val result = Thread(runnable, name)
            result.isDaemon = daemon
            result
        }
    }

    override fun <T> execute(runnable: () -> IResp<T>): IResp<T> {
        lock.lock()
        try {
            return runnable()
        } finally {
            lock.unlock()
        }
    }

    @Synchronized
    override fun enqueue(runnable: () -> Unit) {
        executorService().execute {
            lock.lock()
            try {
                runnable()
            } finally {
                lock.unlock()
            }
        }
    }
}