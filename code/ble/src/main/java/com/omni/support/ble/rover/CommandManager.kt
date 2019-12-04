package com.omni.support.ble.rover

import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.protocol.base.TransmissionCommand
import com.omni.support.ble.protocol.bike.BLCommands
import com.omni.support.ble.protocol.bloom.BloomCommands
import com.omni.support.ble.protocol.box.BoxCommands
import com.omni.support.ble.protocol.bsj.BSJCommands
import com.omni.support.ble.protocol.carport.CarportCommands
import com.omni.support.ble.protocol.hj.HJCommands
import com.omni.support.ble.protocol.keybox.KeyboxCommands
import com.omni.support.ble.protocol.scooter.ScooterCommands
import com.omni.support.ble.protocol.stem.StemCommands
import com.omni.support.ble.protocol.ulock.ULockCommands
import com.omni.support.ble.protocol.wheelchair.WheelchairCommands
import com.omni.support.ble.rover.factory.CommandFactory
import com.omni.support.ble.rover.factory.LogCommandFactory
import com.omni.support.ble.rover.factory.TransmissionCommandFactory

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:10
 *
 * @desc
 *
 */
object CommandManager {
    private val builder: ProtocolRetrofit by lazy {
        ProtocolRetrofit.Builder()
            .addCommandFactory(Command::class.java, CommandFactory())
            .addCommandFactory(TransmissionCommand::class.java, TransmissionCommandFactory())
            .addCommandFactory(LogCommand::class.java, LogCommandFactory())
            .build()
    }

    val blCommand: BLCommands by lazy { builder.create(BLCommands::class.java) }
    val scooterCommand: ScooterCommands by lazy { builder.create(ScooterCommands::class.java) }
    val wcCommand: WheelchairCommands by lazy { builder.create(WheelchairCommands::class.java) }
    val keyboxCommand: KeyboxCommands by lazy { builder.create(KeyboxCommands::class.java) }
    val ulockCommand: ULockCommands by lazy { builder.create(ULockCommands::class.java) }
    val carportCommand: CarportCommands by lazy { builder.create(CarportCommands::class.java) }
    val boxCommand: BoxCommands by lazy { builder.create(BoxCommands::class.java) }
    val bsjCommand: BSJCommands by lazy { builder.create(BSJCommands::class.java) }
    val hjCommand: HJCommands by lazy { builder.create(HJCommands::class.java) }
    val bloomCommand: BloomCommands by lazy { builder.create(BloomCommands::class.java) }
    val stemCommand: StemCommands by lazy { builder.create(StemCommands::class.java) }
}