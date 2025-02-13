package com.omni.support

import android.bluetooth.BluetoothDevice
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.*
import com.omni.support.ble.protocol.base.model.ReadResult
import com.omni.support.ble.protocol.base.model.StartReadResult
import com.omni.support.ble.protocol.base.model.WriteResult
import com.omni.support.ble.protocol.ulock.model.ULockInfoResult
import com.omni.support.ble.protocol.ulock.model.ULockLockResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.ULockSession
import com.omni.support.ble.task.*
import com.omni.support.ble.task.bike.BLReadTask
import com.omni.support.ble.task.bike.BLUpgradeTask
import com.omni.support.ble.task.bike.BLWriteTask
import com.omni.support.ble.utils.DataUtils
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_ble.*
import java.io.File

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:48
 *
 * @desc
 *
 */
class ULockTestActivity : BaseActivity() {
    private lateinit var session: ULockSession

    override fun getLayoutId(): Int {
        return R.layout.activity_ulock
    }

    override fun initListener() {
        session = ULockSession.Builder()
//            .address("D8:6E:46:79:B8:37")
            .address("DB:A6:92:66:65:75")
            .deviceKey("yOTmK50z")
            .deviceType("D1")
            .updateKey("Vgz7")
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {

            }

            override fun onConnecting() {
                Toast.makeText(this@ULockTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@ULockTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@ULockTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@ULockTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
            }

            override fun onReady() {
                // 关锁监听
                session.call(CommandManager.ulockCommand.lock())
                    .subscribe(object : NotifyCallback<ULockLockResult> {
                        override fun onSuccess(call: ISessionCall<ULockLockResult>, data: IResp<ULockLockResult>) {
                            val result = data.getResult()
                            if (result != null) {
                                Log.d("=====", result.toString())
                            }
                            // 关锁回复
                            session.call(CommandManager.blCommand.lockReply()).execute()
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
            session.call(CommandManager.ulockCommand.unlock(0, System.currentTimeMillis() / 1000))
                .timeout(3000)
                .enqueue(object : SessionCallback<Boolean> {
                    override fun onSuccess(call: ISessionCall<Boolean>, data: IResp<Boolean>) {
                        val isSuccess = data.getResult() ?: false
                        Toast.makeText(this@ULockTestActivity, if (isSuccess) "开锁成功" else "开锁失败", Toast.LENGTH_SHORT)
                            .show()
                        // 开锁回复
                        session.call(CommandManager.blCommand.unlockReply()).execute()
                    }

                    override fun onFailure(call: ISessionCall<Boolean>, e: Throwable) {
                    }
                })
        }

        btn_info.setOnClickListener {
            session.call(CommandManager.ulockCommand.getLockInfo())
                .enqueue(object : SessionCallback<ULockInfoResult> {
                    override fun onSuccess(call: ISessionCall<ULockInfoResult>, data: IResp<ULockInfoResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<ULockInfoResult>, e: Throwable) {

                    }
                })
        }

        btn_fw_info.setOnClickListener {
            session.call(CommandManager.ulockCommand.startRead())
                .enqueue(object : SessionCallback<StartReadResult> {
                    override fun onSuccess(call: ISessionCall<StartReadResult>, data: IResp<StartReadResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<StartReadResult>, e: Throwable) {
                    }
                })
        }

        btn_fw.setOnClickListener {
            session.call(CommandManager.ulockCommand.read(0, session.getDeviceType()))
                .enqueue(object : SessionCallback<ReadResult> {
                    override fun onSuccess(call: ISessionCall<ReadResult>, data: IResp<ReadResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<ReadResult>, e: Throwable) {
                    }
                })
        }

        btn_get_fw_info.setOnClickListener {
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

        btn_start_modify.setOnClickListener {
            val unPack = DataUtils.unPack("IP:120.24.228.199,PORT:9666,IPMODE:0,")
            session.call(CommandManager.ulockCommand.startWrite(unPack.totalPack, unPack.crc, session.getDeviceType()))
                .enqueue(object : SessionCallback<WriteResult> {
                    override fun onSuccess(call: ISessionCall<WriteResult>, data: IResp<WriteResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                        session.call(CommandManager.ulockCommand.write(0, unPack.pack[0]))
                            .enqueue(object : SessionCallback<WriteResult> {
                                override fun onSuccess(call: ISessionCall<WriteResult>, data: IResp<WriteResult>) {
                                    val result = data.getResult()
                                    if (result != null) {
                                        Log.d("=====", result.toString())
                                    }
                                }

                                override fun onFailure(call: ISessionCall<WriteResult>, e: Throwable) {
                                }
                            })
                    }

                    override fun onFailure(call: ISessionCall<WriteResult>, e: Throwable) {
                    }
                })
        }

        btn_send_pack.setOnClickListener {
            BLWriteTask(
                session,
                "IP:120.24.228.199,PORT:9666,IPMODE:0,",
                session.getDeviceType(),
                object :
                    OnProgressListener<Boolean> {
                    override fun onProgress(progress: Progress) {
                        Log.d("=====", "percent = ${progress.getPercent()}%, speed = ${progress.getSpeed()}b/s")
                    }

                    override fun onStatusChanged(status: Int, e: Throwable?) {
                        Log.e("=====", "status = $status")
                    }

                    override fun onComplete(t: Boolean) {
                        Log.d("=====", "complete = $t")
                    }
                }).start()
        }

        btn_upgrade.setOnClickListener {
            val filePath = Environment.getExternalStorageDirectory().toString()

            val file = File(filePath, "upgrade.txt")
            BLUpgradeTask(
                session,
                file,
                session.getDeviceType(),
                session.getUpdateKey(),
                object :
                    OnProgressListener<Boolean> {
                    override fun onProgress(progress: Progress) {

                    }

                    override fun onStatusChanged(status: Int, e: Throwable?) {
                        Log.e("=====", "status = $status")
                    }

                    override fun onComplete(t: Boolean) {
                        Log.d("=====", "complete = $t")
                    }
                }).start()
        }

        btn_shutdown.setOnClickListener {
            session.call(CommandManager.ulockCommand.shutdown()).execute()
        }

        btn_get_log.setOnClickListener {
            session.call(CommandManager.ulockCommand.getLog(session.getDeviceType()))
                .asyncTimeout(5000)
                .asyncCall(object : AsyncCallback<String> {
                    override fun onTimeout() {

                    }

                    override fun onStarted(success: Boolean) {

                    }

                    override fun onReceiving(call: ISessionCall<String>, data: IResp<String>) {
                        Log.d("=====", "${data.getResult()}")
                    }

                    override fun onReceived() {

                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        session.disConnect()
    }
}