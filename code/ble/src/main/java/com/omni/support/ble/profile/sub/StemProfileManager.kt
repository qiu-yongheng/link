package com.omni.support.ble.profile.sub

import android.bluetooth.*
import android.content.Context
import android.util.Log
import com.omni.support.ble.BleModuleHelper
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.profile.IProfileManager
import com.omni.support.ble.profile.OmniBleManagerCallbacks
import com.omni.support.ble.protocol.stem.StemPack
import com.omni.support.ble.utils.HexString
import com.omni.support.ble.utils.permission.PermissionConstants
import com.omni.support.ble.utils.permission.PermissionUtils
import com.omni.support.ble.utils.scan.MacScanCallback
import com.omni.support.ble.utils.scan.ScanUtils
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/11/11 11:26
 *
 * @desc 智能把立
 *
 */
class StemProfileManager : BleManager<OmniBleManagerCallbacks>(BleModuleHelper.getApp()),
    IProfileManager {

    private lateinit var _receiver: DataReceivedCallback

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var supported: Boolean = false
    private var config: BluetoothGattCharacteristic? = null
    private var upgrade: BluetoothGattCharacteristic? = null
    private var control: BluetoothGattCharacteristic? = null
    private var cycling: BluetoothGattCharacteristic? = null
    private var sensor: BluetoothGattCharacteristic? = null
    private var count: BluetoothGattCharacteristic? = null
    private var serial: BluetoothGattCharacteristic? = null

    companion object {
        private val UUID_SERVICE = UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb")
        // 读写配置(read, write) 时间，车轮直径，防盗锁功能开关，坡角补偿
        private val UUID_W_R_CONFIG = UUID.fromString("0000FFF1-0000-1000-8000-00805f9b34fb")
        // 升级(write)
        private val UUID_W_UPGRADE = UUID.fromString("0000FFFE-0000-1000-8000-00805f9b34fb")
        // 控制(write) 锁车开关，报警声关控制，灯关控制。
        private val UUID_W_CONTROL = UUID.fromString("0000FFF2-0000-1000-8000-00805f9b34fb")
        // 骑行状态(notify) 骑行状态，最近一次骑行的起始时间，连续骑行秒数，实时速度，最大速度，最低速度，平均速度，骑行路程
        private val UUID_N_CYCLING = UUID.fromString("0000FFF3-0000-1000-8000-00805f9b34fb")
        // 传感器状态(notify) 坡度，温度，高度，气压，电量百分比，充电状态
        private val UUID_N_SENSOR = UUID.fromString("0000FFF4-0000-1000-8000-00805f9b34fb")
        // 踏频计数状态(notify)
        private val UUID_N_COUNT = UUID.fromString("0000FFF6-0000-1000-8000-00805f9b34fb")
        // 机器序列号(read, write)
        private val UUID_W_R_SERIAL = UUID.fromString("0000FFF5-0000-1000-8000-00805f9b34fb")
    }


    init {
        bluetoothManager =
            BleModuleHelper.getApp().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
    }

    private val gattCallback = object : BleManagerGattCallback() {
        override fun initialize() {
            initNotify()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service = gatt.getService(UUID_SERVICE)
            if (service != null) {
                config = service.getCharacteristic(UUID_W_R_CONFIG)
                upgrade = service.getCharacteristic(UUID_W_UPGRADE)
                control = service.getCharacteristic(UUID_W_CONTROL)
                cycling = service.getCharacteristic(UUID_N_CYCLING)
                sensor = service.getCharacteristic(UUID_N_SENSOR)
                count = service.getCharacteristic(UUID_N_COUNT)
                serial = service.getCharacteristic(UUID_W_R_SERIAL)
            }

            supported =
                config != null && upgrade != null && control != null && cycling != null && sensor != null && count != null && serial != null
            return supported
        }

        override fun onDeviceDisconnected() {
            config = null
            upgrade = null
            control = null
            cycling = null
            sensor = null
            count = null
            serial = null
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return gattCallback
    }

    override fun shouldClearCacheWhenDisconnected(): Boolean {
        return !supported
    }

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

    override fun setReceiver(receiver: DataReceivedCallback) {
        this._receiver = receiver
    }

    override fun setProfileCallback(callback: OmniBleManagerCallbacks) {
        setGattCallbacks(callback)
    }


    /**
     * 初始化订阅
     * 必须要sleep, 不然会报133错误
     */
    fun initNotify() {
        setNotificationCallback(cycling).with(cyclingCallback)
        enableNotifications(cycling).enqueue()

        Thread.sleep(200)
        setNotificationCallback(sensor).with(externalCallback)
        enableNotifications(sensor).enqueue()

        Thread.sleep(200)
        setNotificationCallback(count).with(minuteCallback)
        enableNotifications(count).enqueue()
    }

    /**
     * 写数据
     * 根据data发送到不同的UUID
     */
    override fun send(data: ByteArray) {
        val prefix = data[0].toInt() and 0xFF
        val buffer = ByteArray(data.size - 1)
        System.arraycopy(data, 1, buffer, 0, buffer.size)
        debug("send uuid: ${HexString.valueOf(prefix)}, value: ${HexString.valueOf(buffer)}")

        when (prefix) {
            StemPack.PREFIX_CONFIG -> {
                writeCharacteristic(config, buffer).enqueue()
                Thread.sleep(500)
                readCharacteristic(config).with(configReadCallback).enqueue()
            }
            StemPack.PREFIX_CONTROL -> {
                writeCharacteristic(control, buffer).enqueue()
            }
            StemPack.PREFIX_SERIAL -> {

            }
        }
    }

    /**
     * 读数据
     */
    override fun read() {
    }

    private fun processResult(prefix: Int, data: ByteArray): ByteArray {
        val payload = ByteArray(data.size + 2)
        payload[0] = prefix.toByte()
        payload[1] = data.size.toByte()
        System.arraycopy(data, 0, payload, 2, data.size)
        return payload
    }

    private val configReadCallback = DataReceivedCallback { device, data ->
        val value = data.value ?: return@DataReceivedCallback
        _receiver.onDataReceived(device, Data(processResult(StemPack.PREFIX_CONFIG, value)))
    }

    private val serialReadCallback = DataReceivedCallback { device, data ->
        val value = data.value ?: return@DataReceivedCallback
        _receiver.onDataReceived(device, Data(processResult(StemPack.PREFIX_SERIAL, value)))
    }

    private val cyclingCallback = DataReceivedCallback { device, data ->
        val value = data.value ?: return@DataReceivedCallback
        _receiver.onDataReceived(device, Data(processResult(StemPack.PREFIX_CYCLING, value)))
    }

    private val externalCallback = DataReceivedCallback { device, data ->
        val value = data.value ?: return@DataReceivedCallback
        _receiver.onDataReceived(device, Data(processResult(StemPack.PREFIX_SENSOR, value)))
    }

    private val minuteCallback = DataReceivedCallback { device, data ->
        val value = data.value ?: return@DataReceivedCallback
        _receiver.onDataReceived(device, Data(processResult(StemPack.PREFIX_COUNT, value)))
    }

    private fun debug(msg: String) {
        Log.d(this.javaClass.simpleName, msg)
    }
}