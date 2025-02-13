package com.omni.support

import android.util.Log
import android.widget.Toast
import com.omni.support.ble.core.IResp
import com.omni.support.ble.core.ISessionCall
import com.omni.support.ble.core.NotifyCallback
import com.omni.support.ble.core.SessionCallback
import com.omni.support.ble.protocol.base.model.UnlockResult
import com.omni.support.ble.protocol.box.model.BoxLockInfoResult
import com.omni.support.ble.protocol.box.model.BoxLowUnlockConfigResult
import com.omni.support.ble.protocol.wheelchair.model.WcInfoResult
import com.omni.support.ble.protocol.wheelchair.model.WcLockResult
import com.omni.support.ble.protocol.wheelchair.model.WcOldDataResult
import com.omni.support.ble.protocol.wheelchair.model.WcUpdateIdErrorResult
import com.omni.support.ble.rover.CommandManager
import com.omni.support.ble.session.ISessionListener
import com.omni.support.ble.session.SimpleSessionListener
import com.omni.support.ble.session.sub.BoxSession
import com.omni.support.ble.session.sub.WheelchairSession
import com.omni.support.ble.task.OnProgressListener
import com.omni.support.ble.task.Progress
import com.omni.support.ble.task.bike.BLReadTask
import com.omni.support.widget.base.BaseActivity
import kotlinx.android.synthetic.main.activity_box.*
import kotlinx.android.synthetic.main.activity_wc.*
import kotlinx.android.synthetic.main.activity_wc.btn_connect
import kotlinx.android.synthetic.main.activity_wc.btn_disconnect
import kotlinx.android.synthetic.main.activity_wc.btn_info
import kotlinx.android.synthetic.main.activity_wc.btn_key
import kotlinx.android.synthetic.main.activity_wc.btn_unlock

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:48
 *
 * @desc
 *
 */
class BoxTestActivity : BaseActivity() {
    private lateinit var session: BoxSession
    private var id: Long = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_box
    }

    override fun initListener() {
        session = BoxSession.Builder()
//            .address("E4:B9:EB:AC:CF:E9")
//            .address("FF:74:37:54:5A:8E")
//            .address("CB:7C:82:37:24:1E")
            .address("C3:FC:DD:43:88:12")
//            .address("D6:38:6F:7F:53:A1")
            .deviceKey("OmniW4GX")
//            .deviceKey("yOTmK50z")
            .deviceType("54")
            .updateKey("Vgz7")
            .build()

        session.setListener(object : SimpleSessionListener() {
            override fun onConnecting() {
                Toast.makeText(this@BoxTestActivity, "正在连接...", Toast.LENGTH_SHORT).show()
            }

            override fun onConnected() {
                Toast.makeText(this@BoxTestActivity, "连接成功", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@BoxTestActivity, "断开连接", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceNoSupport() {
                Toast.makeText(this@BoxTestActivity, "设备不支持", Toast.LENGTH_SHORT).show()
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

        btn_key.setOnClickListener {
            session.initConfig()
        }

        btn_unlock.setOnClickListener {
            session.call(CommandManager.boxCommand.unlock(1, 0, System.currentTimeMillis() / 1000, 0))
                .timeout(3000)
                .enqueue(object : SessionCallback<UnlockResult> {
                    override fun onSuccess(call: ISessionCall<UnlockResult>, data: IResp<UnlockResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<UnlockResult>, e: Throwable) {
                    }
                })
        }

        btn_info.setOnClickListener {
            session.call(CommandManager.boxCommand.getLockInfo())
                .enqueue(object : SessionCallback<BoxLockInfoResult> {
                    override fun onSuccess(call: ISessionCall<BoxLockInfoResult>, data: IResp<BoxLockInfoResult>) {
                        val result = data.getResult()
                        if (result != null) {
                            Log.d("=====", result.toString())
                        }
                    }

                    override fun onFailure(call: ISessionCall<BoxLockInfoResult>, e: Throwable) {

                    }
                })
        }

       btn_low_open.setOnClickListener {
           session.call(CommandManager.boxCommand.lowUnlockConfig(data = 1))
               .enqueue(object : SessionCallback<BoxLowUnlockConfigResult> {
                   override fun onSuccess(
                       call: ISessionCall<BoxLowUnlockConfigResult>,
                       data: IResp<BoxLowUnlockConfigResult>
                   ) {
                       val result = data.getResult()
                       if (result != null) {
                           Log.d("=====", result.toString())
                       }
                   }

                   override fun onFailure(
                       call: ISessionCall<BoxLowUnlockConfigResult>,
                       e: Throwable
                   ) {
                       TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                   }
               })
       }

       btn_low_close.setOnClickListener {
           session.call(CommandManager.boxCommand.lowUnlockConfig(data = 0))
               .enqueue(object : SessionCallback<BoxLowUnlockConfigResult> {
                   override fun onSuccess(
                       call: ISessionCall<BoxLowUnlockConfigResult>,
                       data: IResp<BoxLowUnlockConfigResult>
                   ) {
                       val result = data.getResult()
                       if (result != null) {
                           Log.d("=====", result.toString())
                       }
                   }

                   override fun onFailure(
                       call: ISessionCall<BoxLowUnlockConfigResult>,
                       e: Throwable
                   ) {
                       TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                   }
               })
       }

    }

    override fun onDestroy() {
        super.onDestroy()
        session.disConnect()
    }
}