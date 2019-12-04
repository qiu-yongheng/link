package com.omni.support.ble.task

/**
 * @author 邱永恒
 *
 * @time 2019/8/14 18:15
 *
 * @desc
 *
 */
interface ITask {
    object Status {

        // 成功
        val SUCESS = 1
        // 失败
        val FAIL = 2
        // 异常
        val ERROR = 3
    }

    fun start()

    fun stop()
}