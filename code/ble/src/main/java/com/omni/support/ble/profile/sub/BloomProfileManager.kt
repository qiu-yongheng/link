package com.omni.support.ble.profile.sub

import com.omni.support.ble.profile.ProfileManager
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 16:28
 *
 * @desc
 *
 */
class BloomProfileManager : ProfileManager() {
    override fun getService(): UUID = UUID.fromString("6e400001-e6ac-a7e7-b1b3-e699bae80062")

    override fun getNotify(): UUID = UUID.fromString("6e400003-e6ac-a7e7-b1b3-e699bae80062")

    override fun getWrite(): UUID = UUID.fromString("6e400002-e6ac-a7e7-b1b3-e699bae80062")
}