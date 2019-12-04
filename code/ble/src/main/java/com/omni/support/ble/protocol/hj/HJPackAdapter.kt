package com.omni.support.ble.protocol.hj

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 11:59
 *
 * @desc
 *
 */
class HJPackAdapter : IPackResolver {
    private val frame = ByteArray(16)

    override fun resolver(queue: IPackResolver.ReceivedBufferQueue): IPack {
        return onResult(queue)
    }

    /**
     * 结果
     */
    private fun onResult(queue: IPackResolver.ReceivedBufferQueue): IPack {
        // command
        frame[0] = queue.take()
        frame[1] = queue.take()
        // len
        frame[2] = queue.take()
        val len = frame[2].toInt() and 0xFF
        if (len < 0 || len > 13) {
            debug("len error: $len")
            return EmptyPack()
        }
        debug("len = $len, command = ${(frame[0].toInt() and 0xFF) or (frame[1].toInt() and 0xFF)}")

        // payload
        for (i in 0 until 13) {
            frame[3 + i] = queue.take()
        }

        return HJPack.from(frame)
    }

    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }
}