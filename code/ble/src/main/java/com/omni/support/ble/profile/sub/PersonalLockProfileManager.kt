package com.omni.support.ble.profile.sub

import com.omni.support.ble.profile.ProfileManager
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 16:28
 *
 * @desc 个人蓝牙锁
 *
 */
class PersonalLockProfileManager : ProfileManager() {

    override fun getService(): UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")

    override fun getNotify(): UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

    override fun getWrite(): UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
}