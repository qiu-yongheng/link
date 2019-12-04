package com.omni.support.ble.profile

import no.nordicsemi.android.ble.BleManagerCallbacks

/**
 * @author 邱永恒
 *
 * @time 2019/8/9 11:28
 *
 * @desc
 *
 */
interface OmniBleManagerCallbacks: BleManagerCallbacks {
    fun onDeviceNoFound()
}