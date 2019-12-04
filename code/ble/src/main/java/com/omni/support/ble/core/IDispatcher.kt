package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2018/12/18  09:19
 *
 * @desc 线程调度
 *
 */

interface IDispatcher {
    fun <T> execute(runnable: () -> IResp<T>): IResp<T>

    fun enqueue(runnable: () -> Unit)
}