package com.omni.support.widget.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author 邱永恒
 *
 * @time 2019/8/3 16:14
 *
 * @desc activity基类
 *
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = getLayoutId()
        if (layoutId > 0) {
            setContentView(getLayoutId())
        }

        initView(savedInstanceState)
        initListener()
        initSubscribe()
        initData()
    }

    open fun getLayoutId(): Int = -1

    open fun initView(savedInstanceState: Bundle?) {}

    open fun initListener() {}

    open fun initSubscribe() {}

    open fun initData() {}
}