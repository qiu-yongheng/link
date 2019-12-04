package com.omni.support.ble.profile

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.omni.support.ble.BleModuleHelper
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.utils.permission.PermissionConstants
import com.omni.support.ble.utils.permission.PermissionUtils
import com.omni.support.ble.utils.scan.MacScanCallback
import com.omni.support.ble.utils.scan.ScanUtils
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 22:15
 *
 * @desc 马蹄锁蓝牙协议管理
 *
 */
abstract class ProfileManager : BleManager<OmniBleManagerCallbacks>(BleModuleHelper.getApp()),
    IProfileManager {
    private lateinit var _receiver: DataReceivedCallback

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var supported: Boolean = false
    private var notify: BluetoothGattCharacteristic? = null
    private var write: BluetoothGattCharacteristic? = null

    init {
        bluetoothManager =
            BleModuleHelper.getApp().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
    }

    override fun setReceiver(receiver: DataReceivedCallback) {
        this._receiver = receiver
    }

    override fun setProfileCallback(callback: OmniBleManagerCallbacks) {
        setGattCallbacks(callback)
    }

    private val gattCallback = object : BleManagerGattCallback() {
        override fun initialize() {
            initNotify()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service = gatt.getService(getService())
            if (service != null) {
                notify = service.getCharacteristic(getNotify())
                write = service.getCharacteristic(getWrite())
            }

            supported = notify != null && write != null
            return supported
        }

        override fun onDeviceDisconnected() {
            notify = null
            write = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return gattCallback
    }

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return !supported
    }

//    override fun log(priority: Int, message: String) {
//        super.log(priority, message)
//        Log.println(priority, this.javaClass.simpleName, message)
//    }

    override fun connect(mac: String) {
        if (bluetoothAdapter?.isEnabled == false) {
            throw BleException(BleException.ERR_BLE_UN_ENABLE, "蓝牙未打开")
        }
        val device =
            bluetoothAdapter?.getRemoteDevice(mac)
                ?: throw BleException(BleException.ERR_DEVICE_NOT_FOUND, "设备未找到")

        if (isConnecting) return
        isConnecting = true
        retryCount = 1

        connectOrScanDevice(device)
    }

    private var isConnecting: Boolean = false
    private var retryCount: Int = 0

    /**
     * 连接设备
     * 如果是第一次连接, 可能会连接不上, 扫描设备后再连接
     */
    private fun connectOrScanDevice(device: BluetoothDevice) {
        connect(device)
            .timeout(5000)
            .useAutoConnect(false)
            .done {
                isConnecting = false
            }
            .fail { bluetoothDevice, i ->
                debug("connect fail: $i")
                PermissionUtils.permission(PermissionConstants.LOCATION)
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            debug("权限已授权")
                            ScanUtils.startLeScan(object :
                                MacScanCallback(bluetoothDevice.address, 5000) {
                                override fun onDeviceFound(device: BluetoothDevice) {
                                    debug("搜索到设备")
                                    if (retryCount-- > 0) {
                                        connectOrScanDevice(device)
                                    } else {
                                        mCallbacks.onDeviceDisconnected(device)
                                        isConnecting = false
                                    }
                                }

                                override fun onDeviceNotFound() {
                                    debug("没有搜索到设备")
                                    mCallbacks.onDeviceNoFound()
                                    isConnecting = false
                                }
                            })
                        }

                        override fun onDenied() {
                            debug("权限未授权")
                            isConnecting = false
                        }
                    })
                    .request()
            }
            .invalid {
                isConnecting = false
            }
            .enqueue()
    }

    override fun disConnect() {
        if (isConnect()) {
            disconnect().enqueue()
        }
    }

    override fun isConnect(): Boolean {
        return isConnected
    }

    /**
     * 初始化订阅
     */
    open fun initNotify() {
        setNotificationCallback(notify).with(_receiver)
        enableNotifications(notify).enqueue()
    }

    /**
     * 写数据
     */
    override fun send(data: ByteArray) {
        writeCharacteristic(write, data).enqueue()
    }

    /**
     * 读数据, 在link中开个线程, 循环读
     */
    override fun read() {
        readCharacteristic(notify).with(readCallback).enqueue()
    }

    private var lastByteArray: ByteArray? = null
    private val readCallback = DataReceivedCallback { device, data ->
        val bytes = data.value
        if (lastByteArray == null || bytes == null || !lastByteArray!!.contentEquals(bytes)) {
            lastByteArray = bytes
            _receiver.onDataReceived(device, data)
        }
    }

    private fun debug(msg: String) {
        Log.d(this.javaClass.simpleName, msg)
    }

    abstract fun getService(): UUID
    abstract fun getNotify(): UUID
    abstract fun getWrite(): UUID
}