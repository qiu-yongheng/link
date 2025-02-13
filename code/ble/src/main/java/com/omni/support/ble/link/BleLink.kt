package com.omni.support.ble.link

import android.bluetooth.BluetoothDevice
import android.text.TextUtils
import com.omni.support.ble.core.IPack
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.profile.IProfileManager
import com.omni.support.ble.profile.SimpleBleCallbacks
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.utils.HexString
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import java.io.IOException

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 18:04
 *
 * @desc 基础的蓝牙通讯链路
 *
 */
abstract class BleLink(build: BaseBuilder<*>) : Link() {
    private var profile: IProfileManager
    private val mac: String? = build.mac
    private var opened = false

    /** 阻塞队列  */
    protected val receivedDataQueue = SimpleBlockingQueue(QUEUE_CAPACITY)
    /** 解析线程 */
    private var processThread: Thread? = null
    /** 数据接收 */
    protected val receiver = DataReceivedCallback { _, data -> readRaw(data.value) }
    /** 蓝牙操作回调 */
    protected val profileCallback = object : SimpleBleCallbacks() {
        override fun onDeviceConnecting(device: BluetoothDevice) {
            debug("onDeviceConnecting")
            callback?.onDeviceConnecting(device)
        }

        override fun onDeviceConnected(device: BluetoothDevice) {
            debug("onDeviceConnected")
            callback?.onDeviceConnected(device)
        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {
            debug("onDeviceDisconnecting")
            callback?.onDeviceDisconnecting(device)
        }

        override fun onDeviceDisconnected(device: BluetoothDevice) {
            debug("onDeviceDisconnected")
            callback?.onDeviceDisconnected(device)
            opened = false
            close()
        }

        override fun onDeviceReady(device: BluetoothDevice) {
            debug("onDeviceReady")
            callback?.onDeviceReady(device)
            opened = true

            // 启动解析线程
            processThread = Thread(ProcessRunnable())
            processThread?.start()
        }

        override fun onDeviceNotSupported(device: BluetoothDevice) {
            debug("onDeviceNotSupported")
            callback?.onDeviceNotSupported(device)
            opened = false
        }

        override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
            debug("onError")
            callback?.onError(device, message, errorCode)
            opened = false
        }

        override fun onDeviceNoFound() {
            debug("onDeviceNoFound")
            callback?.onDeviceNoFound()
            opened = false
        }
    }

    companion object {
        private const val QUEUE_CAPACITY = 4096
    }

    init {
        setDataPackAdapter(build.packAdapter)

        profile = build.profileManager ?: throw BleException(
            BleException.ERR_DATA_NULL,
            "没有设置ProfileManager"
        )
        profile.setReceiver(receiver)
        profile.setProfileCallback(profileCallback)
    }

    override fun open() {
        if (TextUtils.isEmpty(mac)) {
            throw BleException(BleException.ERR_DATA_NULL, "mac == null")
        }
        profile.connect(mac!!)
    }

    override fun close() {
        // 断开设备连接
        profile.disConnect()

        // 释放资源
        receivedDataQueue.clear()

        // 关闭协议解析线程
        processThread?.interrupt()
        try {
            processThread?.join(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun isOpen(): Boolean {
        opened = profile.isConnect()
        return opened
    }

    override fun sendTo(pack: IPack) {
        if (!isOpen()) throw IOException("Link is closed")
        val sendBuffer = pack.getBuffer()
        try {
            if (LinkGlobalSetting.DEBUG) {
                debug("send " + sendBuffer.size + " bytes: " + HexString.valueOf(sendBuffer))
            }
            writeRaw(sendBuffer)
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    /**
     * 发送数据
     */
    private fun writeRaw(buffer: ByteArray) {
        profile.send(buffer)
    }

    /**
     * 接收数据, 添加到队列中等待解析
     */
    open fun readRaw(buffer: ByteArray?) {
        if (buffer == null || buffer.isEmpty()) {
            return
        }

        customRead(buffer, receivedDataQueue)

        for (byte in buffer) {
            receivedDataQueue.put(byte)
        }

        if (LinkGlobalSetting.DEBUG) {
            debug(
                "Recv " + buffer.size + " bytes, " + HexString.valueOf(
                    buffer,
                    0,
                    buffer.size,
                    " "
                )
            )
        }
    }

    /**
     * 给子类自定义解析返回的数据
     */
    open fun customRead(buffer: ByteArray, queue: SimpleBlockingQueue) {
    }

    /**
     * 解析
     */
    inner class ProcessRunnable : Runnable {
        override fun run() {
            while (isOpen()) {
                val adapter = getPackResolver()
                    ?: throw IllegalArgumentException("No data pack adapter")

                try {
                    synchronized(this) {
                        val pack = adapter.resolver(receivedDataQueue)
                        onReceived(pack)
                    }
                } catch (e: Exception) {
                    if (e is InterruptedException) return
                    onError(e)
                    if (LinkGlobalSetting.DEBUG) {
                        e.printStackTrace()
                    }
                    try {
                        Thread.sleep(10)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}