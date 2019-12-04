package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.fe.FELink
import com.omni.support.ble.profile.sub.BikeProfileManager
import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.WritePack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.bike.BLPack
import com.omni.support.ble.protocol.bike.BikePackAdapter
import com.omni.support.ble.protocol.bike.model.BLKeyResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:48
 *
 * @desc 3in1马蹄锁会话
 *
 */
class Bike3In1Session(build: BaseBuilder<Bike3In1Session>) : BaseSession(
    FELink(build.profileManager(BikeProfileManager()).packAdapter(BikePackAdapter())),
    build
) {

    override fun initConfig() {
        // 获取蓝牙通讯key
        val deviceKey = build.deviceKey
            ?: throw BleException(BleException.ERR_DATA_NULL, "device key == null")
        call(CommandManager.blCommand.getKey(deviceKey))
            .timeout(1000)
            .enqueue(object : SessionCallback<BLKeyResult> {
                override fun onSuccess(call: ISessionCall<BLKeyResult>, data: IResp<BLKeyResult>) {
                    key = data.getResult()?.key ?: 0
                    debug("获取key成功: $key")
                    sessionListener?.onReady()
                }

                override fun onFailure(call: ISessionCall<BLKeyResult>, e: Throwable) {
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
                val pack = BLPack()
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
            is BLPack -> {
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

    class Builder : BaseBuilder<Bike3In1Session>() {
        override fun build(): Bike3In1Session =
            Bike3In1Session(this)
    }
}