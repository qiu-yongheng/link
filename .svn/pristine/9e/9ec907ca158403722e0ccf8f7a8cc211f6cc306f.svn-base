package com.omni.support.ble.link

import android.util.Log
import com.omni.support.ble.core.ILink
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.profile.SimpleBleCallbacks

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 11:25
 *
 * @desc 数据链路基类
 *
 */
abstract class Link : ILink {
    private var onReceivedListener: ILink.OnReceivedListener? = null
    private var onErrorListener: ILink.OnErrorListener? = null
    protected var callback: SimpleBleCallbacks? = null
    private var packResolver: IPackResolver? = null
    var isReadLog: Boolean = false


    fun setOnReceivedListener(listener: ILink.OnReceivedListener?) {
        onReceivedListener = listener
    }

    fun setOnErrorListener(listener: ILink.OnErrorListener?) {
        onErrorListener = listener
    }

    fun setOnBleOperationCallback(callback: SimpleBleCallbacks?) {
        this.callback = callback
    }

    protected fun onReceived(pack: IPack) {
        onReceivedListener?.onReceived(pack)
    }

    protected fun onError(e: Throwable) {
        if (onErrorListener != null)
            onErrorListener?.onError(e)
        else
            e.printStackTrace()
    }

    fun getPackResolver(): IPackResolver? {
        return packResolver
    }


    fun setDataPackAdapter(resolver: IPackResolver?): Link {
        this.packResolver = resolver
        return this
    }

    open fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }

    open fun error(message: String) {
        Log.e(this.javaClass.simpleName, message)
    }
}