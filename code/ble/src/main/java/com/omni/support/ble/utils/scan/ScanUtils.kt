package com.omni.support.ble.utils.scan

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

/**
 * @author 邱永恒
 *
 * @time 2019/11/20 13:57
 *
 * @desc 蓝牙扫描工具类
 *
 */
object ScanUtils {
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    fun init(app: Application) {
        bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            bluetoothAdapter?.enable()
        }
    }

    fun startLeScan(callback: PeriodScanCallback) {
        enableBluetooth()
        callback.notifyScanStarted()
        bluetoothAdapter?.startLeScan(callback)
    }

    fun stopLeScan(callback: PeriodScanCallback) {
        callback.notifyScanCancel()
        bluetoothAdapter?.stopLeScan(callback)
    }
}