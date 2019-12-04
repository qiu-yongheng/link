package com.omni.support.ble.protocol.keybox.model

import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.BufferConverter

/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:51
 *
 * @desc
 *
 */
class KeyboxModifyDeviceKeyResult : BufferDeserializable {
    var isDeleteSuccess: Boolean = false
    var isModifySuccess: Boolean = false

    override fun setBuffer(buffer: ByteArray) {
        if (buffer.size != 1) {
            return
        }

        val bc = BufferConverter(buffer)
        val status = bc.u8
        isDeleteSuccess = status == 2
        isModifySuccess = status == 1
    }

    override fun toString(): String {
        return "KeyboxModifyDeviceKeyResult(isDeleteSuccess=$isDeleteSuccess, isModifySuccess=$isModifySuccess)"
    }
}