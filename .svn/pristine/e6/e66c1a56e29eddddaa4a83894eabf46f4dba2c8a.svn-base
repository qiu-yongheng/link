package com.omni.ble.library.activity;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.omni.ble.library.service.BaseService;
import com.omni.ble.library.service.KeyLockService;
import com.omni.ble.library.service.OBL2Service;

/**
 * 新版 纯蓝牙<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseOBL2ServiceActivity extends Activity {
    private static final String TAG=BaseOBL2ServiceActivity.class.getSimpleName();

    protected OBL2Service mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((OBL2Service.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 柜锁 的服务器绑定 回调
     * @param mBLEService
     */
    protected  void onServiceConnectedCallBack(OBL2Service mBLEService){

    }

    /**
     *
     * @param deviceAddress 扫描的MAC地址
     * @param scanPeriod  扫描时长(毫秒数)
     */
    protected void startScanBLEDevice(String deviceAddress,int scanPeriod){
        mService.startScanBLEDevice(  deviceAddress,  scanPeriod);
    }

    /**
     * 停止扫描设备
     */
    protected void stopScanBLEDevice( ){
        mService.stopScanBLEDevice(  );
    }

    /**
     * 判断 设备是否通过蓝牙连接了
     * @param mac  要检查的设备的MAC地址
     * @return true APP已经连接了当前设备，false 没有连接当前设备
     */
    protected  boolean isConnectedDevice(String mac){
        return mService.isConnectedDevice(mac);
    }

    /**
     * 扫描到指令BEL设备的回调
     * @param scanBLEDevice
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice){  }
    /**
     * 指令扫描周期，没有扫描的指令BLE设备的回调
     * @param deviceAddress 指令扫描的设备的MAC地址
     */
    protected  void onScanBLEDeviceNotCallBack(String  deviceAddress){  }


    /**
     * 连接 纯蓝牙锁
     * @param mac OGBL1 Mac  纯蓝牙锁 MAC地址
     */
    protected  void connectOGBL2(String mac){
        mService.connect(mac);
    }
    protected void disconnectOGBL2(){
        mService.disconnect();
    }

    /**
     * 获取通信KEY 指令
     * @param deviceKey  设备KEY
     */
    protected  void sendGetKeyCommand(String deviceKey){
        mService.sendGetKeyCommand( deviceKey);
    }


    protected  void sendOpenCommand(int uid,long timestamp){
        mService.sendOpenCommand(uid,timestamp);

    }

    protected  void sendShutDown(){
        mService.sendShutDown();
    }

    protected  void getLockInfo( ){
        mService.sendGetLockInfoCommand( );

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBindService();

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: 注册本地广播");
        registerLocalReceiver();
        super.onStart();
    }

    private void registerLocalReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BaseService.ACTION_BLE_WRITE_NOTIFY);
        intentFilter.addAction(BaseService.ACTION_BLE_OPT_GET_KEY_WITH_MAC);


        intentFilter.addAction(OBL2Service.ACTION_OGBL2_OPEN);
        intentFilter.addAction(OBL2Service.ACTION_OGBL2_CLOSE);
        intentFilter.addAction(OBL2Service.ACTION_OBL2_INFO);


        intentFilter.addAction(KeyLockService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE_NOT);


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this,OBL2Service.class);
        //启动服务
        startService(gattServiceIntent);
        //绑定服务。绑定服务的目的是和服务进行通信
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Log.i(TAG, "onStop: 注销本地广播");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);

        super.onDestroy();
    }





    protected  void onBLEDisconnected(){

    }


    /**
     * 获取到通信KEY的回调，在主线程中回调
     */
    protected  void onBLEGetKey(){

    }

    /**
      获取到通信的KEY的回调，在主线程中回调
     * @param mac 通信设备的MAC地址
     */
    protected  void onBLEGetKey(String mac,byte ckey){

        Log.i(TAG, "onBLEGetKey: mac="+mac+",ckey="+ckey);
    }
    /**
     * 注册完通知的回调,在主线程中回调。
     */
    protected  void onBLEWriteNotify(){
        Log.i(TAG, "onBLEWriteNotify: write notify thread name="+Thread.currentThread().getName());

    }

    /**
     * 开锁接口的回调
     * @param status ,0开锁成，1开锁失败
     */
    protected  void onBLEOpenCallback(int status,long timestamp){


    }


    protected  void onBLECloseCallback(int status,long openId,long openTime){


    }


    /**
     *
     * @param status 开锁锁状态，0开锁，1关锁
     * @param power 电量，0.1V
     * @param old 0 有旧数据，1 没有就数据
     */
    protected  void onBLEInfoCallback(int status,int power,int old,long timestamp){


    }


    /**
     * 指令发送错误的回调
     * @param status
     */
    protected  void onBLECommandError(int status){
        Log.i(TAG, "onBLECommandError: status="+status);
        if(status==3){
            Toast.makeText(this, "通信KEY错误", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(this, "未获取通信KEY", Toast.LENGTH_SHORT).show();
        }else if(status==1){
            Toast.makeText(this, "CRC认证错误", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: all  action="+intent.getAction());
            if(BaseService.ACTION_BLE_OPT_GET_KEY_WITH_MAC.equals(intent.getAction()) ){
                String mac=intent.getStringExtra("mac");
                Byte ckey=intent.getByteExtra("ckey",(byte)0);
                onBLEGetKey();
                onBLEGetKey(mac,ckey);
            }else if(BaseService.ACTION_BLE_WRITE_NOTIFY.equals(intent.getAction())){
              //  Log.i(TAG, "onReceive: write notify thread name="+Thread.currentThread().getName());
                onBLEWriteNotify();
            }else if(BaseService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            }
            else if(BaseService.ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BaseService.EXTRA_SCAN_DEVICE);
                onScanBLEDeviceCallBack(bluetoothDevice);
            }
            else if(BaseService.ACTION_BLE_SCAN_DEVICE_NOT.equals(intent.getAction())){
                String addressMac = intent.getStringExtra(BaseService.EXTRA_DEVICE_MAC);
                onScanBLEDeviceNotCallBack(addressMac);
            }else if(OBL2Service.ACTION_OGBL2_OPEN.equals(intent.getAction())){

                int status =intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra("timestamp",0L);
                onBLEOpenCallback(status,timestamp);
            }else if(OBL2Service.ACTION_OGBL2_CLOSE.equals(intent.getAction())){
                int status =intent.getIntExtra("status",0);
                long openId =intent.getLongExtra("openTimestamp",0L);
                long openTime =intent.getLongExtra("openTime",0L);
                onBLECloseCallback(status,openId,openTime);

            }else if(OBL2Service.ACTION_OBL2_INFO.equals(intent.getAction())){

                int lockStatus = intent.getIntExtra(OBL2Service.EXTRA_LOCK_STATUS,0);
                int power = intent.getIntExtra(OBL2Service.EXTRA_LOCK_POWER,0);
                int old = intent.getIntExtra(OBL2Service.EXTRA_LOCK_OLD,1);
                long timestamp = intent.getLongExtra(OBL2Service.EXTRA_LOCK_INCO_TIMESTAMP,1L);
                onBLEInfoCallback(lockStatus,power,old,timestamp);
            }
        }
    };
}
