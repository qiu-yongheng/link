package com.omni.support.ble.link.stem

import com.omni.support.ble.link.BleLink
import com.omni.support.ble.link.SimpleBlockingQueue
import com.omni.support.ble.session.BaseBuilder

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 11:25
 *
 * @desc 智能把立
 *
 */
class StemLink(build: BaseBuilder<*>) : BleLink(build) {
    override fun customRead(buffer: ByteArray, queue: SimpleBlockingQueue) {

    }
}