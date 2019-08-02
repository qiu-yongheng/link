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
import com.omni.ble.library.service.BikeLockService;


/**
 * 3in1 马蹄锁 基础操作activity<br />
 * created by solo at 2019/05/11 21:55.
 */

public class BaseBikeLockServiceActivity extends Activity {
    private static final String TAG= BaseBikeLockServiceActivity.class.getSimpleName();

    protected BikeLockService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((BikeLockService.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 马蹄锁 的服务器绑定 回调
     * @param mBLEService 蓝牙操作服务器对象
     */
    protected  void onServiceConnectedCallBack(BikeLockService mBLEService){

    }

    /**
     *
     * @param deviceAddress 扫描的MAC地址
     * @param scanPeriod  扫描时长(毫秒数)
     */
    protected void startScanBLEDevice(String deviceAddress, int scanPeriod){
        mService.startScanBLEDevice(  deviceAddress,  scanPeriod);
    }

    /**
     * 停止扫描设备
     */
    protected void stopScanBLEDevice( ){
        mService.stopScanBLEDevice(  );
    }
    /**
     * 发送获取旧数据指令
     */
    protected  void sendGetOldData(){
        mService.sendGetOldDataCommand();

    }

    /**
     * 清空旧数据
     */
    protected  void sendGetClearOldData(){
        mService.sendGetClearOldDataCommand();

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
     * @param scanBLEDevice 扫描到的对象
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice){  }
    /**
     * 指令扫描周期，没有扫描的指令BLE设备的回调
     * @param deviceAddress 指令扫描的设备的MAC地址
     */
    protected  void onScanBLEDeviceNotCallBack(String deviceAddress){  }


    /**
     * 连接 纯蓝牙锁
     * @param mac OGBL1 Mac  纯蓝牙锁 MAC地址
     */
    protected  void connectBikeLock(String mac){
        mService.connect(mac);
    }
    protected void disconnectBikeLock(){
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


    /**
     * 获取固件信息
     */
    protected  void sendGetFirmwareInfo(){
        mService.sendGetFirmwareInfo();
    }

    protected  void sendGetFirmwareInfoDetail(int npack,byte deviceType){
        mService.sendGetFirmwareInfoDetail(  npack,  deviceType);
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


        intentFilter.addAction(BikeLockService.ACTION_BIKE_LOCK_OPEN);
        intentFilter.addAction(BikeLockService.ACTION_BIKE_LOCK_CLOSE);
        intentFilter.addAction(BikeLockService.ACTION_BIKE_LOCK_INFO);
        intentFilter.addAction(BikeLockService.ACTION_BLE_BIKE_LOCK_FW_INFO);
        intentFilter.addAction(BikeLockService.ACTION_BLE_BIKE_LOCK_FW_INFO_ING);
        intentFilter.addAction(BikeLockService.ACTION_BIKE_LOCK_CLEAR_OLD_DATA);
        intentFilter.addAction(BikeLockService.ACTION_BIKE_LOCK_OLD_DATA);

        intentFilter.addAction(BikeLockService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(BaseService.ACTION_BLE_SCAN_DEVICE_NOT);


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this, BikeLockService.class);
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
     * @param ckey 蓝牙通信KEY
     */
    protected  void onBLEGetKey(String mac, byte ckey){

        Log.i(TAG, "onBLEGetKey: mac="+mac+",ckey="+ckey);
    }
    /**
     * 注册完通知的回调,在主线程中回调。
     */
    protected  void onBLEWriteNotify(){

    }

    /**
     * 开锁接口的回调
     * @param status ,0开锁成功，1开锁失败
     */
    protected  void onBLEOpenCallback(int status,long timestamp){


    }


    /**
     * 关锁的回调
     * @param status 0关锁成功，1关锁失败
     * @param openId 开锁序列号,开锁时间戳
     * @param openTime 骑行时间，开锁时间
     */
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
     *
     * @param status 开锁锁状态，0开锁，1关锁
     * @param power 马蹄锁电量(0.1V)
     * @param old 是否有旧数据
     * @param timestamp 开锁时间戳
     * @param bikePower 电动车电池电量
     */
    protected  void onBLEBikeInfoCallback(int status,int power,int old,long timestamp,int bikePower){


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

    /**
     *
     * @param status  状态，0成功，1失败
     */
    protected  void onBLEClearDataCallback(int status ){
        Log.i(TAG, "onBLEClearDataCallback: status="+status);
    }


    /**
     * 获取旧数据
     * @param uid 用户id
     * @param timestamp 时间戳
     * @param openTime 骑行时间
     */
    protected  void onBLEOldDataCallback(long uid,long timestamp,long openTime ){
        Log.i(TAG, "onBLEOldDataCallback: uid="+uid);
        Log.i(TAG, "onBLEOldDataCallback: timestamp="+timestamp);
        Log.i(TAG, "onBLEOldDataCallback: openTime="+openTime);
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
            }else if(BikeLockService.ACTION_BIKE_LOCK_OPEN.equals(intent.getAction())){

                int status =intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra("timestamp",0L);
                onBLEOpenCallback(status,timestamp);
            }else if(BikeLockService.ACTION_BIKE_LOCK_CLOSE.equals(intent.getAction())){
                int status =intent.getIntExtra("status",0);
                long openId =intent.getLongExtra("openTimestamp",0L);
                long openTime =intent.getLongExtra("openTime",0L);
                onBLECloseCallback(status,openId,openTime);

            }else if(BikeLockService.ACTION_BIKE_LOCK_INFO.equals(intent.getAction())){

                int lockStatus = intent.getIntExtra(BikeLockService.EXTRA_LOCK_STATUS,0);
                int power = intent.getIntExtra(BikeLockService.EXTRA_LOCK_POWER,0);
                int old = intent.getIntExtra(BikeLockService.EXTRA_LOCK_OLD,1);
                int bikePower = intent.getIntExtra(BikeLockService.EXTRA_BIKE_POWER,0);
                long timestamp = intent.getLongExtra(BikeLockService.EXTRA_LOCK_INFO_TIMESTAMP,1L);
                onBLEInfoCallback(lockStatus,power,old,timestamp);

                onBLEBikeInfoCallback(lockStatus,power,old,timestamp,bikePower);
            }
            else if(BikeLockService.ACTION_BLE_BIKE_LOCK_FW_INFO.equals(intent.getAction())){
                String fwData = intent.getStringExtra(BikeLockService.EXTRA_FIRMWARE_DATA);
                onBLEBikeLockFirmwareData(fwData);

            }else if(BikeLockService.ACTION_BLE_BIKE_LOCK_FW_INFO_ING.equals(intent.getAction())){
                int pack = intent.getIntExtra(BikeLockService.EXTRA_FIRMWARE_NPACK,0);
                int total = intent.getIntExtra(BikeLockService.EXTRA_FIRMWARE_TOTAL_PACK,0);
                onBLEBikeLockFirmwareDataIng(pack,total);

            }else if(BikeLockService.ACTION_BIKE_LOCK_OLD_DATA.equals(intent.getAction())){

                long timestamp = intent.getLongExtra(BikeLockService.EXTRA_LOCK_OLD_TIMESTAMP,0L);
                long openTime = intent.getLongExtra(BikeLockService.EXTRA_LOCK_OLD_TIME,0L);
                long uid = intent.getLongExtra(BikeLockService.EXTRA_LOCK_OLD_UID,0L);

                onBLEOldDataCallback(uid,timestamp,openTime);
            }else if(BikeLockService.ACTION_BIKE_LOCK_CLEAR_OLD_DATA.equals(intent.getAction())){
                int  status = intent.getIntExtra(BikeLockService.EXTRA_LOCK_CLEAR_STATUS,0);
                onBLEClearDataCallback(status);
            }
        }
    };





    /**
     * 固件信息，当前获取到的固件信息
     * @param fwData
     */
    protected void onBLEBikeLockFirmwareData(String fwData){

    }

    /**
     * 正在获取固件，当前返回的是第几包数据
     * @param pack 当前iot返回的固件包数
     */
    protected void onBLEBikeLockFirmwareDataIng(int pack,int total){
        // 默认去获取下一包数据
        sendGetFirmwareInfoDetail(pack+1,(byte)0xA1);
    }
}
