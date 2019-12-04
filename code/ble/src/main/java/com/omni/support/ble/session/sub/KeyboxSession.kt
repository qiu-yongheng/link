package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.a3a4.A3A4Link
import com.omni.support.ble.profile.sub.KeyboxProfileManager
import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.WritePack
import com.omni.support.ble.protocol.keybox.KeyboxPack
import com.omni.support.ble.protocol.keybox.KeyboxPackAdapter
import com.omni.support.ble.protocol.keybox.model.KeyboxKeyResult
import com.omni.support.ble.protocol.scooter.ScooterPack
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:48
 *
 * @desc 钥匙盒锁会话
 *
 */
class KeyboxSession(build: BaseBuilder<KeyboxSession>) : BaseSession(
    A3A4Link(build.profileManager(KeyboxProfileManager()).packAdapter(KeyboxPackAdapter())),
    build
) {

    override fun initConfig() {
        // 获取蓝牙通讯key
        val deviceKey = build.deviceKey
            ?: throw BleException(BleException.ERR_DATA_NULL, "device key == null")
        call(CommandManager.keyboxCommand.getKey(deviceKey))
            .timeout(1000)
            .enqueue(object : SessionCallback<KeyboxKeyResult> {
                override fun onSuccess(
                    call: ISessionCall<KeyboxKeyResult>,
                    data: IResp<KeyboxKeyResult>
                ) {
                    key = data.getResult()?.key ?: 0
                    debug("获取key成功: $key")
                    sessionListener?.onReady()
                }

                override fun onFailure(call: ISessionCall<KeyboxKeyResult>, e: Throwable) {
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
                val pack = ScooterPack()
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
            is KeyboxPack -> {
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

    class Builder : BaseBuilder<KeyboxSession>() {
        override fun build(): KeyboxSession =
            KeyboxSession(this)
    }
}