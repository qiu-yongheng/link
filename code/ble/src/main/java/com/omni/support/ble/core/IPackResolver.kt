package com.omni.support.ble.core

/**
 * @author 邱永恒
 *
 * @time 2018/12/17  17:32
 *
 * @desc 数据包解析器
 *
 */

interface IPackResolver {
    @Throws(InterruptedException::class)
    fun resolver(queue: ReceivedBufferQueue): IPack

    interface ReceivedBufferQueue {
        /**
         * 清除阻塞队列
         */
        fun clear()

        /**
         * 添加到队列
         */
        fun put(b: Byte)

        /**
         * 获取并移除头元素, 阻塞
         */
        @Throws(InterruptedException::class)
        fun take(): Byte

        /**
         * 获取不移除
         */
        fun elementAt(index: Int): Byte

        /**
         * 添加到头
         */
        fun addFirst(b: Byte)

        /**
         * 队列长度
         */
        fun size(): Int
    }
}