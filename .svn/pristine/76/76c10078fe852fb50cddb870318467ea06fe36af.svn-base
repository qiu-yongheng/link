package com.omni.ble.library.proxy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.omni.ble.library.model.GattAttributes;
import com.omni.ble.library.service.HorseLockService;

import java.util.UUID;

/**
 * @author 邱永恒
 * @time 2019/6/27 15:01
 * @desc
 */
public class BLEBikeServiceProxy {
    private HorseLockService bikeService;
    private MyBroadCast myBroadCast;
    private OnBLEListener listener;

    private String TAG = "BLEBikeServiceProxy";
    public UUID ser;
    public UUID write;
    public UUID notify;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bikeService = ((HorseLockService.LocalBinder) service).getService();
            bikeService.setService(ser);
            bikeService.setWrite(write);
            bikeService.setNotify(notify);

            Log.i(TAG, "==========================================绑定HorseLockService");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "==========================================断开绑定");
            bikeService = null;
        }
    };

    public void start(Context context) {
        Intent intent = new Intent(context, HorseLockService.class);
        boolean bindService = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "绑定结果: " + bindService);
        registerBroad(context);
    }

    public void stop(Context context) {
        context.unbindService(serviceConnection);
        unregisterReceiver(context);
        listener = null;
    }

    public void connect(String mac) {
        if (bikeService != null) {
            if (!bikeService.isConnectedDevice(mac)) {
                bikeService.connect(mac);
            } else {
                Log.i(TAG, "areRiding: 单车蓝牙已经连接，可以通讯");
            }
        }
    }

    public void stopScan() {
        if (bikeService != null) {
            bikeService.stopScan();
        }
    }

    public boolean isConnectedDevice(String mac) {
        if (bikeService != null) {
            return bikeService.isConnectedDevice(mac);
        }
        return false;
    }

    public void getOpenKey(String bleKey) {
        if (bikeService != null) {
            bikeService.getOpenKey(bleKey);
        }
    }

    public String getKey() {
        return String.valueOf(bikeService.BLECkey);
    }

    public void setOpenLock(long timestamp) {
        if (bikeService != null) {
            bikeService.setOpenLock(timestamp, (byte) 0);
        }
    }

    public void getLockStatus() {
        if (bikeService != null) {
            bikeService.getLockStatus();
        }
    }

    public void getFwInfo() {
        if (bikeService != null) {
            bikeService.getFwInfo();
        }
    }

    public void sendGetFirmwareInfoDetail(int pack, byte deviceType) {
        if (bikeService != null) {
            bikeService.sendGetFirmwareInfoDetail(pack, deviceType);
        }
    }

    public void sendFwUpdatePackCommand(int pack, byte[] data) {
        if (bikeService != null) {
            bikeService.sendFwUpdatePackCommand(pack, data);
        }
    }

    public void getOldData() {
        if (bikeService != null) {
            bikeService.getOldData();
        }
    }

    public void disconnect() {
        if (bikeService != null) {
            bikeService.disconnect();
        }
    }

    public void clearData() {
        if (bikeService != null) {
            bikeService.clearData();
        }
    }

    public void sendCloseResponse() {
        if (bikeService != null) {
            bikeService.setLockResponse();
        }
    }

    public void sendOpenResponse() {
        if (bikeService != null) {
            bikeService.sendOpenResponse();
        }
    }

    public void shutdown() {
        if (bikeService != null) {
            bikeService.sendShutDown();
        }
    }

    public void sendUpdateFirmwareCommand(int nPack, int crc, byte deviceType, String updateKey) {
        if (bikeService != null) {
            bikeService.sendUpdateFirmwareCommand(nPack, crc, deviceType, updateKey);
        }
    }

    public void sendConfigModifyCommand(int pack, int crc, byte deviceType) {
        if (bikeService != null) {
            bikeService.sendConfigModifyCommand(pack, crc, deviceType);
        }
    }

    public void setOnBLEListener(OnBLEListener listener) {
        this.listener = listener;
    }

    private void registerBroad(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HorseLockService.ACTION_BLE_LOCK_OPEN_STATUS);//开锁成功
        intentFilter.addAction(HorseLockService.ACTION_BLE_LOCK_CLOSE_STATUS);//蓝牙上锁广播
        intentFilter.addAction(HorseLockService.ACTION_BLE_SERVICE_NO_FIND);//服务未发现
        intentFilter.addAction(HorseLockService.ACTION_BLE_WRITE_NOTIFY);//蓝牙通信去获得通信Key广播
        intentFilter.addAction(HorseLockService.ACTION_BLE_GET_KEY);//获取Key成功广播
        intentFilter.addAction(HorseLockService.ACTION_BLE_LOCK_STATUS);//获取锁状态广播
        intentFilter.addAction(HorseLockService.ACTION_BLE_HAVE_OLD_DATA);//获取到旧数据
        intentFilter.addAction(HorseLockService.ACTION_BLE_GET_DATA_TIME_OUT);//获得设备数据超时广播(其中包含：key，锁状态，旧数据)
        intentFilter.addAction(HorseLockService.ACTION_BLE_DISCONNECTED);//断开连接
        intentFilter.addAction(HorseLockService.ACTION_BLE_SCAN_STOP);//停止扫描
        intentFilter.addAction(HorseLockService.ACTION_BLE_SCAN_TIMEOUT);//扫描超时
        intentFilter.addAction(HorseLockService.ACTION_BLE_HORSE_LOCK_FW_INFO);//获取固件信息完毕
        intentFilter.addAction(HorseLockService.ACTION_BLE_HORSE_LOCK_FW_INFO_ING);//获取固件信息包信息
        intentFilter.addAction(HorseLockService.ACTION_BLE_HORSE_LOCK_FW_UPGRADE);//固件升级传输
        intentFilter.addAction(HorseLockService.ACTION_BLE_HORSE_LOCK_INFO_MODIFY);//锁信息修改
        myBroadCast = new MyBroadCast();
        LocalBroadcastManager.getInstance(context).registerReceiver(myBroadCast, intentFilter);
    }

    private void unregisterReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(myBroadCast);
    }


    class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null || listener == null) {
                return;
            }


            String action = intent.getAction();
            switch (action) {
                case HorseLockService.ACTION_BLE_LOCK_OPEN_STATUS:
                    // 开锁成功
                    listener.onLockOpen(
                            intent.getStringExtra("number"),
                            intent.getIntExtra("status", 0),
                            intent.getLongExtra("timestamp", 0));
                    break;
                case HorseLockService.ACTION_BLE_LOCK_CLOSE_STATUS:
                    // 关锁成功
                    listener.onLockClose(
                            intent.getIntExtra("status", 0),
                            intent.getIntExtra("runTime", 0),
                            intent.getLongExtra("timestamp", 0)
                    );
                    break;
                case HorseLockService.ACTION_BLE_WRITE_NOTIFY:
                    // 开启通知
                    listener.onBLEWriteNotify();
                    break;
                case HorseLockService.ACTION_BLE_GET_KEY:
                    // 获取到key
                    listener.onGetKey();
                    break;
                case HorseLockService.ACTION_BLE_LOCK_STATUS:
                    // 获取锁状态
                    listener.onGetLockStatus(
                            intent.getIntExtra(HorseLockService.EXTRA_POWER, 0),
                            intent.getIntExtra(HorseLockService.EXTRA_HAS_OLD, 1) == 0,
                            intent.getLongExtra(HorseLockService.EXTRA_TIMESTAMP, 0),
                            intent.getIntExtra(HorseLockService.EXTRA_OPEN_STATUS, 1) == 0
                    );
                    break;
                case HorseLockService.ACTION_BLE_HAVE_OLD_DATA:
                    // 获取到旧数据
                    listener.onGetOldData(
                            intent.getIntExtra("uid", 0),
                            intent.getIntExtra("runTime", 0),
                            intent.getLongExtra("timestamp", 0)
                    );
                    break;
                case HorseLockService.ACTION_BLE_HORSE_LOCK_FW_INFO:
                    // 获取固件信息完毕
                    listener.onGetFWInfoSuccess(
                            intent.getStringExtra("firmwareData")
                    );
                    break;
                case HorseLockService.ACTION_BLE_HORSE_LOCK_FW_INFO_ING:
                    // 获取固件信息包数据
                    listener.onGetFWInfoPack(
                            intent.getIntExtra("nPack", 0),
                            intent.getIntExtra("totalPack", 0)
                    );
                    break;
                case HorseLockService.ACTION_BLE_HORSE_LOCK_FW_UPGRADE:
                    // 固件升级
                    listener.onFwUpgradeTransmission(
                            intent.getIntExtra("currentPack", 0),
                            intent.getIntExtra("deviceType", 0)
                    );
                    break;
                case HorseLockService.ACTION_BLE_HORSE_LOCK_INFO_MODIFY:
                    // 锁信息修改
                    listener.onLockInfoModify(
                            intent.getIntExtra("pack", 0),
                            intent.getIntExtra("deviceType", 0)
                    );
                    break;
                case HorseLockService.ACTION_BLE_GET_DATA_TIME_OUT:
                    // 获取数据超时
                    listener.onGetDataTimeout();
                    break;
                case HorseLockService.ACTION_BLE_DISCONNECTED:
                    // 断开连接
                    listener.onDisconnect();
                    break;
                case HorseLockService.ACTION_BLE_SCAN_STOP:
                    // 停止扫描
                    listener.stopScan();
                    break;
                case HorseLockService.ACTION_BLE_SCAN_TIMEOUT:
                    // 扫描超时
                    listener.scanTimeout();
                    break;
                case HorseLockService.ACTION_BLE_SERVICE_NO_FIND:
                    // 服务未发现
                    listener.serviceNoFind();
                    break;
            }
        }
    }
}
