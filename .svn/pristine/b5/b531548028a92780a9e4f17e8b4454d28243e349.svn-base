package com.omni.support.ble.task.scooter

import com.omni.support.ble.core.ISession
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.task.BaseTask
import com.omni.support.ble.task.ITask
import com.omni.support.ble.task.OnProgressListener
import com.omni.support.ble.task.Progress
import com.omni.support.ble.utils.*
import java.io.File

/**
 * @author 邱永恒
 *
 * @time 2019/8/14 18:11
 *
 * @desc
 *
 */
class ScooterUpgradeTask(
    private val session: ISession,
    private val file: File,
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
                if (!file.exists())
                    throw BleException(BleException.ERR_FILE_NO_EXIST, "文件不存在")

                val pack = DataUtils.unPack(file)
                val payload = pack.pack
                totalPack = pack.totalPack
                finishPack = 0
                val crc = pack.crc
                log("处理数据: ${file.absoluteFile}, crc: ${pack.crc}, totalPack: ${pack.totalPack}")

                // 初始化进度
                progress.reset()
                progress.totalPack = totalPack
                progress.startTime = System.currentTimeMillis()

                // 开始写
                val startPosition = startUpgrade(totalPack, crc)
                finishPack = startPosition
                upgrade(payload)
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
     * 发送开始升级指令
     */
    private fun startUpgrade(totalPack: Int, crc: Int): Int {
        log("start upgrade")
        val result =
            session.call(CommandManager.scooterCommand.startWrite(0x00, totalPack, crc, deviceType, updateKey))
                .execute().getResult() ?: throw BleException(BleException.ERR_DATA_NULL, "启动升级失败")
        log("start upgrade: ${result.pack}")
        return result.pack
    }

    /**
     * 升级
     */
    private fun upgrade(payload: Array<ByteArray>) {
        try {
            log("start upgrade pack")
            while (finishPack < totalPack) {
                checkCancelled()
                val data = payload[finishPack]
                log("start upgrade pack = $finishPack, data = ${HexString.valueOf(data)}")

                var isSuccess = false
                var nextPack = 0
                retry(5) { retry ->
                    checkCancelled()
                    if (retry > 0) logWarning("retry $retry, upgrade $finishPack")

                    if (finishPack == totalPack - 1) {
                        session
                            .call(CommandManager.scooterCommand.endWrite(finishPack, data))
                            .retry(0)
                            .timeout(5000)
                            .execute()
                        nextPack = totalPack
                        isSuccess = true
                    } else {
                        val result = session
                            .call(CommandManager.scooterCommand.write(finishPack, data))
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
                    log("send pack ${finishPack - 1} success, progress: ${progress.getPercent()}%, $finishPack / $totalPack, speed=${progress.getSpeed()}B/s, time=${progress.getTimeUsed()}")
                } else {
                    throw BleException(BleException.ERR_TIMEOUT, "发送pack = ${finishPack}超时")
                }
            }

            val completed = finishPack == totalPack
            if (!completed) {
                logError("end upgrade: 发送pack不完整")
                throw BleException(BleException.ERR_INCOMPLETE, "发送不完整: total = $totalPack, finish = $finishPack")
            }
        } finally {
            log("end upgrade pack")
            // 不需要退出写模式
        }
    }
}