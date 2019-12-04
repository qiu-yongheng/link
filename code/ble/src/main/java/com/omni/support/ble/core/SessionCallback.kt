package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  17:25
 *
 * @desc call异步回调
 *
 */

interface SessionCallback<T> {
    fun onSuccess(call: ISessionCall<T>, data: IResp<T>)
    fun onFailure(call: ISessionCall<T>, e: Throwable)
}