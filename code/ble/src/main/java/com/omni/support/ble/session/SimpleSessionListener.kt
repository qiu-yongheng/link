package com.omni.support.ble.session

import android.bluetooth.BluetoothDevice

/**
 * @author 邱永恒
 *
 * @time 2019/9/15 14:14
 *
 * @desc
 *
 */
open class SimpleSessionListener: ISessionListener {

    override fun onConnecting() {
    }

    override fun onConnected() {
    }

    override fun onDisconnected() {
    }

    override fun onDeviceNoSupport() {
    }

    override fun onDeviceNoFound() {
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {

    }

    override fun onReady() {
    }
}