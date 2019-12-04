package com.omni.support.utils

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.ToastUtils

/**
 * @author 邱永恒
 *
 * @time 2019/9/4 11:28
 *
 * @desc 常用工具
 *
 */
object Kit {
    private var app: Application? = null
    private const val TAG = "Kit"

    fun init(app: Application) {
        this.app = app
    }

    fun getApp(): Application {
        return app ?: throw NullPointerException("Kit must preDeploy!")
    }

    fun showToast(message: String) {
        Log.d(TAG, message)
        ToastUtils.showShort(message)
    }

    fun showToast(stringResId: Int) {
        showToast(getApp().getString(stringResId))
    }

    fun showSuccessToast(message: String) {
        Log.d(TAG, message)
        ToastUtils.showShort(message)
    }

    fun showSuccessToast(stringResId: Int) {
        showSuccessToast(getApp().getString(stringResId))
    }

    fun showErrorToast(message: String) {
        Log.e(TAG, message)
        ToastUtils.showShort(message)
    }

    fun showErrorToast(stringResId: Int) {
        showErrorToast(getApp().getString(stringResId))
    }

}