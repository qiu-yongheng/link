package com.omni.support.ble.protocol.base.abde

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 10:44
 *
 * @desc
 *
 */
abstract class ABDEPackAdapter: IPackResolver {
    private val frame = ByteArray(FRAME_BUFFER_SIZE)

    override fun resolver(queue: IPackResolver.ReceivedBufferQueue): IPack {
        return when (queue.take().toInt() and 0xFF) {
            ABDEPack.PREFIX_1 -> {
                onNotify(queue)
            }
            else -> {
                EmptyPack()
            }
        }
    }

    abstract fun onNotify(buffer: ByteArray): IPack

    private fun onNotify(queue: IPackResolver.ReceivedBufferQueue): IPack {
        val prefix2 = queue.take().toInt() and 0xFF
        if (prefix2 != ABDEPack.PREFIX_2) {
            return EmptyPack()
        }

        // 协议头
        frame[0] = ABDEPack.PREFIX_1.toByte()
        frame[1] = ABDEPack.PREFIX_2.toByte()
        // 长度
        frame[2] = queue.take()
        val len = frame[2].toInt()

        if (len < 1 || len > 14) {
            debug("len error: $len")
            return EmptyPack()
        }
        debug("len = $len")

        for (i in 0 until len) {
            frame[3 + i] = queue.take()
        }

        return onNotify(BufferConverter.copy(frame, 0, 3 + len))
    }

    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }

    companion object {
        private const val FRAME_BUFFER_SIZE = 1024
    }
}