package com.omni.support.ble.session

import android.bluetooth.BluetoothDevice

/**
 * @author 邱永恒
 *
 * @time 2019/8/12 10:13
 *
 * @desc
 *
 */
interface ISessionListener {
    fun onConnecting()
    fun onConnected()
    fun onDisconnected()
    fun onDeviceNoSupport()
    fun onDeviceNoFound()
    fun onError(device: BluetoothDevice, message: String, errorCode: Int)

    fun onReady()
}