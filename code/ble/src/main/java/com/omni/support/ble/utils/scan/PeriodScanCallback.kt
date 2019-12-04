package com.omni.support.ble.utils.scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper

/**
 * @author 邱永恒
 *
 * @time 2019/11/20 14:03
 *
 * @desc 有扫描超时的扫描回调
 *
 */
abstract class PeriodScanCallback(private var timeoutMillis: Long = 10000) : BluetoothAdapter.LeScanCallback {
    private val handler = Handler(Looper.getMainLooper())

    /**
     * handler定时停止扫描
     */
    fun notifyScanStarted() {
        if (timeoutMillis > 0) {
            removeHandlerMsg()
            handler.postDelayed({
                removeHandlerMsg()
                ScanUtils.stopLeScan(this)
                onScanTimeout()
            }, timeoutMillis)
        }
    }

    /**
     * 手动停止扫描
     */
    fun notifyScanCancel() {
        removeHandlerMsg()
        onScanCancel()
    }

    /**
     * 移除定时任务(超时)
     */
    private fun removeHandlerMsg() {
        handler.removeCallbacksAndMessages(null)
    }

    fun getTimeoutMillis(): Long {
        return timeoutMillis
    }

    fun setTimeoutMillis(timeoutMillis: Long): PeriodScanCallback {
        this.timeoutMillis = timeoutMillis
        return this
    }

    abstract fun onScanTimeout()

    abstract fun onScanCancel()
}