package com.omni.support.widget.base

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * @author 邱永恒
 *
 * @time 2019/9/4 11:15
 *
 * @desc
 *
 */
open class BaseDialogFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)
        val layoutId = getLayoutId()
        if (layoutId > 0) {
            view = inflater.inflate(getLayoutId(), container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(savedInstanceState)
        initListener()
        initSubscribe()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.repeatCount == 0) {
                dismiss()
            }
            false
        }
        return dialog
    }

    open fun show(fragmentManager: FragmentManager) {
        // 执行未执行的事务, 防止isAdded结果不对
        fragmentManager.executePendingTransactions()
        if (!isAdded) {
            show(fragmentManager, javaClass.simpleName)
        }
    }

    override fun dismiss() {
        if (fragmentManager != null) {
            super.dismiss()
        }
    }

    open fun getLayoutId(): Int = 1
    open fun initView(savedInstanceState: Bundle?) {}
    open fun initListener() {}
    open fun initSubscribe() {}
}