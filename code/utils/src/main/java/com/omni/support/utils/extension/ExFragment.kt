package com.omni.support.utils.extension

import android.text.TextUtils
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * @author 邱永恒
 *
 * @time 2018/6/21  10:05
 *
 * @desc fragment管理
 *
 */


fun FragmentManager.addFragment(@IdRes flId: Int, fragment: Fragment, tag: String? = null) {
    val fragmentTag = if (TextUtils.isEmpty(tag)) fragment.javaClass.simpleName else tag
    val fragmentByTag = findFragmentByTag(fragmentTag)
    if (fragmentByTag == null) {
        beginTransaction().add(flId, fragment, fragmentTag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
    }
}

fun FragmentManager.addFragmentToBackStack(@IdRes flId: Int, fragment: Fragment, tag: String? = null) {
    val fragmentTag = if (TextUtils.isEmpty(tag)) fragment::class.java.simpleName else tag
    val fragmentByTag = findFragmentByTag(fragmentTag)
    if (fragmentByTag == null) {
        beginTransaction().add(flId, fragment, fragmentTag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragmentTag).commit()
    }
}

fun Fragment.popToChild(clazz: Class<*>, includeTargetFragment: Boolean) {
    val flag = if (includeTargetFragment) 1 else 0
    childFragmentManager.popBackStackImmediate(clazz.simpleName, flag)
}

fun <T : Fragment> Fragment.findChildFragment(clazz: Class<T>): T? {
    return childFragmentManager.findFragmentByTag(clazz.simpleName) as T?
}

fun <T : Fragment> Fragment.findFragment(clazz: Class<T>): T? {
    return fragmentManager?.findFragmentByTag(clazz.simpleName) as T?
}

fun Fragment.loadRootFragment(@IdRes containerId: Int, toFragment: Fragment, addToBackStack: Boolean = false, allowAnim: Boolean = false) {
    val transaction = childFragmentManager.beginTransaction().replace(containerId, toFragment, toFragment.javaClass.simpleName)
    if (addToBackStack) {
        transaction.addToBackStack(toFragment.javaClass.simpleName)
    }
    if (allowAnim) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    }
    transaction.commitAllowingStateLoss()
}

fun FragmentActivity.loadRootFragment(@IdRes containerId: Int, toFragment: Fragment, addToBackStack: Boolean = false, allowAnim: Boolean = false) {
    val transaction = supportFragmentManager.beginTransaction().replace(containerId, toFragment, toFragment.javaClass.simpleName)
    if (addToBackStack) {
        transaction.addToBackStack(toFragment.javaClass.simpleName)
    }
    if (allowAnim) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    }
    transaction.commitAllowingStateLoss()
}

@Synchronized
fun Fragment.start(@IdRes containerId: Int, toFragment: Fragment) {
    val fragmentManager = fragmentManager ?: return
    val tag = toFragment.javaClass.simpleName
//    val fragmentByTag = fragmentManager.findFragmentByTag(tag)
//    if (fragmentByTag != null) {
//        KitUtil.showToast("打开失败")
//        return
//    }

    fragmentManager.beginTransaction()
            .add(containerId, toFragment, tag)
            .addToBackStack(tag)
            .commit()
}

@Synchronized
fun Fragment.startAndCheck(@IdRes containerId: Int, toFragment: Fragment) {
    val fragmentManager = fragmentManager ?: return
    val tag = toFragment.javaClass.simpleName

    val fragmentByTag = fragmentManager.findFragmentByTag(tag)
    if (fragmentByTag != null) {
//        KitUtil.showToast("打开失败")
        return
    }

    fragmentManager.beginTransaction()
            .add(containerId, toFragment, tag)
            .addToBackStack(tag)
            .commit()
}

@Synchronized
fun Fragment.pop() {

    val fragmentManager = fragmentManager ?: return
    fragmentManager.beginTransaction()
            .remove(this)
            .commit()

    fragmentManager.popBackStackImmediate()
}