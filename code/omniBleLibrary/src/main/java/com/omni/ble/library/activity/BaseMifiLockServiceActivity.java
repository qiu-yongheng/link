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
import com.omni.ble.library.service.BoxLockService;
import com.omni.ble.library.service.CarportService;
import com.omni.ble.library.service.KeyLockService;
import com.omni.ble.library.service.MifiLockService;

/**
 * 柜锁<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseMifiLockServiceActivity extends Activity {
    private static final String TAG=BaseMifiLockServiceActivity.class.getSimpleName();

    protected MifiLockService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((MifiLockService.LocalBinder)service).getService();
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
    protected  void onServiceConnectedCallBack(MifiLockService mBLEService){

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
     * 扫描到指定BEL设备的回调
     * @param scanBLEDevice
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice, byte[] record ){  }
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice  ){  }

    /**
     * 没有扫描到设备的回调
     * @param addressMac 指令要扫描的 柜锁MAC
     */
    protected  void onScanBLEDeviceNotCallBack(String addressMac){}

    /**
     * 连接 mifi底座锁
     * @param mifiLockMac  mifi底座锁MAC地址
     */
    protected  void connectMifiLock(String mifiLockMac){
        mService.connect(mifiLockMac);
    }
    protected void disconnectMifiLock(){
//        Log.i(TAG, "onItemClick: service activity  断开连接的 操作线程="+Thread.currentThread().getName());
        mService.disconnect();
    }

    /**
     * 获取通信KEY 指令
     * @param deviceKey  设备KEY
     */
    protected  void sendGetKeyCommand(String deviceKey){
        mService.sendGetKey(deviceKey);
    }




    /**
     * 发送 MIFI锁 开锁指令
     */
    protected  void sendMifiLockOpenCommand(int uid,long timestamp){
        mService.sendMifiLockOpen(uid,timestamp);
    }

    /**
     * 发送 MIFI锁 关锁指令
     */
    protected  void sendMifiLockClsoeCommand(int uid,long timestamp){
        mService.sendMifiLockClose(uid,timestamp);
    }

    protected  void sendBoxLockLowOpen(boolean isOpen){
        if(isOpen){
            mService.sendBoxLockLowOpen((byte )1);
        }else{
            mService.sendBoxLockLowOpen((byte )0);
        }

    }

    /**
     * 获取 柜锁 信息指令
     */
    protected  void getBoxLockInfo(){
        mService.sendBoxLockInfo();
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

        intentFilter.addAction(BaseService.ACTION_BLE_OPT_GET_KEY_ERROR);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE_NOT);

        intentFilter.addAction(MifiLockService.ACTION_BLE_MIFI_LOCK_OPEN);
        intentFilter.addAction(MifiLockService.ACTION_BLE_MIFI_LOCK_CLOSE);
        intentFilter.addAction(MifiLockService.ACTION_BLE_MIFI_LOCK_INFO);
        intentFilter.addAction(MifiLockService.ACTION_BLE_MIFI_LOCK_LOW_STATUS);
        intentFilter.addAction(MifiLockService.ACTION_BLE_DISCONNECTED);



        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this,MifiLockService.class);
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




    /**
     * 柜锁，获取信息
     * @param voltage 电压
     * @param status 低电量开锁状态(1-开启，0-关闭)
     * @param version 版本
     */
    protected  void onBLEBoxLockInfoCallBack(int voltage,int status,String version){

        Log.i(TAG, "onBLEOpenCallBack: "+String.format("voltage=%s,status=%s,timestamp=%s",voltage,status,version));




    }

    /**
     * 柜锁 开锁回调
     * @param status
     * @param timestamp
     */
    protected  void onBLEOpenCallBack(int status, long timestamp){
        Log.i(TAG, "onBLEOpenCallBack: status="+status+",timestamp="+timestamp);

    }


    protected  void onBLEBoxLockLowStatusCallBack(int status){


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


    protected void onBLEMifiLockCloseCallBack(int status,long timestamp) {
        Log.i(TAG, "onBLEMifiLockCloseCallBack: status="+status+",timestamp="+timestamp);

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
            }else if(BoxLockService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            }  else if(BoxLockService.ACTION_BLE_OPT_GET_KEY_ERROR.equals(intent.getAction())){
                onBLEGetKeyError();
            }else if(KeyLockService.ACTION_BLE_COMMAND_ERROR.equals(intent.getAction())){
                int errorStatus = intent.getIntExtra(CarportService.EXTRA_ERROR_STATUS,0);
                onBLECommandError(errorStatus);
            }
            else if(MifiLockService.ACTION_BLE_MIFI_LOCK_OPEN.equals(intent.getAction())){
                // 开锁 广播
                int status = intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra(BoxLockService.EXTRA_TIMESTAMP,0L);
                onBLEOpenCallBack(status,timestamp);

            } else if(BaseService.ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BaseService.EXTRA_SCAN_DEVICE);
                byte[] record = intent.getByteArrayExtra(BaseService.EXTRA_SCAN_RECORD);
                onScanBLEDeviceCallBack(bluetoothDevice,record);
                onScanBLEDeviceCallBack(bluetoothDevice);
            }  else if(BaseService.ACTION_BLE_SCAN_DEVICE_NOT.equals(intent.getAction())){
                String addressMac = intent.getStringExtra(BaseService.EXTRA_DEVICE_MAC);
                onScanBLEDeviceNotCallBack(addressMac);
            }else if(MifiLockService.ACTION_BLE_MIFI_LOCK_INFO.equals(intent.getAction())){

                int voltage=intent.getIntExtra(BoxLockService.EXTRA_VOLTAGE,0);
                int status = intent.getIntExtra(BoxLockService.EXTRA_LOW_OPEN_STATUS,0);
                String version = intent.getStringExtra(BoxLockService.EXTRA_VERSION);
                onBLEBoxLockInfoCallBack( voltage, status,version);


            }else if(MifiLockService.ACTION_BLE_MIFI_LOCK_LOW_STATUS.equals(intent.getAction())){
                int status = intent.getIntExtra(BoxLockService.EXTRA_LOW_POWER_STATUS,0);

                onBLEBoxLockLowStatusCallBack( status);
            }else if(MifiLockService.ACTION_BLE_MIFI_LOCK_CLOSE.equals(intent.getAction())){

                int status = intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra(BoxLockService.EXTRA_TIMESTAMP,0L);
                onBLEMifiLockCloseCallBack(status,timestamp);
            }
        }
    };


}
