package com.omni.support

import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.IResp
import com.omni.support.ble.core.ISessionCall
import com.omni.support.ble.core.NotifyCallback
import com.omni.support.ble.core.SessionCallback
import com.omni.support.ble.protocol.hj.model.*
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.HJSession
import com.omni.support.ble.utils.BufferBuilder
import com.omni.support.ble.utils.BufferConverter
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_hj.*

/**
 * @author 邱永恒
 *
 * @time 2019/9/24 12:26
 *
 * @desc
 *
 */
class HJTestActivity : BaseActivity() {
    private lateinit var session: HJSession

    override fun getLayoutId(): Int {
        return R.layout.activity_hj
    }

    override fun initListener() {
        session = HJSession.Builder()
//            .address("F4:A4:52:CA:89:BF")
            .address("DC:CE:50:3F:14:A0")
            .aesKey(
                byteArrayOf(
                    0x31,
                    0x17,
                    0x2C,
                    0x12,
                    0x38,
                    0x1B,
                    0x3A,
                    0x8E.toByte(),
                    0x1F,
                    0x50,
                    0x42,
                    0x28,
                    0x19,
                    0xD3.toByte(),
                    0x1D,
                    0xE6.toByte()
                )
            )
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onConnecting() {
                Toast.makeText(this@HJTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@HJTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@HJTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@HJTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
            }

            override fun onReady() {
                // 关锁监听
            }
        })

        btn_connect.setOnClickListener {
            session.connect()
        }

        btn_disconnect.setOnClickListener {
            session.disConnect()
        }

        btn_token.setOnClickListener {

        }

        btn_battery_status.setOnClickListener {
            session.call(CommandManager.hjCommand.getBattery())
                .enqueue(object : SessionCallback<HJBatteryResult> {
                    override fun onSuccess(
                        call: ISessionCall<HJBatteryResult>,
                        data: IResp<HJBatteryResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<HJBatteryResult>, e: Throwable) {

                    }
                })
        }

        btn_lock_status.setOnClickListener {
            session.call(CommandManager.hjCommand.getLockStatus())
                .enqueue(object : SessionCallback<HJLockStatusResult> {
                    override fun onSuccess(
                        call: ISessionCall<HJLockStatusResult>,
                        data: IResp<HJLockStatusResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<HJLockStatusResult>, e: Throwable) {
                    }
                })
        }

        btn_unlock.setOnClickListener {
            session.call(CommandManager.hjCommand.unlock("000000"))
                .timeout(3000)
                .enqueue(object : SessionCallback<HJUlockResult> {
                    override fun onSuccess(
                        call: ISessionCall<HJUlockResult>,
                        data: IResp<HJUlockResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<HJUlockResult>, e: Throwable) {
                    }
                })
        }

        btn_lock.setOnClickListener {
            session.call(CommandManager.hjCommand.lock())
                .enqueue(object : SessionCallback<HJLockResult> {
                    override fun onSuccess(
                        call: ISessionCall<HJLockResult>,
                        data: IResp<HJLockResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<HJLockResult>, e: Throwable) {
                    }
                })
        }

        btn_get_work_mode.setOnClickListener {
            session.call(CommandManager.hjCommand.getWorkMode())
                .enqueue(object : SessionCallback<HJWorkModeResult> {
                    override fun onSuccess(
                        call: ISessionCall<HJWorkModeResult>,
                        data: IResp<HJWorkModeResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<HJWorkModeResult>, e: Throwable) {
                    }
                })
        }

        btn_set_work_mode.setOnClickListener {
            session.call(CommandManager.hjCommand.setWorkMode(0)).execute()
        }

        btn_modify_name.setOnClickListener {
            val converter = BufferConverter(18)
            converter.putString("hjlock")
            val buffer = converter.buffer()
            val data = BufferConverter(buffer)
            session.call(
                CommandManager.hjCommand.modifyName(data.getBytes(9)),
                CommandManager.hjCommand.modifyName(data.getBytes(9))
            ).retry(0)
                .enqueue(object : SessionCallback<Boolean> {
                    override fun onSuccess(call: ISessionCall<Boolean>, data: IResp<Boolean>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<Boolean>, e: Throwable) {

                    }
                })
        }
    }
}