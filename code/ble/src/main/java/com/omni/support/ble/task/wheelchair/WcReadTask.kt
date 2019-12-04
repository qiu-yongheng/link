package com.omni.support.ble.task.wheelchair

import com.omni.support.ble.core.ISession
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.task.BaseTask
import com.omni.support.ble.task.ITask
import com.omni.support.ble.task.OnProgressListener
import com.omni.support.ble.task.Progress
import com.omni.support.ble.utils.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/14 18:11
 *
 * @desc 轮椅锁获取固件信息
 *
 */
class WcReadTask(private val session: ISession, listener: OnProgressListener<String>) : BaseTask<String>(listener) {
    private val progress = Progress()
    private var cancelled = false

    private var totalPack: Int = 0
    private var finishPack: Int = 0

    override fun start() {
        AppExecutors.WORK_THREAD.execute {
            try {
                log("start get fw info total pack")
                val packInfo = session.call(CommandManager.wcCommand.startRead()).execute()
                val result = packInfo.getResult()
                if (result == null) {
                    AppExecutors.MAIN_THREAD.execute {
                        listener.onStatusChanged(ITask.Status.FAIL, null)
                    }
                    return@execute
                }
                log("end get fw info total pack: $result")

                totalPack = result.totalPack
                finishPack = 0
                val crc = result.crc
                val deviceType = result.deviceType

                progress.reset()
                progress.totalPack = totalPack
                progress.startTime = System.currentTimeMillis()

                val success: Boolean

                checkCancelled()
                val data = transfer(deviceType, crc)
                success = data.isNotEmpty()

                AppExecutors.MAIN_THREAD.execute {
                    if (success) {
                        listener.onComplete(String(data))
                    } else {
                        listener.onStatusChanged(ITask.Status.FAIL, null)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                AppExecutors.MAIN_THREAD.execute {
                    if (e is BleException) {
                        listener.onStatusChanged(ITask.Status.FAIL, e)
                    } else {
                        listener.onStatusChanged(ITask.Status.ERROR, e)
                    }
                }
            }
        }
    }

    override fun stop() {
        cancelled = true
    }

    /**
     * 检查用户取消
     */
    private fun checkCancelled() {
        if (cancelled)
            throw BleException(BleException.ERR_CANCEL, "取消执行")
    }

    /**
     * 传输文件
     */
    private fun transfer(deviceType: Int, crc: Int): ByteArray {
        log("start get fw info")
        val buffer = BufferBuilder()
        while (finishPack < totalPack) {
            checkCancelled()

            log("get pack: $finishPack")
            var isSuccess = false
            retry(5) { retry ->
                checkCancelled()
                if (retry > 0) logWarning("retry $retry,  get fw info pack $finishPack")

                val result = session
                    .call(CommandManager.wcCommand.read(finishPack, deviceType))
                    .retry(0)
                    .timeout(1000)
                    .execute()
                    .getResult()
                result?.data?.apply { buffer.putBytes(this) }

                isSuccess = result != null && result.pack == finishPack
                return@retry isSuccess
            }

            if (isSuccess) {
                finishPack++
                progress.finishPack = finishPack
                progress.sampleSpeed(1)
                AppExecutors.MAIN_THREAD.execute {
                    listener.onProgress(progress)
                }
                log("get pack ${finishPack - 1} success, progress: ${progress.getPercent()}%, $finishPack / $totalPack, speed=${progress.getSpeed()}B/s, time=${progress.getTimeUsed()}")
            } else {
                throw BleException(BleException.ERR_TIMEOUT, "获取pack = ${finishPack}超时")
            }
        }

        val completed = finishPack == totalPack
        if (!completed) {
            logError("end get fw info: 下载不完整")
            throw BleException(BleException.ERR_INCOMPLETE, "下载不完整: total = $totalPack, finish = $finishPack")
        }

        val payload = buffer.buffer()
        val payloadCRC = CRC.crc16(payload)
        if (payloadCRC != crc) {
            logError("end get fw info: crc校验失败")
            throw BleException(BleException.ERR_CRC_FAIL, "crc校验失败: 目标 = $crc, 当前 = $payloadCRC")
        }

        // 剔除最后一包的无用数据
        var len = payload.size
        for (i in payload.indices.reversed()) {
            val item = payload[i]
            if (item != 0x00.toByte()) {
                len = i + 1
                break
            }
        }
        val data = BufferConverter.copy(payload, 0, len)
        log("end get fw info: ${String(data)}")
        return data
    }
}