package com.omni.support.ble.protocol.bsj

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/9/6 9:38
 *
 * @desc
 *
 */
class BSJPackAdapter : IPackResolver {
    private val frame = ByteArray(16)
    override fun resolver(queue: IPackResolver.ReceivedBufferQueue): IPack {
        return when (val prefix = queue.take().toInt() and 0xFF) {
            BSJPack.PREFIX -> {
                onReply(queue)
            }
            else -> {
                onResult(prefix, queue)
            }
        }
    }

    /**
     * 回应
     */
    private fun onReply(queue: IPackResolver.ReceivedBufferQueue): IPack {
        // 协议头
        frame[0] = BSJPack.PREFIX.toByte()
        // command
        frame[1] = queue.take()
        frame[2] = queue.take()
        // len
        frame[3] = queue.take()
        // ack
        frame[4] = queue.take()
        val len = frame[3].toInt() and 0xFF
        if (len != 1) {
            debug("reply len error: $len, frame: ${HexString.valueOf(frame)}")
            return EmptyPack()
        }
        debug("len = $len, command = ${(frame[1].toInt() and 0xFF) or (frame[2].toInt() and 0xFF)}")

        // random
        for (i in 0 until 11) {
            frame[5 + i] = queue.take()
        }

        return BSJReplyPack.from(frame)
    }

    /**
     * 结果
     */
    private fun onResult(prefix: Int, queue: IPackResolver.ReceivedBufferQueue): IPack {
        // command
        frame[0] = prefix.toByte()
        frame[1] = queue.take()
        // len
        frame[2] = queue.take()
        val len = frame[2].toInt() and 0xFF
        if (len < 0 || len > 13) {
            debug("result len error: $len, frame: ${HexString.valueOf(frame)}")
            return EmptyPack()
        }
        debug("len = $len, command = ${(frame[0].toInt() and 0xFF) or (frame[1].toInt() and 0xFF)}")

        // payload
        for (i in 0 until 13) {
            frame[3 + i] = queue.take()
        }

        return BSJResultPack.from(frame)
    }

    fun debug(message: String) {
        if (LinkGlobalSetting.DEBUG) {
            Log.d(this.javaClass.simpleName, message)
        }
    }
}