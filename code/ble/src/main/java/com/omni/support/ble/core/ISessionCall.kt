package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:44
 *
 * @desc call
 *
 */
interface ISessionCall<T> {
    /**
     * 回话的session
     */
    fun session(session: ISession): ISessionCall<T>

    /**
     * 重试次数
     * @param retry 0:不重试,n:重试n次
     */
    fun retry(retry: Int): ISessionCall<T>

    /**
     * 超时
     */
    fun timeout(timeout: Long): ISessionCall<T>

    /**
     * 异步回调超时
     */
    fun asyncTimeout(timeout: Long): ISessionCall<T>

    /**
     * 执行（同步）
     */
    fun execute(): IResp<T>

    /**
     * 执行（异步）
     * @param callback 回调callback，包含成功与出错两个事件
     */
    fun enqueue(callback: SessionCallback<T>?)

    /**
     * 不用请求, 直接订阅通知
     */
    fun subscribe(callback: NotifyCallback<T>)

    /**
     * 异步回调接收多个结果
     */
    fun asyncCall(callback: AsyncCallback<T>)

    /**
     * 取消执行
     */
    fun cancel()

    /**
     * 设置线程调度器
     */
    fun dispatcher(dispatcher: IDispatcher): ISessionCall<T>
}