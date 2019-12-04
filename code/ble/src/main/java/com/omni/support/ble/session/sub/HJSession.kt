package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.bsj.BSJLink
import com.omni.support.ble.profile.sub.HJProfileManager
import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.hj.HJPack
import com.omni.support.ble.protocol.hj.HJPackAdapter
import com.omni.support.ble.protocol.hj.model.HJTokenResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 12:17
 *
 * @desc 洪记
 *
 */
class HJSession(build: BaseBuilder<HJSession>) :
    BaseSession(
        BSJLink(build.profileManager(HJProfileManager()).packAdapter(HJPackAdapter())),
        build
    ) {

    private var token: Long = 0
    private var aesKey: ByteArray = build.aesKey ?: throw BleException(BleException.ERR_DATA_NULL, "aes key = null")

    override fun initConfig() {
        call(CommandManager.hjCommand.getToken())
            .timeout(2000)
            .enqueue(object : SessionCallback<HJTokenResult> {
                override fun onSuccess(
                    call: ISessionCall<HJTokenResult>,
                    data: IResp<HJTokenResult>
                ) {
                    token = data.getResult()?.token ?: 0
                    debug("获取token成功: $token")
                    sessionListener?.onReady()
                }

                override fun onFailure(call: ISessionCall<HJTokenResult>, e: Throwable) {
                }
            })
    }

    override fun send(command: ICommand<*>): IResponse {
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response = getResponse(cmd)
        response.reset()

        when (command) {
            is Command -> {
                val pack = HJPack()
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
            is HJPack -> {
                val response = getResponse(pack.command)
                response.setResult(pack.payload)
            }
        }
    }

    class Builder : BaseBuilder<HJSession>() {
        override fun build(): HJSession =
            HJSession(this)
    }
}