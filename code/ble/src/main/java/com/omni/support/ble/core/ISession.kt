package com.omni.support.ble.core

import com.omni.support.ble.session.ISessionListener

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 9:44
 *
 * @desc 会话接口
 *
 */
interface ISession {
    /**
     * 连接
     */
    fun connect()

    /**
     * 连接成功后, 初始化配置, 手动调用
     */
    fun initConfig()

    /**
     * 断开连接
     */
    fun disConnect()

    /**
     * 是否已连接
     */
    fun isConnect(): Boolean

    /**
     * 发送
     */
    fun send(command: ICommand<*>): IResponse

    /**
     * 获取response
     */
    fun getResponse(commandId: Int): IResponse

    /**
     * 创建call
     */
    fun <T> call(command: ICommand<T>): ISessionCall<T>

    fun <T> call(vararg command: ICommand<T>): ISessionCall<T>

    /**
     * 设置回调
     */
    fun setListener(listener: ISessionListener?)

    /**
     * 是否发送了读取日志的指令
     */
    fun isReadLog(isReadLog: Boolean)
}