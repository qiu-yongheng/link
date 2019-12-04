package com.omni.support.ble.session.sub

import com.omni.support.ble.core.*
import com.omni.support.ble.exception.BleException
import com.omni.support.ble.link.a3a4.A3A4Link
import com.omni.support.ble.link.stem.StemLink
import com.omni.support.ble.profile.sub.BoxProfileManager
import com.omni.support.ble.profile.sub.StemProfileManager
import com.omni.support.ble.protocol.*
import com.omni.support.ble.protocol.base.LogPack
import com.omni.support.ble.protocol.base.ReadPack
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.base.WritePack
import com.omni.support.ble.protocol.base.model.KeyResult
import com.omni.support.ble.protocol.box.BoxPack
import com.omni.support.ble.protocol.box.BoxPackAdapter
import com.omni.support.ble.protocol.scooter.ScooterPack
import com.omni.support.ble.protocol.stem.StemPack
import com.omni.support.ble.protocol.stem.StemPackAdapter
import com.omni.support.ble.protocol.stem.model.StemConfigModel
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.BaseBuilder
import com.omni.support.ble.session.BaseSession

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 17:48
 *
 * @desc 智能把立会话
 *
 */
class StemSession(build: BaseBuilder<StemSession>) : BaseSession(
    StemLink(build.packAdapter(StemPackAdapter()).profileManager(StemProfileManager())),
    build
) {

    override fun initConfig() {
        sessionListener?.onReady()
    }

    override fun send(command: ICommand<*>): IResponse {
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response = getResponse(cmd)
        response.reset()

        when (command) {
            is Command -> {
                val pack = StemPack()
                pack.cmd = command.getCmd()
                pack.payload = command.getData()
                link.sendTo(pack)
            }
        }

        return response
    }

    override fun onReceived(pack: IPack) {
        when (pack) {
            is StemPack -> {
                val response = getResponse(pack.cmd)
                response.setResult(pack.payload)
            }
        }
    }

    class Builder : BaseBuilder<StemSession>() {
        override fun build(): StemSession =
            StemSession(this)
    }
}