package com.omni.support.ble.utils.scan

import android.bluetooth.BluetoothDevice
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author 邱永恒
 *
 * @time 2019/11/20 14:12
 *
 * @desc
 *
 */
abstract class MacScanCallback(private val filterMac: String, private val timeout: Long) : PeriodScanCallback(timeout){
    private val hasFound = AtomicBoolean(false)

    override fun onScanTimeout() {
        onDeviceNotFound()
    }

    override fun onScanCancel() {
    }

    override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
        if (device == null) return
        val mac = device.address ?: return

        if (!hasFound.get() && mac.equals(filterMac, true)) {
            hasFound.set(true)
            ScanUtils.stopLeScan(this)
            onDeviceFound(device)
        }
    }

    abstract fun onDeviceFound(device: BluetoothDevice)
    abstract fun onDeviceNotFound()
}