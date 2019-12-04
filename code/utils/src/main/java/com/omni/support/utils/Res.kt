package com.omni.support.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.lang.ref.WeakReference

/**
 * @author 邱永恒
 *
 * @time 2019/8/5 9:10
 *
 * @desc 资源工具
 *
 */
object Res {
    private lateinit var resources: Resources
    private lateinit var context: WeakReference<Context>

    fun init(context: Context) {
        resources = context.applicationContext.resources
        Res.context = WeakReference(context)
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        return resources.getString(resId, *formatArgs)
    }

    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    fun getColor(@ColorRes colorResId: Int): Int {
        return ContextCompat.getColor(context.get()!!, colorResId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, resId, null)
    }
}