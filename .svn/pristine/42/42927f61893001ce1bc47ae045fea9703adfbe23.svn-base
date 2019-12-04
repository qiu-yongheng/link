package com.omni.support.ble.protocol.base.a3a4

import android.util.Log
import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.EmptyPack
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.ble.utils.CRC

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 10:23
 *
 * @desc
 *
 */
abstract class A3A4PackAdapter : IPackResolver {
    private val frame = ByteArray(FRAME_BUFFER_SIZE)

    override fun resolver(queue: IPackResolver.ReceivedBufferQueue): IPack {
        return when (queue.take().toInt() and 0xFF) {
            A3A4Pack.PREFIX_1 -> {
                onNotify(queue)
            }
            ReadPack.TRANSMISSION -> {
                onTransmission(queue)
            }
            LogPack.LOG -> {
                onLog(queue)
            }
            else -> {
                EmptyPack()
            }
        }
    }

    abstract fun onNotify(buffer: ByteArray): IPack

    private fun onNotify(queue: IPackResolver.ReceivedBufferQueue): IPack {
        val prefix2 = queue.take().toInt() and 0xFF
        if (prefix2 != A3A4Pack.PREFIX_2) {
            return EmptyPack()
        }

        // 协议头
        frame[0] = A3A4Pack.PREFIX_1.toByte()
        frame[1] = A3A4Pack.PREFIX_2.toByte()
        // 长度
        frame[2] = queue.take()
        val len = (frame[2].toInt() and 0xFF) + 1
        // 随机码
        frame[3] = queue.take()
        // key
        frame[4] = queue.take()
        // cmd
        frame[5] = queue.take()

        val randNum = (frame[3] - 0x32) and 0xFF
        if (len < 1 || len > 14) {
            debug("len error: $len")
            return EmptyPack()
        }
        debug("len = $len, randNum = $randNum")

        for (i in 0 until len) {
            frame[6 + i] = queue.take()
        }

        return onNotify(BufferConverter.copy(frame, 0, 6 + len))
    }

    /**
     * 传输协议
     */
    private fun onTransmission(queue: IPackResolver.ReceivedBufferQueue): IPack {
        val buffer = ByteArray(20)
        for (i in 0 until 20) {
            buffer[i] = queue.take()
        }

        return if (CRC.checkFirstCRC16(buffer)) {
            debug("校验成功")
            ReadPack.from(ReadPack.TRANSMISSION, BufferConverter.copy(buffer, 2, 18))
        } else {
            debug("校验失败")
            EmptyPack()
        }
    }

    /**
     * 日志
     */
    private fun onLog(queue: IPackResolver.ReceivedBufferQueue): IPack {
        val size = queue.take().toInt() and 0xFF
        if (size > 20) {
            return EmptyPack()
        }

        val buffer = ByteArray(size)
        for (i in 0 until size) {
            buffer[i] = queue.take()
        }

        return LogPack.from(0xC7, buffer)
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