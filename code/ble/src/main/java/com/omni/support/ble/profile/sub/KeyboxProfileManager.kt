package com.omni.support.ble.profile.sub

import com.omni.support.ble.profile.ProfileManager
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/28 17:10
 *
 * @desc
 *
 */
class KeyboxProfileManager : ProfileManager() {
    override fun getService(): UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")

    override fun getNotify(): UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")

    override fun getWrite(): UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
}