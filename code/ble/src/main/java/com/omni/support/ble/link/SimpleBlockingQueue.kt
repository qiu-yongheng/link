package com.omni.support.ble.link

import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.utils.HexString
import java.util.concurrent.LinkedBlockingDeque

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  18:00
 *
 * @desc
 *
 */

class SimpleBlockingQueue(capacity: Int) : IPackResolver.ReceivedBufferQueue {

    private val queue: LinkedBlockingDeque<Byte> = LinkedBlockingDeque(capacity)

    override fun take(): Byte {
        return queue.take()
    }

    override fun clear() {
        queue.clear()
    }

    override fun put(b: Byte) {
        queue.put(b)
    }

    override fun elementAt(index: Int): Byte {
        return queue.elementAt(index)
    }

    override fun addFirst(b: Byte) {
        queue.addFirst(b)
    }

    override fun size(): Int {
        return queue.size
    }

    override fun toString(): String {
        return try {
            "(size=${queue.size}, queue=${HexString.valueOf(queue.toByteArray())})"
        } catch (e: Throwable) {
            "(size=${queue.size})"
        }
    }
}