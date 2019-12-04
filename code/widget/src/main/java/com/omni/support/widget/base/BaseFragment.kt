package com.omni.support.widget.base

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders


/**
 * @author 邱永恒
 *
 * @time 2018/7/13  17:26
 *
 * @desc
 *
 */

abstract class BaseFragment : Fragment() {
    protected val handler: Handler by lazy { Handler() }
    protected val requestDelayed: Long = 250
    private val initDataRunnable = { initData() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)
        val layoutId = getLayoutId()
        if (layoutId > 0) {
            view = inflater.inflate(getLayoutId(), container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 拦截触摸事件, 防止fragment叠加点击穿透
        view.setOnTouchListener { _, _ ->
            true
        }

        initView(savedInstanceState)
        initListener()
        initSubscribe()

        handler.postDelayed(initDataRunnable, requestDelayed)
    }

    open fun getLayoutId(): Int = 1
    open fun initView(savedInstanceState: Bundle?) {}
    open fun initListener() {}
    open fun initSubscribe() {}
    open fun initData() {}

    /**
     * 实例化ViewModel子类
     */
    inline fun <reified T : ViewModel> getViewModel(factory: ViewModelProvider.Factory? = null): T {
        return ViewModelProviders.of(this, factory).get(T::class.java)
    }

    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (activity?.applicationContext as BaseApp).getAppViewModelProvider(activity!!)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            onSupportVisible()
        } else {
            onSupportInvisible()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            onSupportInvisible()
        } else {
            onSupportVisible()
        }
    }

    override fun onResume() {
        super.onResume()
        onSupportVisible()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(initDataRunnable)
        onSupportInvisible()
    }

    open fun onSupportVisible() {
    }

    open fun onSupportInvisible() {
    }

    open fun close() {
        val fragmentManager = fragmentManager ?: return
        fragmentManager.beginTransaction()
            .remove(this)
            .commit()

        fragmentManager.popBackStackImmediate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.setOnTouchListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}


