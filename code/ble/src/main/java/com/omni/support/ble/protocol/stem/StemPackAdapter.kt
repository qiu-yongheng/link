package com.omni.support.ble.protocol.stem

import com.omni.support.ble.core.IPack
import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.protocol.base.EmptyPack

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 15:17
 *
 * @desc
 *
 */
class StemPackAdapter : IPackResolver {

    override fun resolver(queue: IPackResolver.ReceivedBufferQueue): IPack {
        return when (val prefix = queue.take().toInt() and 0xFF) {
            StemPack.PREFIX_CONFIG,
            StemPack.PREFIX_CONTROL,
            StemPack.PREFIX_CYCLING,
            StemPack.PREFIX_SENSOR,
            StemPack.PREFIX_COUNT,
            StemPack.PREFIX_SERIAL
            -> {
                onProcess(prefix, queue)
            }
            else -> {
                EmptyPack()
            }
        }
    }

    private fun onProcess(prefix: Int, queue: IPackResolver.ReceivedBufferQueue): IPack {
        val size = queue.take().toInt() and 0xFF
        if (size > 20) {
            return EmptyPack()
        }

        val buffer = ByteArray(size)
        for (i in 0 until size) {
            buffer[i] = queue.take()
        }

        return StemPack.from(prefix, buffer)
    }

}