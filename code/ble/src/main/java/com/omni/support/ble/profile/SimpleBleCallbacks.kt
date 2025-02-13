package com.omni.support.ble.profile

import android.bluetooth.BluetoothDevice

/**
 * @author 邱永恒
 *
 * @time 2019/8/9 11:34
 *
 * @desc
 *
 */
open class SimpleBleCallbacks: OmniBleManagerCallbacks {
    override fun onDeviceDisconnecting(device: BluetoothDevice) {
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
    }

    override fun onBondingFailed(device: BluetoothDevice) {
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
    }

    override fun onBondingRequired(device: BluetoothDevice) {
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
    }

    override fun onBonded(device: BluetoothDevice) {
    }

    override fun onDeviceReady(device: BluetoothDevice) {
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
    }

    override fun onDeviceNoFound() {
    }
}