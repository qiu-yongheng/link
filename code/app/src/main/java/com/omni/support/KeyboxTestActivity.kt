package com.omni.support

import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.*
import com.omni.support.ble.protocol.keybox.model.*
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.KeyboxSession
import com.omni.support.ble.task.OnProgressListener
import com.omni.support.ble.task.Progress
import com.omni.support.ble.task.scooter.ScooterReadTask
import com.omni.support.ble.task.scooter.ScooterUpgradeTask
import com.omni.support.ble.utils.TOTPUtils
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_keybox.*
import java.io.File

/**
 * @author 邱永恒
 *
 * @time 2019/8/29 9:41
 *
 * @desc
 *
 */
class KeyboxTestActivity : BaseActivity() {
    private lateinit var session: KeyboxSession

    override fun getLayoutId(): Int {
        return R.layout.activity_keybox
    }

    override fun initListener() {
        session = KeyboxSession.Builder()
//            .address("D5:13:96:F9:C0:1A")
            .address("CB:99:E6:91:46:51")
//            .address("EE:4F:1F:87:41:46")
//            .address("E6:C4:A0:EE:5B:1E")
            .deviceKey("OmniW4GX")
            .deviceType("34")
            .updateKey("Vgz7")
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onConnecting() {
                Toast.makeText(this@KeyboxTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@KeyboxTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@KeyboxTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@KeyboxTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
            }

            override fun onReady() {
                session.call(CommandManager.keyboxCommand.lock())
                    .subscribe(object : NotifyCallback<KeyboxLockResult> {
                        override fun onSuccess(
                            call: ISessionCall<KeyboxLockResult>,
                            data: IResp<KeyboxLockResult>
                        ) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                            }
                            // 关锁回复
                            session.call(CommandManager.keyboxCommand.lockReply()).execute()
                        }
                    })

                session.call(CommandManager.keyboxCommand.pullOut())
                    .subscribe(object : NotifyCallback<KeyboxKeyStatusResult> {
                        override fun onSuccess(
                            call: ISessionCall<KeyboxKeyStatusResult>,
                            data: IResp<KeyboxKeyStatusResult>
                        ) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                            }
                            // 拔钥匙回复
                            session.call(CommandManager.keyboxCommand.pullOutReply()).execute()
                        }
                    })

                session.call(CommandManager.keyboxCommand.putBack())
                    .subscribe(object : NotifyCallback<KeyboxKeyStatusResult> {
                        override fun onSuccess(
                            call: ISessionCall<KeyboxKeyStatusResult>,
                            data: IResp<KeyboxKeyStatusResult>
                        ) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                            }
                            // 插钥匙回复
                            session.call(CommandManager.keyboxCommand.putBackReply()).execute()
                        }
                    })

                session.call(CommandManager.keyboxCommand.unlock(0, 0, 0, 0))
                    .subscribe(object : NotifyCallback<KeyboxUnlockResult> {
                        override fun onSuccess(
                            call: ISessionCall<KeyboxUnlockResult>,
                            data: IResp<KeyboxUnlockResult>
                        ) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                            }
                            // 开锁回复
                            session.call(CommandManager.keyboxCommand.unlockReply()).execute()
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
            session.call(
                CommandManager.keyboxCommand.unlock(
                    uid = 0,
                    timestamp = System.currentTimeMillis() / 1000
                )
            )
                .enqueue(object : SessionCallback<KeyboxUnlockResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxUnlockResult>,
                        data: IResp<KeyboxUnlockResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<KeyboxUnlockResult>, e: Throwable) {
                    }
                })
        }

        btn_info.setOnClickListener {
            session.call(CommandManager.keyboxCommand.getLockInfo())
                .enqueue(object : SessionCallback<KeyboxLockInfoResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxLockInfoResult>,
                        data: IResp<KeyboxLockInfoResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<KeyboxLockInfoResult>, e: Throwable) {
                    }
                })
        }

        btn_set_timestamp.setOnClickListener {
            session.call(CommandManager.keyboxCommand.setTimestamp(System.currentTimeMillis() / 1000))
                .enqueue(object : SessionCallback<KeyboxSetTimestampResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxSetTimestampResult>,
                        data: IResp<KeyboxSetTimestampResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxSetTimestampResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_record.setOnClickListener {
            session.call(CommandManager.keyboxCommand.getUnlockRecord())
                .asyncTimeout(30000)
                .asyncCall(object : AsyncCallback<KeyboxLockRecordResult> {
                    override fun onTimeout() {

                    }

                    override fun onStarted(success: Boolean) {
                        Log.d("=====", "开始读取记录: $success")
                    }

                    override fun onReceiving(
                        call: ISessionCall<KeyboxLockRecordResult>,
                        data: IResp<KeyboxLockRecordResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                            if (result.recordIndex < 10) {
                                session.call(CommandManager.keyboxCommand.recordReply(index = result.recordIndex))
                                    .execute()
                            }
                        }
                    }

                    override fun onReceived() {
                        Log.d("=====", "读取记录完毕")
                    }
                })
        }

        btn_clean_record.setOnClickListener {
            session.call(CommandManager.keyboxCommand.cleanUnlockRecord()).execute()
        }

        btn_alert_open.setOnClickListener {
            session.call(CommandManager.keyboxCommand.setAlert(0x01))
                .enqueue(object : SessionCallback<KeyboxAlertResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxAlertResult>,
                        data: IResp<KeyboxAlertResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<KeyboxAlertResult>, e: Throwable) {
                    }
                })
        }

        btn_alert_close.setOnClickListener {
            session.call(CommandManager.keyboxCommand.setAlert(0x00))
                .enqueue(object : SessionCallback<KeyboxAlertResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxAlertResult>,
                        data: IResp<KeyboxAlertResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<KeyboxAlertResult>, e: Throwable) {
                    }
                })
        }

        btn_modify_key.setOnClickListener {
            session.call(CommandManager.keyboxCommand.modifyDeviceKey(deviceKey = "aaaaaaaa"))
                .enqueue(object : SessionCallback<KeyboxModifyDeviceKeyResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxModifyDeviceKeyResult>,
                        data: IResp<KeyboxModifyDeviceKeyResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxModifyDeviceKeyResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_reset_key.setOnClickListener {
            session.call(CommandManager.keyboxCommand.resetDeviceKey())
                .enqueue(object : SessionCallback<KeyboxModifyDeviceKeyResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxModifyDeviceKeyResult>,
                        data: IResp<KeyboxModifyDeviceKeyResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxModifyDeviceKeyResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_set_temp_pwd.setOnClickListener {
            session.call(CommandManager.keyboxCommand.tempPwdControl(0x01, 12345678))
                .enqueue(object : SessionCallback<KeyboxTempPwdControlResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        data: IResp<KeyboxTempPwdControlResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_query_temp_pwd.setOnClickListener {
            session.call(CommandManager.keyboxCommand.tempPwdControl(0x02, 12345678))
                .enqueue(object : SessionCallback<KeyboxTempPwdControlResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        data: IResp<KeyboxTempPwdControlResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_delete_temp_pwd.setOnClickListener {
            session.call(CommandManager.keyboxCommand.tempPwdControl(0x03, 12345678))
                .enqueue(object : SessionCallback<KeyboxTempPwdControlResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        data: IResp<KeyboxTempPwdControlResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_delete_all_temp_pwd.setOnClickListener {
            session.call(CommandManager.keyboxCommand.tempPwdControl(0x04, 0))
                .enqueue(object : SessionCallback<KeyboxTempPwdControlResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        data: IResp<KeyboxTempPwdControlResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxTempPwdControlResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_random_code_config.setOnClickListener {
            session.call(CommandManager.keyboxCommand.randomPwdConfig(7, 3600))
                .enqueue(object : SessionCallback<KeyboxRandomCodeConfigResult> {
                    override fun onSuccess(
                        call: ISessionCall<KeyboxRandomCodeConfigResult>,
                        data: IResp<KeyboxRandomCodeConfigResult>
                    ) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(
                        call: ISessionCall<KeyboxRandomCodeConfigResult>,
                        e: Throwable
                    ) {
                    }
                })
        }

        btn_show_random_code.setOnClickListener {
            val openKey = TOTPUtils.getOpenKey(System.currentTimeMillis() / 1000, 3600, 7)
            Log.d("=====", "随机码: $openKey")
        }

        btn_upgrade.setOnClickListener {
            val filePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(filePath, "key_box_v6_1129a.aes")

            ScooterUpgradeTask(session, file, session.getDeviceType(), session.getUpdateKey(), object :
                OnProgressListener<Boolean> {
                override fun onProgress(progress: Progress) {
                    Log.d("=====", progress.toString())
                }

                override fun onStatusChanged(status: Int, e: Throwable?) {
                    Log.e("=====", "status = $status")
                }

                override fun onComplete(t: Boolean) {
                    Log.d("=====", "complete = $t")
                }
            }).start()
        }

        btn_get_fw_info.setOnClickListener {
            ScooterReadTask(session, object : OnProgressListener<String> {
                override fun onProgress(progress: Progress) {
                    Log.d(
                        "=====",
                        "percent = ${progress.getPercent()}%, speed = ${progress.getSpeed()}b/s"
                    )
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
}