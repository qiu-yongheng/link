package com.omni.support.ble.profile.sub

import com.omni.support.ble.profile.ProfileManager
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 11:57
 *
 * @desc 洪记蓝牙锁
 *
 */
class HJProfileManager: ProfileManager() {
    override fun getService(): UUID = UUID.fromString("60000001-0000-1000-8000-00805F9B34FB")

    override fun getNotify(): UUID = UUID.fromString("60000002-0000-1000-8000-00805F9B34FB")

    override fun getWrite(): UUID = UUID.fromString("60000003-0000-1000-8000-00805F9B34FB")
}