package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.bsj.BSJLink
import com.omni.support.ble.profile.sub.BSJProfileManager
import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.bsj.BSJPack
import com.omni.support.ble.protocol.bsj.BSJPackAdapter
import com.omni.support.ble.protocol.bsj.BSJReplyPack
import com.omni.support.ble.protocol.bsj.BSJResultPack
import com.omni.support.ble.protocol.bsj.model.BSJBatteryStatusResult
import com.omni.support.ble.protocol.bsj.model.BSJTokenResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/9/5 18:26
 *
 * @desc
 *
 */
class BSJSession(build: BaseBuilder<BSJSession>) :
    BaseSession(
        BSJLink(build.profileManager(BSJProfileManager()).packAdapter(BSJPackAdapter())),
        build
    ) {

    private var token: Long = 0
    private var aesKey: ByteArray = build.aesKey ?: throw BleException(BleException.ERR_DATA_NULL, "aes key = null")
    var isNeedCharging: Boolean? = null

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            if (isConnect()) {
                handler.postDelayed(this, 10 * 1000)
                call(CommandManager.bsjCommand.getBatteryStatus())
                    .enqueue(object : SessionCallback<BSJBatteryStatusResult> {
                        override fun onSuccess(
                            call: ISessionCall<BSJBatteryStatusResult>,
                            data: IResp<BSJBatteryStatusResult>
                        ) {
                            isNeedCharging = data.getResult()?.isNeedCharging
                            debug("是否需要充电: $isNeedCharging")
                        }

                        override fun onFailure(
                            call: ISessionCall<BSJBatteryStatusResult>,
                            e: Throwable
                        ) {
                        }
                    })
            }
        }
    }

    override fun initConfig() {
        call(CommandManager.bsjCommand.getToken())
            .timeout(2000)
            .enqueue(object : SessionCallback<BSJTokenResult> {
                override fun onSuccess(
                    call: ISessionCall<BSJTokenResult>,
                    data: IResp<BSJTokenResult>
                ) {
                    token = data.getResult()?.token ?: 0
                    debug("获取token成功: $token")
                    sessionListener?.onReady()
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(heartbeatRunnable, 1000)
                }

                override fun onFailure(call: ISessionCall<BSJTokenResult>, e: Throwable) {

                }
            })
    }

    override fun send(command: ICommand<*>): IResponse {
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response = getResponse(cmd)
        response.reset()

        when (command) {
            is Command -> {
                val pack = BSJPack()
                pack.token = token
                pack.aesKey = aesKey
                pack.command = command.getCmd()
                pack.payload = command.getData()
                link.sendTo(pack)
            }
        }

        return response
    }

    override fun onReceived(pack: IPack) {
        when (pack) {
            is BSJReplyPack -> {
                val response = getResponse(pack.command)
                response.setResult(pack.payload)
            }
            is BSJResultPack -> {
                val response = getResponse(pack.command)
                response.setResult(pack.payload)
            }
        }
    }

    class Builder : BaseBuilder<BSJSession>() {
        override fun build(): BSJSession =
            BSJSession(this)
    }
}