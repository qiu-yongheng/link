package com.omni.support

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.IResp
import com.omni.support.ble.core.ISessionCall
import com.omni.support.ble.core.NotifyCallback
import com.omni.support.ble.core.SessionCallback
import com.omni.support.ble.protocol.stem.model.*
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.StemSession
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_stem.*

/**
 * @author 邱永恒
 *
 * @time 2019/11/12 13:55
 *
 * @desc
 *
 */
class StemTestActivity : BaseActivity() {
    private lateinit var session: StemSession

    override fun getLayoutId(): Int {
        return R.layout.activity_stem
    }

    override fun initView(savedInstanceState: Bundle?) {
        session = StemSession.Builder()
            .address("50:65:83:8C:AA:C7")
//            .address("88:C2:55:D3:34:5E")
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onConnecting() {
                Toast.makeText(this@StemTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@StemTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@StemTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@StemTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
            }

            override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
                Log.d("=====", "message: $message, error: $errorCode")
                Toast.makeText(this@StemTestActivity, "连接失败", Toast.LENGTH_SHORT).show()
            }

            override fun onReady() {
                session.call(
                    CommandManager.stemCommand.setConfig(
                        StemConfigModel(223, false, 1)
                    )
                ).enqueue(object : SessionCallback<StemConfigModel> {
                    override fun onSuccess(
                        call: ISessionCall<StemConfigModel>,
                        data: IResp<StemConfigModel>
                    ) {
                        Log.d("=====", "初始化配置成功: ${data.getResult().toString()}")
                        // TODO: 保存
                    }

                    override fun onFailure(call: ISessionCall<StemConfigModel>, e: Throwable) {
                        e.printStackTrace()
                    }
                })

                session.call(CommandManager.stemCommand.recvCycling())
                    .subscribe(object : NotifyCallback<StemCyclingResult> {
                        override fun onSuccess(
                            call: ISessionCall<StemCyclingResult>,
                            data: IResp<StemCyclingResult>
                        ) {
                            Log.d("=====", data.getResult().toString())
                        }
                    })

                session.call(CommandManager.stemCommand.recvSensor())
                    .subscribe(object : NotifyCallback<StemSensorResult> {
                        override fun onSuccess(
                            call: ISessionCall<StemSensorResult>,
                            data: IResp<StemSensorResult>
                        ) {
                            Log.d("=====", data.getResult().toString())
                        }
                    })

                session.call(CommandManager.stemCommand.recvCount())
                    .subscribe(object : NotifyCallback<StemCountResult> {
                        override fun onSuccess(
                            call: ISessionCall<StemCountResult>,
                            data: IResp<StemCountResult>
                        ) {
                            Log.d("=====", data.getResult().toString())
                        }
                    })
            }
        })
    }


    override fun initListener() {
        btn_connect.setOnClickListener {
            session.connect()
        }

        btn_disconnect.setOnClickListener {
            session.disConnect()
        }

        btn_unlock.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                lockState = StemControlparam.UNLOCK
            })).execute()
        }

        btn_lock.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                lockState = StemControlparam.LOCK
            })).execute()
        }

        btn_find.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                alarmState = StemControlparam.ALARM_FIND
            })).execute()
        }

        btn_light_on.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                lightState = StemControlparam.LIGHT_HALF_BRIGHT
            })).execute()
        }

        btn_light_off.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                lightState = StemControlparam.LIGHT_CLOSE
            })).execute()
        }

        btn_v.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                horizontalCalibration = StemControlparam.HORIZONTAL_CALIBRATION_OPEN
            })).execute()
        }

        btn_shutdown.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                shutdown = StemControlparam.SHUTDOWN
            })).execute()
        }

        btn_reset.setOnClickListener {
            session.call(CommandManager.stemCommand.sendControl(StemControlparam().apply {
                reset = StemControlparam.RESET
            })).execute()
        }

    }
}