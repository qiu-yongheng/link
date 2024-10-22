package com.omni.support.ble.session

import android.util.Log
import com.omni.support.ble.core.IResponse
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 11:51
 *
 * @desc 回调
 *
 */
open class Response : IResponse {
    private var call: IResponse.ResponseCall? = null
    private val semaphore: Semaphore = Semaphore(0, false)
    private var result: ByteArray = ByteArray(0)

    var id: Int = 0

    override fun reset() {
        result = ByteArray(0)
        semaphore.drainPermits()
    }

    override fun await(timeoutMillions: Long): Boolean {
        return try {
            semaphore.tryAcquire(timeoutMillions, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            false
        }
    }

    override fun getResult(): ByteArray {
        return result
    }

    override fun setResult(buffer: ByteArray) {
        this.result = buffer
        semaphore.release()
        call?.onResult(buffer)
    }

    override fun setResultCall(call: IResponse.ResponseCall?) {
        this.call = call
    }
}