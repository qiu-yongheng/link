package com.omni.support.ble.session

import com.omni.support.ble.core.IPackResolver
import com.omni.support.ble.profile.IProfileManager


/**
 * @author 邱永恒
 *
 * @time 2019/8/23 14:39
 *
 * @desc session 建造者基类
 *
 */
abstract class BaseBuilder<T> {
    var mac: String? = null
    var deviceKey: String? = null
    var deviceType: String? = null
    var updateKey: String? = null
    var aesKey: ByteArray? = null
    var keyOrg: ByteArray? = null
    var packAdapter: IPackResolver? = null
    var profileManager: IProfileManager? = null

    fun address(address: String) = apply {
        this.mac = address
    }

    fun deviceKey(key: String) = apply {
        this.deviceKey = key
    }

    fun deviceType(type: String) = apply {
        this.deviceType = type
    }

    fun updateKey(key: String) = apply {
        this.updateKey = key
    }

    fun aesKey(key: ByteArray) = apply {
        this.aesKey = key
    }

    fun keyOrg(key: ByteArray) = apply {
        this.keyOrg = key
    }

    fun packAdapter(adapter: IPackResolver) = apply {
        this.packAdapter = adapter
    }

    fun profileManager(profile: IProfileManager) = apply {
        this.profileManager = profile
    }

    abstract fun build(): T
}