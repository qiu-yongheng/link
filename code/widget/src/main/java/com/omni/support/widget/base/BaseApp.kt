package com.omni.support.widget.base

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author 邱永恒
 *
 * @time 2019/11/8 11:45
 *
 * @desc
 *
 */
class BaseApp: Application(), ViewModelStoreOwner {
    private lateinit var appViewModelStore: ViewModelStore
    private var factory: ViewModelProvider.Factory? = null

    override fun onCreate() {
        super.onCreate()
        appViewModelStore = ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    /**
     * 获取application生命周期的ViewModelProvider
     */
    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            activity.applicationContext as BaseApp,
            (activity.applicationContext as BaseApp).getAppFactory(activity)
        )
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return factory!!
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException("Your activity/fragment is not yet attached to " + "Application. You can't request ViewModel before onCreate call.")
    }

}