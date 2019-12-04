package com.omni.support.utils

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * @author 邱永恒
 *
 * @time 2019/8/5 9:18
 *
 * @desc Utils模块初始化类
 *
 */
object UtilsModuleHelper {
    fun init(app: Application) {
        Utils.init(app)
        Res.init(app)
        Kit.init(app)
    }
}