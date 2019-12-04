package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.link.abde.ABDELink
import com.omni.support.ble.profile.sub.BloomProfileManager
import com.omni.support.ble.profile.sub.PersonalLockProfileManager
import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.a3a4.A3A4Pack
import com.omni.support.ble.protocol.base.abde.ABDEPack
import com.omni.support.ble.protocol.base.abde.ABDEPackAdapter
import com.omni.support.ble.protocol.bloom.BloomPack
import com.omni.support.ble.protocol.bloom.BloomPackAdapter
import com.omni.support.ble.protocol.bloom.model.BloomResult
import com.omni.support.ble.protocol.bsj.model.BSJBatteryStatusResult
import com.omni.support.ble.protocol.scooter.ScooterPack
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession
import com.omni.support.ble.utils.AppExecutors

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:48
 *
 * @desc 个人锁会话
 *
 */
class PersonalLockSession(build: BaseBuilder<PersonalLockSession>) : BaseSession(
    ABDELink(build.profileManager(PersonalLockProfileManager()).packAdapter(BloomPackAdapter())),
    build
) {

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            if (isConnect()) {
                handler.postDelayed(this, 5 * 1000)
                call(CommandManager.bloomCommand.heartbeat()).execute()
            }
        }
    }

    override fun initConfig() {
        val response = getResponse(ABDEPack.RESULT_CMD)
        response.setResultCall(object : IResponse.ResponseCall {
            override fun onResult(buffer: ByteArray) {
                val verificationStatus = buffer[1].toInt() and 0xFF
                when {
                    verificationStatus == 0x08 ->
                        // 本地有key, 校验
                        call(CommandManager.bloomCommand.checkKey(byteArrayOf(0x1, 0x2, 0x3, 0x4))).execute()
                    verificationStatus and 0x08 == 0 ->
                        // 本地没有key, 设置
                        call(CommandManager.bloomCommand.setKey(byteArrayOf(0x1, 0x2, 0x3, 0x4))).execute()
                    verificationStatus and 0x03 != 0 -> {
                        // key校验通过, 发送心跳包
                        response.setResultCall(null)
                        AppExecutors.MAIN_THREAD.execute{sessionListener?.onReady()}
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed(heartbeatRunnable, 1000)
                    }
                }
            }
        })
    }

    override fun send(command: ICommand<*>): IResponse {
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response = getResponse(cmd)
        response.reset()

        when (command) {
            is Command -> {
                val pack = BloomPack()
                pack.cmd = command.getCmd()
                pack.payload = command.getData()
                link.sendTo(pack)
            }
        }

        return response
    }

    override fun onReceived(pack: IPack) {
        when (pack) {
            is BloomPack -> {
                val response = getResponse(pack.cmd)
                response.setResult(pack.payload)
            }
        }
    }

    class Builder : BaseBuilder<PersonalLockSession>() {
        override fun build(): PersonalLockSession =
            PersonalLockSession(this)
    }
}