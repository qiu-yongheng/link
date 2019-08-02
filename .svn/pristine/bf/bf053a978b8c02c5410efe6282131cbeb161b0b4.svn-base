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
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.omni.ble.library.service.BaseService;
import com.omni.ble.library.service.UService;

/**
 * u 型锁 蓝牙<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */
public class BaseUServiceActivity extends AppCompatActivity {
    private static final String TAG= BaseUServiceActivity.class.getSimpleName();

    protected UService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((UService.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 U型锁 的服务器绑定 回调
     * @param mBLEService
     */
    protected  void onServiceConnectedCallBack(UService mBLEService){

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
    protected  void onScanBLEDeviceNotCallBack(String deviceAddress){  }


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


    /**
     * 发送升级固件指令
     * @param nPack 升级固件的总包数
     * @param crc   升级固件的总CRC16值
     * @param deviceType  设备类型(U型锁类型为0xD1)
     * @param updateKey 升级KEY(默认KEY为Vgz7)
     */
    protected  void sendUpdateFirmwareCommand(int nPack, int crc, byte deviceType, String updateKey){
        mService.sendUpdateFirmwareCommand(nPack,crc,deviceType,updateKey);
    }

    /**
     * APP发送每包升级固件数据
     * @param pack 第几包
     * @param data 该包升级固件的具体数据
     */
    protected  void sendTransmissionData(int pack,byte[] data){
        mService.sendUpgradeFwDetail(pack,data);
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


        intentFilter.addAction(UService.ACTION_OGBL2_OPEN);
        intentFilter.addAction(UService.ACTION_OGBL2_CLOSE);
        intentFilter.addAction(UService.ACTION_OBL2_INFO);


        intentFilter.addAction(UService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(UService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(UService.ACTION_BLE_SCAN_DEVICE_NOT);
        intentFilter.addAction(UService.ACTION_BLE_BIKE_LOCK_FW_INFO);
        intentFilter.addAction(UService.ACTION_BLE_BIKE_LOCK_FW_INFO_ING);
        intentFilter.addAction(UService.ACTION_BLE_TRANSMISSION_PACK);


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this,UService.class);
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
    protected  void onBLEGetKey(String mac, byte ckey){

        Log.i(TAG, "onBLEGetKey: mac="+mac+",ckey="+ckey);
    }
    /**
     * 注册完通知的回调,在主线程中回调。
     */
    protected  void onBLEWriteNotify(){
        Log.i(TAG, "onBLEWriteNotify: write notify thread name="+ Thread.currentThread().getName());

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
            }else if(UService.ACTION_OGBL2_OPEN.equals(intent.getAction())){

                int status =intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra("timestamp",0L);
                onBLEOpenCallback(status,timestamp);
            }else if(UService.ACTION_OGBL2_CLOSE.equals(intent.getAction())){
                int status =intent.getIntExtra("status",0);
                long openId =intent.getLongExtra("openTimestamp",0L);
                long openTime =intent.getLongExtra("openTime",0L);
                onBLECloseCallback(status,openId,openTime);

            }else if(UService.ACTION_OBL2_INFO.equals(intent.getAction())){

                int lockStatus = intent.getIntExtra(UService.EXTRA_LOCK_STATUS,0);
                int power = intent.getIntExtra(UService.EXTRA_LOCK_POWER,0);
                int old = intent.getIntExtra(UService.EXTRA_LOCK_OLD,1);
                long timestamp = intent.getLongExtra(UService.EXTRA_LOCK_INCO_TIMESTAMP,1L);
                onBLEInfoCallback(lockStatus,power,old,timestamp);
            }
            else if(UService.ACTION_BLE_BIKE_LOCK_FW_INFO.equals(intent.getAction())){
                String fwData = intent.getStringExtra(UService.EXTRA_FIRMWARE_DATA);
                onBLEBikeLockFirmwareData(fwData);

            }else if(UService.ACTION_BLE_BIKE_LOCK_FW_INFO_ING.equals(intent.getAction())){
                int pack = intent.getIntExtra(UService.EXTRA_FIRMWARE_NPACK,0);
                onBLEBikeLockFirmwareDataIng(pack);

            }else if(UService.ACTION_BLE_TRANSMISSION_PACK.equals(intent.getAction())){
                int pack = intent.getIntExtra(UService.EXTRA_TRANSMISSION_PACK_PACK,0);
                int deviceType = intent.getIntExtra(UService.EXTRA_TRANSMISSION_PACK_TYPE,0);
                onBLEBikeLockTransmission(pack,deviceType);

            }
        }
    };

    /**
     * 回调-U型锁升级固件数据传输请求
     * @param pack  请求传输的第几包数据
     * @param type  请求传输的类型
     */
    protected  void onBLEBikeLockTransmission(int pack, int type){
        Log.i(TAG, "onBLEULockTransmission: pack="+pack);
        Log.i(TAG, "onBLEULockTransmission: type="+type);

    }


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
    protected void onBLEBikeLockFirmwareDataIng(int pack){
        // 默认去获取下一包数据
        sendGetFirmwareInfoDetail(pack+1,(byte)0xD1);
    }
}
