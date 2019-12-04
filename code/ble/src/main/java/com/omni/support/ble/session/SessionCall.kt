package com.omni.support.ble.session

import android.util.Log
import com.omni.support.ble.core.*
import com.omni.support.ble.exception.CommandException
import com.omni.support.ble.exception.CommandTimeoutException
import com.omni.support.ble.link.LinkGlobalSetting
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.utils.AppExecutors
import java.io.IOException

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 13:39
 *
 * @desc
 *
 */
class SessionCall<T>(private val command: ICommand<T>) : ISessionCall<T> {

    companion object {
        const val TAG = "SessionCall"
        const val DEFAULT_RETRY = 3
        const val DEFAULT_TIMEOUT = 1000L
        const val DEFAULT_ASYN_TIMEOUT = 10_000L
    }

    private var session: ISession? = null
    private var retryCount: Int = 0
    private var timeout: Long = 0
    private var dispatcher: IDispatcher? = null
    @Volatile
    private var isRunning = false
    private var asynTimeout: Long = 0

    init {
        retryCount = DEFAULT_RETRY
        timeout = DEFAULT_TIMEOUT
        asynTimeout = DEFAULT_ASYN_TIMEOUT
    }

    override fun session(session: ISession): ISessionCall<T> {
        this.session = session
        return this
    }

    override fun retry(retry: Int): ISessionCall<T> {
        this.retryCount = if (retry < 0) 0 else retry
        return this
    }

    override fun timeout(timeout: Long): ISessionCall<T> {
        this.timeout = timeout
        return this
    }

    override fun asyncTimeout(timeout: Long): ISessionCall<T> {
        this.asynTimeout = timeout
        return this
    }

    override fun subscribe(callback: NotifyCallback<T>) {
        val session = session ?: throw IllegalArgumentException("no session")
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response: IResponse = session.getResponse(cmd)
        response.setResultCall(object : IResponse.ResponseCall {
            override fun onResult(buffer: ByteArray) {
                AppExecutors.MAIN_THREAD.execute {
                    // TODO: 不能再onSuccess()中执行耗时操作, 不然会阻塞主线程
                    // TODO: 比如说, 在onSuccess中同步执行指令, 当有指令在排队执行时, 就会阻塞主线程
                    callback.onSuccess(this@SessionCall, Resp(command.onResult(buffer)))
                }
//                response.reset()
            }
        })
    }

    /**
     * 接收多个响应数据
     * 如果判断接收完毕, 可以主动调用cancel()方法结束, 不然其他指令都会阻塞, 直到当前call执行完毕
     */
    override fun asyncCall(callback: AsyncCallback<T>) {
        if (dispatcher == null) throw NullPointerException("Dispatcher == null")
        dispatcher?.enqueue {
            val session = session
            if (session == null) {
                AppExecutors.MAIN_THREAD.execute {
                    callback.onStarted(false)
                }
                throw IllegalArgumentException("no session")
            }
            AppExecutors.MAIN_THREAD.execute {
                callback.onStarted(true)
            }

            if (command is LogCommand) {
                session.isReadLog(true)
            }

            var isReceiver = false

            val response: IResponse = session.send(command)
            response.setResultCall(object : IResponse.ResponseCall {
                override fun onResult(buffer: ByteArray) {
                    isReceiver = true
                    AppExecutors.MAIN_THREAD.execute {
                        callback.onReceiving(this@SessionCall, Resp(command.onResult(buffer)))
                    }
                }
            })

            isRunning = true
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < asynTimeout && isRunning) {
                Thread.sleep(timeout)
            }

            if (command is LogCommand) {
                session.isReadLog(false)
            }

            isRunning = false
            response.setResultCall(null)
            AppExecutors.MAIN_THREAD.execute {
                if (isReceiver) {
                    callback.onReceived()
                } else {
                    callback.onTimeout()
                }
            }
        }
    }

    override fun execute(): IResp<T> {
        val dispatcher = this.dispatcher ?: throw NullPointerException("Dispatcher == null")

        return dispatcher.execute {
            executeCall()
        }
    }

    override fun enqueue(callback: SessionCallback<T>?) {
        if (dispatcher == null) throw NullPointerException("Dispatcher == null")

        dispatcher?.enqueue {
            try {
                val response = executeCall()

                AppExecutors.MAIN_THREAD.execute {
                    callback?.onSuccess(this, response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AppExecutors.MAIN_THREAD.execute {
                    callback?.onFailure(this, e)
                }
            }
        }
    }

    private fun executeCall(): IResp<T> {
        debugLog("===== START SESSION CALL ($command) =====")
        val session = session ?: throw IllegalArgumentException("no session")
        val RETRY = retryCount
        var retryRequest = retryCount
        try {
            do {
                // 发送间隔10毫秒
                Thread.sleep(10)

                /** 重试回调 */
                if (RETRY - retryRequest > 0) {
                    Thread.sleep(50)
                    debugLog("** RETRY ${RETRY - retryRequest} **")
                }

                /** send: 发送时, response获取信号量 */
                val response: IResponse = session.send(command)

                /** 不等待, 直接返回 */
                if (command.isNoResponse()) return Resp(null)

                /** response尝试在指定时间内获取信号量(按理来说, 现在可用信号量是0, 只有接收到response后释放信号量, 才能获取到, 这里用来判断是否接收到响应) */
                if (response.await(timeout)) {
                    return Resp(command.onResult(response.getResult()))
                } else {
                    debugLog("** TIMEOUT **")
                }
            } while (retryRequest-- > 0)

            throw CommandTimeoutException(command)
        } catch (e: IOException) {
            throw CommandException(command, e)
        } catch (e: InterruptedException) {
            throw CommandException(command, e)
        } finally {
            debugLog("===== END SESSION CALL =====")
            debugLog(" ")
        }
    }

    override fun cancel() {
        isRunning = false

        // 取消订阅
        val session = session ?: throw IllegalArgumentException("no session")
        val cmd = if (command.getResponseCmd() == 0) command.getCmd() else command.getResponseCmd()
        val response: IResponse = session.getResponse(cmd)
        response.setResultCall(null)
    }

    override fun dispatcher(dispatcher: IDispatcher): ISessionCall<T> {
        this.dispatcher = dispatcher
        return this
    }

    private fun debugLog(message: String) {
        if (LinkGlobalSetting.LOG_DATA) {
            Log.d(TAG, message)
        }
    }

    class Resp<RESULT>(private val result: RESULT?) : IResp<RESULT> {
        override fun getResult(): RESULT? {
            return result
        }
    }
}