package com.omni.support.utils

import org.greenrobot.eventbus.EventBus

/**
 * @author 邱永恒
 *
 * @time 2018/8/10  12:10
 *
 * @desc EventBus管理
 *
 */

object Bus {
    /**
     * 注册
     */
    fun register(subscriber: Any) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber)
        }
    }

    /**
     * 注销
     */
    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    /**
     * 发送消息
     * 如果有类订阅了这个事件, 发送
     */
    fun post(obj: Any) {
        if (EventBus.getDefault().hasSubscriberForEvent(obj.javaClass)) {
            EventBus.getDefault().post(obj)
        }
    }

    /**
     * 发送粘性事件
     */
    fun postSticky(obj: Any) {
        EventBus.getDefault().postSticky(obj)
    }

    fun removeStickyEvent(obj: Any) {
        EventBus.getDefault().removeStickyEvent(obj)
    }

    fun removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents()
    }
}