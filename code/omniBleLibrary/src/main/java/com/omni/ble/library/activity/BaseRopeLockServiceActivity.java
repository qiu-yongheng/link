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

import com.omni.ble.library.service.RopeLockService;

/**
 * 钢缆锁<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseRopeLockServiceActivity extends Activity {
    private static final String TAG= BaseRopeLockServiceActivity.class.getSimpleName();

    protected RopeLockService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((RopeLockService.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 钢缆锁 的服务器绑定 回调
     * @param mBLEService
     */
    protected  void onServiceConnectedCallBack(RopeLockService mBLEService){

    }

    protected  boolean isConnectDevice(String mac){

        return mService.isConnectedDevice(mac);

    }


    /**
     * 扫描指定MAC地址，看是否在附近
     * @param deviceAddress 要扫描的设备的MAC地址
     * @param scanPeriod 扫描的毫秒数
     */
    protected void startScanBLEDevice(String deviceAddress,int scanPeriod){
        mService.startScanBLEDevice(  deviceAddress,  scanPeriod);
    }


    /**
     *  扫描到指定BEL设备的回调
     * @param scanBLEDevice  扫描到的BLE device
     * @param record  广播信息
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice, byte[] record ){  }

    /**
     * 扫描到指定BEL设备的回调
     * @param scanBLEDevice 扫描到的BLE device
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice  ){  }

    /**
     * 没有扫描到设备的回调
     * @param addressMac 指令要扫描的 钢缆锁MAC
     */
    protected  void onScanBLEDeviceNotCallBack(String addressMac){}



    /**
     * 连接 钢缆锁
     * @param ropeLockMac  钢缆锁MAC地址
     */
    protected  void connectRopeLock(String ropeLockMac){
        mService.connect(ropeLockMac);
    }
    protected  void connectRopeLock(String ropeLockMac,byte[] inputKey,byte[] keyOrg){
        mService.connect(ropeLockMac,inputKey,keyOrg);
    }

    protected void disconnectRopeLock(){
//        Log.i(TAG, "onItemClick: service activity  断开连接的 操作线程="+Thread.currentThread().getName());
        mService.disconnect();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBindService();
        registerLocalReceiver();

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: 注册本地广播");

        super.onStart();
    }

    private void registerLocalReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RopeLockService.ACTION_BLE_WRITE_NOTIFY);
        intentFilter.addAction(RopeLockService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(RopeLockService.ACTION_BLE_SCAN_DEVICE_NOT);
        intentFilter.addAction(RopeLockService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(RopeLockService.ACTION_LOCK_MAP_KEY);
        intentFilter.addAction(RopeLockService.ACTION_LOCK_NOTIFY_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this,RopeLockService.class);
        //启动服务
        startService(gattServiceIntent);
        //绑定服务。绑定服务的目的是和服务进行通信
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        Log.i(TAG, "onStop: 注销本地广播");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }


    /**
     * 柜锁 开锁回调
     * @param status
     * @param timestamp
     */
    protected  void onBLEOpenCallBack(int status, long timestamp){
        Log.i(TAG, "onBLEOpenCallBack: status="+status+",timestamp="+timestamp);

    }




    protected  void onBLEDisconnected(){

    }
    protected  void onBLEGetKeyError(){

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

    protected  void sendUnlock(){
        mService.writeUnlock();
    }
    protected  void sendUnlock(long timestamp){
        mService.writeUnlock(timestamp);
    }

    protected  void sendShutdown(){
        mService.writeShutdown();
    }

    protected  void sendClearKey(){
        mService.writeClearKey();
    }

    protected  void sendDeviceConfig(byte openMode){
        mService.writeDeviceConfig(openMode);
    }



    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(RopeLockService.ACTION_BLE_WRITE_NOTIFY.equals(intent.getAction())){
                onBLEWriteNotify();
            }else if(RopeLockService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            }
             else if(RopeLockService.ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(RopeLockService.EXTRA_SCAN_DEVICE);
                byte[] record = intent.getByteArrayExtra(RopeLockService.EXTRA_SCAN_RECORD);
                onScanBLEDeviceCallBack(bluetoothDevice,record);
                onScanBLEDeviceCallBack(bluetoothDevice);
            }  else if(RopeLockService.ACTION_BLE_SCAN_DEVICE_NOT.equals(intent.getAction())){
                String addressMac = intent.getStringExtra(RopeLockService.EXTRA_DEVICE_MAC);
                onScanBLEDeviceNotCallBack(addressMac);
            }else if(RopeLockService.ACTION_LOCK_MAP_KEY.equals(intent.getAction())){
                 int type = intent.getIntExtra(RopeLockService.EXTRA_MAP_KEY_TYPE,RopeLockService.MAP_KEY_TYPE_LOCAL_KEY_NOT_EXIST);
                 int randNum=intent.getIntExtra(RopeLockService.EXTRA_MAP_KEY_RAND_NUM,0);
                onBLECallbackMapKeyStatus(randNum,type);
            }else if(RopeLockService.ACTION_LOCK_NOTIFY_DATA.equals(intent.getAction())){
                 byte[] notifyData = intent.getByteArrayExtra(RopeLockService.EXTRA_LOCK_NOTIFY_DATA);
                onBLECallbackNotifyData(notifyData);
            }
        }
    };

    protected  void onBLECallbackMapKeyStatus(int randNum, int type){

    }
    protected  void onBLECallbackNotifyData(byte[] notifyData){

    }

}
