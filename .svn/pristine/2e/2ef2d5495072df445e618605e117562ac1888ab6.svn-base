package com.omni.support.ble.task

import android.util.Log

/**
 * @author 邱永恒
 *
 * @time 2019/8/14 18:16
 *
 * @desc
 *
 */
abstract class BaseTask<T>(protected val listener: OnProgressListener<T>) : ITask {
    private var tag = this.javaClass.simpleName

    fun log(message: String) {
        Log.d(tag, message)
    }

    fun logWarning(message: String) {
        Log.w(tag, message)
    }

    fun logError(message: String) {
        Log.e(tag, message)
    }

    /**
     * 重试
     *
     */
    fun retry(retry: Int, block: (Int) -> Boolean) {
        var count = 0       // >1 表示重试
        do {
            if (try {
                    block(count)
                } catch (e: Exception) {
                    false
                }
            ) {
                break
            }
        } while (count++ < retry)
    }
}