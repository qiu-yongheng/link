package com.omni.support.ble.link.a3a4

import com.omni.support.ble.link.BleLink
import com.omni.support.ble.link.SimpleBlockingQueue
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.utils.CRC

/**
 * @author 邱永恒
 *
 * @time 2019/9/2 17:42
 *
 * @desc
 *
 */
class A3A4Link(build: BaseBuilder<*>) : BleLink(build) {

    override fun customRead(buffer: ByteArray, queue: SimpleBlockingQueue) {
        if (isReadLog) {
            debug("读取日志模式, 添加头: 0xC7")
            val size = buffer.size
            queue.put(0xC7.toByte())
            queue.put(size.toByte())
        } else if (buffer.size == 20 && CRC.checkFirstCRC16(buffer)) {
            debug("添加头: 0xFB")
            // 如果是传输模式, 手动添加cmd, 方便解析
            queue.put(0xFB.toByte())
        }
    }
}