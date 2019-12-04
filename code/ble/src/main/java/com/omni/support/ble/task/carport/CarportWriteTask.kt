package com.omni.support.ble.task.carport

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
 * @desc
 *
 */
class CarportWriteTask(
    private val session: ISession,
    private val data: String,
    private val deviceType: Int,
    private val updateKey: String,
    listener: OnProgressListener<Boolean>
) : BaseTask<Boolean>(listener) {
    private val progress = Progress()
    private var cancelled = false

    private var totalPack: Int = 0
    private var finishPack: Int = 0

    override fun start() {
        AppExecutors.WORK_THREAD.execute {
            try {
                val pack = DataUtils.unPack(data)
                val payload = pack.pack
                totalPack = pack.totalPack
                finishPack = 0
                val crc = pack.crc
                log("处理数据: $data, crc: ${pack.crc}, totalPack: ${pack.totalPack}")

                // 初始化进度
                progress.reset()
                progress.totalPack = totalPack
                progress.startTime = System.currentTimeMillis()

                // 开始写
                val startPosition = startWrite(totalPack, crc)
                finishPack = startPosition
                write(payload)
                AppExecutors.MAIN_THREAD.execute {

                    listener.onComplete(true)
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
     * 发送开始写指令
     */
    private fun startWrite(totalPack: Int, crc: Int): Int {
        log("start write")
        val result =
            session.call(CommandManager.carportCommand.startWrite(0x01, totalPack, crc, deviceType, updateKey))
                .execute().getResult() ?: throw BleException(BleException.ERR_DATA_NULL, "启动下载失败")
        log("start write: ${result.pack}")
        return result.pack
    }

    /**
     * 写数据
     */
    private fun write(payload: Array<ByteArray>) {
        try {
            log("start write pack")
            while (finishPack < totalPack) {
                checkCancelled()
                val data = payload[finishPack]
                log("start write pack = $finishPack, data = ${HexString.valueOf(data)}")

                var isSuccess = false
                var nextPack = 0
                retry(5) { retry ->
                    checkCancelled()
                    if (retry > 0) logWarning("retry $retry, write $finishPack")

                    if (finishPack == totalPack - 1) {
                        session
                            .call(CommandManager.carportCommand.endWrite(finishPack, data))
                            .retry(0)
                            .timeout(5000)
                            .execute()
                        nextPack = totalPack
                        isSuccess = true
                    } else {
                        val result = session
                            .call(CommandManager.carportCommand.write(finishPack, data))
                            .retry(0)
                            .timeout(5000)
                            .execute()
                            .getResult()
                        result?.apply { nextPack = pack }
                        isSuccess = result != null
                    }
                    return@retry isSuccess
                }

                if (isSuccess) {
                    finishPack = nextPack
                    progress.finishPack = finishPack
                    progress.sampleSpeed(1)
                    AppExecutors.MAIN_THREAD.execute {

                        listener.onProgress(progress)
                    }
                    log("get pack ${finishPack - 1} success, progress: ${progress.getPercent()}%, $finishPack / $totalPack, speed=${progress.getSpeed()}B/s, time=${progress.getTimeUsed()}")
                } else {
                    throw BleException(BleException.ERR_TIMEOUT, "发送pack = ${finishPack}超时")
                }
            }

            val completed = finishPack == totalPack
            if (!completed) {
                logError("end write: 发送pack不完整")
                throw BleException(BleException.ERR_INCOMPLETE, "发送不完整: total = $totalPack, finish = $finishPack")
            }
        } finally {
            log("end write pack")
        }
    }
}