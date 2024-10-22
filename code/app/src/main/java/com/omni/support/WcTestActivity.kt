package com.omni.support

import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.IResp
import com.omni.support.ble.core.ISessionCall
import com.omni.support.ble.core.NotifyCallback
import com.omni.support.ble.core.SessionCallback
import com.omni.support.ble.protocol.wheelchair.model.WcInfoResult
import com.omni.support.ble.protocol.wheelchair.model.WcLockResult
import com.omni.support.ble.protocol.wheelchair.model.WcOldDataResult
import com.omni.support.ble.protocol.wheelchair.model.WcUpdateIdErrorResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.WheelchairSession
import com.omni.support.ble.task.OnProgressListener
import com.omni.support.ble.task.Progress
import com.omni.support.ble.task.bike.BLReadTask
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_wc.*

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:48
 *
 * @desc
 *
 */
class WcTestActivity : BaseActivity() {
    private lateinit var session: WheelchairSession
    private var id: Long = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_wc
    }

    override fun initListener() {
        session = WheelchairSession.Builder()
            .address("DF:02:D4:15:6B:76")
            .deviceKey("yOTmK50z")
            .deviceType("54")
            .updateKey("Vgz7")
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onConnecting() {
                Toast.makeText(this@WcTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@WcTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@WcTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@WcTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
            }

            override fun onReady() {
                // 关锁监听
                session.call(CommandManager.wcCommand.lock())
                    .subscribe(object : NotifyCallback<WcLockResult> {
                        override fun onSuccess(call: ISessionCall<WcLockResult>, data: IResp<WcLockResult>) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                                id = result.lockId
                            }
                            // 关锁回复
                            session.call(CommandManager.wcCommand.lockReply()).execute()
                        }
                    })
            }
        })

        btn_connect.setOnClickListener {
            session.connect()
        }

        btn_disconnect.setOnClickListener {
            session.disConnect()
        }

        btn_key.setOnClickListener {
            session.initConfig()
        }

        btn_unlock.setOnClickListener {
            session.call(CommandManager.wcCommand.unlock(0, System.currentTimeMillis() / 1000, 0))
                .timeout(3000)
                .enqueue(object : SessionCallback<Boolean> {
                    override fun onSuccess(call: ISessionCall<Boolean>, data: IResp<Boolean>) {
                        val isSuccess = data.getResult() ?: false
                        Toast.makeText(this@WcTestActivity, if (isSuccess) "开锁成功" else "开锁失败", Toast.LENGTH_SHORT)
                            .show()
                        // 开锁回复
                        session.call(CommandManager.wcCommand.unlockReply()).execute()
                    }

                    override fun onFailure(call: ISessionCall<Boolean>, e: Throwable) {
                    }
                })
        }

        btn_info.setOnClickListener {
            session.call(CommandManager.wcCommand.getLockInfo())
                .enqueue(object : SessionCallback<WcInfoResult> {
                    override fun onSuccess(call: ISessionCall<WcInfoResult>, data: IResp<WcInfoResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<WcInfoResult>, e: Throwable) {

                    }
                })
        }

        btn_get_old_data.setOnClickListener {
            session.call(CommandManager.wcCommand.getOldData())
                .enqueue(object : SessionCallback<WcOldDataResult> {
                    override fun onSuccess(call: ISessionCall<WcOldDataResult>, data: IResp<WcOldDataResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<WcOldDataResult>, e: Throwable) {
                    }
                })
        }

        btn_clean_old_data.setOnClickListener {
            session.call(CommandManager.wcCommand.cleanOldData())
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

        btn_update_id_error.setOnClickListener {
            session.call(CommandManager.wcCommand.updateIdError(id))
                .enqueue(object : SessionCallback<WcUpdateIdErrorResult> {
                    override fun onSuccess(
                        call: ISessionCall<WcUpdateIdErrorResult>,
                        data: IResp<WcUpdateIdErrorResult>
                    ) {

                    }

                    override fun onFailure(call: ISessionCall<WcUpdateIdErrorResult>, e: Throwable) {
                    }
                })
        }

        btn_fw.setOnClickListener {
            BLReadTask(session, object : OnProgressListener<String> {
                override fun onProgress(progress: Progress) {
                    Log.d("=====", "percent = ${progress.getPercent()}%, speed = ${progress.getSpeed()}b/s")
                }

                override fun onStatusChanged(status: Int, e: Throwable?) {
                    Log.e("=====", "status = $status")
                }

                override fun onComplete(t: String) {
                    Log.d("=====", "complete = $t")
                }
            }).start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        session.disConnect()
    }
}