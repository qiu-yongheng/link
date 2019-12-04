package com.omni.support.ble.link.bsj

import com.omni.support.ble.link.BleLink
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.utils.AESUtils
import com.omni.support.ble.utils.HexString

/**
 * @author 邱永恒
 *
 * @time 2019/9/5 18:22
 *
 * @desc 博实结
 *
 */
class BSJLink(val build: BaseBuilder<*>) : BleLink(build) {
    private var readThread: Thread? = null
    @Volatile
    private var isRunning: Boolean = false

    private val readRunnable = Runnable {
        while (isRunning) {
            try {
                build.profileManager?.read()
                Thread.sleep(500)
            } catch (e: Exception) {
            }
        }
    }

    override fun open() {
        super.open()
        // 启动循环读线程
        isRunning = true
        readThread = Thread(readRunnable)
        readThread?.start()
    }

    override fun close() {
        super.close()

        readThread?.interrupt()
        try {
            readThread?.join(200)
        } catch (e: Exception) {
        }
    }

    override fun readRaw(buffer: ByteArray?) {
        if (buffer == null || buffer.size != 16) {
            return
        }

        val aesKey = build.aesKey ?: return

        // 解密
        val decrypt = AESUtils.decrypt(buffer, aesKey)

        for (byte in decrypt) {
            receivedDataQueue.put(byte)
        }

        if (LinkGlobalSetting.DEBUG) {
            debug(
                "Recv " + decrypt.size + " bytes, " + HexString.valueOf(
                    decrypt,
                    0,
                    decrypt.size,
                    " "
                )
            )
        }
    }
}