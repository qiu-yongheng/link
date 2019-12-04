package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  17:19
 *
 * @desc 指令接口
 *
 */

interface ICommand<RESULT> {

    fun getCmd(): Int
    fun setCmd(cmd: Int)

    fun getResponseCmd(): Int
    fun setResponseCmd(cmd: Int)

    fun setNoResponse(isNoResponse: Boolean)
    fun isNoResponse(): Boolean

    fun getData(): ByteArray
    fun setData(data: ByteArray)

    /**
     * 接收到数据, 对数据进行解析
     */
    fun onResult(buffer: ByteArray?): RESULT?
}