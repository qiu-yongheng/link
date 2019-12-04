package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.fe.FELink
import com.omni.support.ble.profile.sub.ULockProfileManager
import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.WritePack
import com.omni.support.ble.protocol.ulock.ULockPack
import com.omni.support.ble.protocol.ulock.ULockPackAdapter
import com.omni.support.ble.protocol.ulock.model.ULockKeyResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:48
 *
 * @desc U型锁会话
 *
 */
class ULockSession(build: BaseBuilder<ULockSession>) : BaseSession(
    FELink(build.packAdapter(ULockPackAdapter()).profileManager(ULockProfileManager())),
    build
) {

    override fun initConfig() {
        // 获取蓝牙通讯key
        val deviceKey = build.deviceKey
            ?: throw BleException(BleException.ERR_DATA_NULL, "device key == null")
        call(CommandManager.ulockCommand.getKey(deviceKey))
            .timeout(1000)
            .enqueue(object : SessionCallback<ULockKeyResult> {
                override fun onSuccess(
                    call: ISessionCall<ULockKeyResult>,
                    data: IResp<ULockKeyResult>
                ) {
                    key = data.getResult()?.key ?: 0
                    debug("获取key成功: $key")
                    sessionListener?.onReady()
                }

                override fun onFailure(call: ISessionCall<ULockKeyResult>, e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    override fun send(command: ICommand<*>): IResponse {
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response = getResponse(cmd)
        response.reset()

        when (command) {
            is TransmissionCommand -> {
                val pack = WritePack()
                pack.payload = command.getData()
                link.sendTo(pack)
            }
            is Command -> {
                val pack = ULockPack()
                pack.key = key
                pack.cmd = command.getCmd()
                pack.payload = command.getData()
                link.sendTo(pack)
            }
        }

        return response
    }

    override fun onReceived(pack: IPack) {
        when (pack) {
            is ULockPack -> {
                val response = getResponse(pack.cmd)
                response.setResult(pack.payload)
            }
            is ReadPack -> {
                // cmd固定为0xFB
                val response = getResponse(pack.cmd)
                response.setResult(pack.payload)
            }
            is LogPack -> {
                // cmd固定为0xC7
                val response = getResponse(pack.cmd)
                response.setResult(pack.payload)
            }
        }
    }

    class Builder : BaseBuilder<ULockSession>() {
        override fun build(): ULockSession =
            ULockSession(this)
    }
}