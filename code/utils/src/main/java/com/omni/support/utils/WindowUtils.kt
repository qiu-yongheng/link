package com.omni.support.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * @author 邱永恒
 *
 * @time 2019/9/3 14:15
 *
 * @desc
 *
 */
object WindowUtils {
    /**
     * 隐藏状态栏, 导航栏
     */
    fun setFullScreen(activity: Activity) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    /**
     * 设置状态栏透明
     */
    fun setTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}