package com.omni.support.ble

import android.app.Application
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.utils.scan.ScanUtils

/**
 * @author 邱永恒
 *
 * @time 2019/8/6 10:03
 *
 * @desc
 *
 */
object BleModuleHelper {
    private var app: Application? = null

    fun init(app: Application) {
        this.app = app
        ScanUtils.init(app)
    }

    fun isDebug(isDebug: Boolean) {
        LinkGlobalSetting.DEBUG = isDebug
        LinkGlobalSetting.LOG_DATA = isDebug
    }

    fun getApp(): Application {
        return app ?: throw NullPointerException("请先初始化BleModuleHelper")
    }
}