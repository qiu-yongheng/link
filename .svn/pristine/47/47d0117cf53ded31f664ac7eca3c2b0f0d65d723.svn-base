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

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.omni.ble.library.service.BaseService;
import com.omni.ble.library.service.CarportService;

import java.util.List;

/**
 * 车位锁<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseCarportServiceActivity extends AppCompatActivity {
    private static final String TAG=BaseCarportServiceActivity.class.getSimpleName();
    protected CarportService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((CarportService.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 车位锁的服务器绑定 回调
     * @param mBLEService
     */
    protected  void onServiceConnectedCallBack(CarportService mBLEService){
    }

    /**
     * 连接车位锁
     * @param carportMac  车位锁MAC地址
     */
    protected  void connectCarport(String carportMac){
        mService.connect(carportMac);
    }

    /**
     * 获取通信KEY 指令
     * @param deviceKey  设备KEY
     */
    protected  void sendGetKeyCommand(String deviceKey){
        mService.sendGetKey(deviceKey);
    }

    /**
     * 获取设备信息指令
     */
    protected  void sendGetCarportInfo(){
        mService.sendDeviceInfo();
    }

    protected  void sendGetCarportPairInfo(){
        mService.sendGetPairInfo();
    }


    /**
     * 发送拦车指令
     */
    protected  void sendCarportUpCommand(){
        mService.sendCarportUp();
    }

    /**
     * 发送不拦车指令
     * @param uid 用户ID
     */
    protected  void sendCarportDownCommand(int uid){
        mService.sendCarportDown(uid,(byte)1);
    }

    /**
     *不拦车指令
     * @param uid  用户ID
     * @param timestamp 不拦车时间戳
     */
    protected  void sendCarportDownCommand(int uid,long timestamp){
        mService.sendCarportDown(uid,(byte)1,timestamp);
    }

    protected  void sendTransmissionData(int pack,byte[] data){
        mService.sendTransmissionData(pack,data);
    }
    /**
     * 发送不拦车指令，隔一段时间后，车位锁会自动拦车
     * @param uid 用户ID
     */
    protected  void sendCarportDownAutoCommand(int uid){
        mService.sendCarportDown(uid,(byte)3);
    }

    /**
     * 获取固件信息
     *
     */
    protected  void sendGetFirmwareInfo(){
        mService.sendGetFirmwareInfo();
    }

    protected  void sendUpdateFirmwareCommand(int nPack,int crc,byte deviceType,String updateKey){
        mService.sendUpdateFirmwareCommand(nPack,crc,deviceType,updateKey);
    }

    /**
     *
     * @param transmissionType  0-升级数据传输，1-系统配置数据传输
     * @param nPack
     * @param crc
     * @param deviceType
     * @param updateKey
     */
    protected  void sendUpdateTransmissionCommand(byte transmissionType,int nPack,int crc,byte deviceType,String updateKey){
        mService.sendUpdateTransmissionCommand(transmissionType,nPack,crc,deviceType,updateKey);
    }

    /**
     * 自动发送 不拦车指令
     * @param isStart
     */
    protected  void startAutoSendDown(int uid,boolean isStart){
        mService.startAutoSendDown(  uid,isStart);
    }
    protected  void stopAutoSendDown( ){
        mService.stopAutoSendDown(  );
    }

    /**
     * 修改车位锁 设备KEY
     * @param deviceKey
     */
    protected  void sendModifyDeviceKeyCommand(String deviceKey){
        mService.sendModifyDeviceKey(deviceKey);
    }

    protected  void sendLogInfo(){
        mService.sendLogInfo((byte)0x80);
    }

    protected void sendLogInfo(byte deviceType) {
        mService.sendLogInfo(deviceType);
    }


    /**
     * 车位锁配对遥控器指令，将遥控器地址写入到车位锁中。
     * @param MAC 遥控器MAC地址
     */
    protected  void sendPairRemoteCommand(byte[] MAC){
        mService.sendPairRemote(MAC);
    }

    protected  void sendClearDeviceKey( ){
        mService.sendClearDeviceKey( );
    }

    protected  void sendDisconnectCarport( ){
        mService.disconnect();
    }

    protected void startScanBLEDevice(String deviceAddress,int scanPeriod){
        mService.startScanBLEDevice(  deviceAddress,  scanPeriod);
    }
    protected void stopScanBLEDevice( ){
        mService.stopScanBLEDevice(  );
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
        intentFilter.addAction(CarportService.ACTION_BLE_GET_KEY);
        intentFilter.addAction(CarportService.ACTION_BLE_CARPORT_DOWN);
        intentFilter.addAction(CarportService.ACTION_BLE_CARPORT_UP);
        intentFilter.addAction(CarportService.ACTION_BLE_GET_MAC);
        intentFilter.addAction(CarportService.ACTION_BLE_WRITE_NOTIFY);
        intentFilter.addAction(CarportService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(CarportService.ACTION_BLE_CARPORT_INFO);
        intentFilter.addAction(CarportService.ACTION_BLE_MODIFY_KEY);
        intentFilter.addAction(CarportService.ACTION_BLE_COMMAND_ERROR);
        intentFilter.addAction(CarportService.ACTION_BLE_GET_KEY_ERROR);
        intentFilter.addAction(CarportService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(CarportService.ACTION_BLE_SCAN_DEVICE_NOT);
        intentFilter.addAction(CarportService.ACTION_BLE_PAIR_STATUS);
        intentFilter.addAction(CarportService.ACTION_BLE_PAIR_INFO);
        intentFilter.addAction(CarportService.ACTION_BLE_CARPORT_FW_INFO);
        intentFilter.addAction(CarportService.ACTION_BLE_TRANSMISSION_PACK);
        intentFilter.addAction(CarportService.ACTION_BLE_LOG_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);
    }

    protected void startBindService(){
        Intent gattServiceIntent=new Intent(this,CarportService.class);
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
     * 车位锁  拦车后的回到
     * @param status  拦车状态
     * @param timestamp 时间戳
     * @param time 不拦车时间
     */
    protected  void onBLEUpCallBack(int status,long timestamp,long time){

        Log.i(TAG, "onBLEOpenCallBack: "+String.format("status=%s,timestamp=%s,time=%s",status,timestamp,time));
        if(status==0){
//            Toast.makeText(BaseCarportServiceActivity.this, "开始拦车", Toast.LENGTH_SHORT).show();
        }else if(status==1){
//            Toast.makeText(BaseCarportServiceActivity.this, "拦车成功", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(BaseCarportServiceActivity.this, "拦车超时", Toast.LENGTH_SHORT).show();
        }
    }

    protected  void onBLEDownCallBack(int status,long timestamp){
        Log.i(TAG, "onBLEOpenCallBack: status="+status+",timestamp="+timestamp);
        if(status==0){
//            Toast.makeText(BaseCarportServiceActivity.this, "开始不拦车", Toast.LENGTH_SHORT).show();
        }else if(status==1){
//            Toast.makeText(BaseCarportServiceActivity.this, "不拦车成功", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(BaseCarportServiceActivity.this, "不拦车超时", Toast.LENGTH_SHORT).show();
        }
    }

    protected  void onBLEPairMacCallBack(long MacLong){

    }

    /**
     * 扫描到指定BEL设备的回调
     * @param scanBLEDevice
     */
    protected  void onScanBLEDeviceCallBack(BluetoothDevice scanBLEDevice){  }
    /**
     * 指令扫描周期，没有扫描的指令BLE设备的回调
     * @param deviceAddress 指令扫描的设备的MAC地址
     */
    protected  void onScanBLEDeviceNotCallBack(String  deviceAddress){  }

    /**
     * 获取 车位锁已经配对的遥控器的MAC地址
     * @param status 状态
     * @param mac 遥控器MAC地址
     */
    protected  void onBLECarportPairRemoteMacCallBack(int status, String  mac){  }
    protected  void onBLECarportFirmwareData(  String  fwData){  }
    /**
     * 车位锁配对遥控器状态，在主线程中回调
     * @param status 配对状态
     */
    protected  void onBLEPairStatus(int  status){

    }
    protected  void onBLEModifyKeyStatus(int  status){

    }


    /**
     * 获取到所有连接了的设备
     * @return 连接了的设备列表
     */
    protected List<BluetoothDevice> getConnectedDevices(){

        return mService.getConnectedDevices();
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
     * 车位锁信息的回调
     * @param voltage  电压值
     * @param status   状态值
     * @param version  硬件版本信息
     */
    protected  void onBLECarportInfo(int  voltage,int status,String version){
        
        Log.i(TAG, "onBLEKeyLockInfo: "+String.format("v=%s,s=%s,v=%s",voltage,status,version));

        if((status&0x01)!=0){
            Log.i(TAG, "onBLEKeyLockInfo:  不拦车状态");
        }
        if((status&0x02)!=0){
            Log.i(TAG, "onBLEKeyLockInfo:  拦车状态");
        }
        if((status&0x40)!=0){
            Log.i(TAG, "onBLEKeyLockInfo:  有旧数据");
        }
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
     * @param communicationKey 通信KEY
     */
    protected  void onBLEGetKey(String mac,byte communicationKey){

        Log.i(TAG, "onBLEGetKey: mac="+mac);
    }
    /**
     * 注册网通知的回调,在主线程中回调。
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


    protected  void onBLECarportTransmission(int pack,int type){

    }

    protected  void onBLECarportLogData(byte[] data){


    }

    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: all  action="+intent.getAction());
            if(CarportService.ACTION_BLE_GET_KEY.equals(intent.getAction()) ){
                String mac=intent.getStringExtra("mac");
                byte bkey=intent.getByteExtra("key",(byte)0);


                onBLEGetKey();
                onBLEGetKey(mac,bkey);
            }else if( CarportService.ACTION_BLE_WRITE_NOTIFY.equals(intent.getAction())){
              //  Log.i(TAG, "onReceive: write notify thread name="+Thread.currentThread().getName());
                onBLEWriteNotify();
            }else if(CarportService.ACTION_BLE_GET_MAC.equals(intent.getAction())){
                Long MacLong = intent.getLongExtra(CarportService.EXTRA_MAC,0L);

                onBLEPairMacCallBack(  MacLong);
            }else if(CarportService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            }else if(CarportService.ACTION_BLE_PAIR_STATUS.equals(intent.getAction())){

                int status = intent.getIntExtra(CarportService.EXTRA_STATUS,0);


                onBLEPairStatus(status);
            }else if(CarportService.ACTION_BLE_CARPORT_INFO.equals(intent.getAction())){
                Log.i(TAG, "onReceive:info  thread name="+Thread.currentThread().getName());
                int voltage = intent.getIntExtra(CarportService.EXTRA_VOLTAGE,0);
                int status = intent.getIntExtra(CarportService.EXTRA_CARPORT_STATUS,0);
                String version = intent.getStringExtra(CarportService.EXTRA_VERSION);

                onBLECarportInfo(voltage,status,version);

            }else if(CarportService.ACTION_BLE_MODIFY_KEY.equals(intent.getAction())){
                int status = intent.getIntExtra(CarportService.EXTRA_MODIFY_KEY_STATUS,0);

                onBLEModifyKeyStatus(status);
            }else if(CarportService.ACTION_BLE_GET_KEY_ERROR.equals(intent.getAction())){
                onBLEGetKeyError();
            }else if(CarportService.ACTION_BLE_COMMAND_ERROR.equals(intent.getAction())){
                int errorStatus = intent.getIntExtra(CarportService.EXTRA_ERROR_STATUS,0);


                onBLECommandError(errorStatus);
            }
            else if(CarportService.ACTION_BLE_CARPORT_DOWN.equals(intent.getAction())){
                int status = intent.getIntExtra(CarportService.EXTRA_CARPORT_STATUS,0);
                long timestamp = intent.getLongExtra(CarportService.EXTRA_TIMESTAMP,0L);
                onBLEDownCallBack(status,timestamp);

            }
            else if(CarportService.ACTION_BLE_CARPORT_UP.equals(intent.getAction())){
                int status = intent.getIntExtra(CarportService.EXTRA_CARPORT_STATUS,0);
                long timestamp = intent.getLongExtra(CarportService.EXTRA_TIMESTAMP, 0L);
                long time = intent.getLongExtra(CarportService.EXTRA_TIME,0L);
                onBLEUpCallBack(status, timestamp, time);

            }  else if(CarportService.ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BaseService.EXTRA_SCAN_DEVICE);
                onScanBLEDeviceCallBack(bluetoothDevice);
            }
            else if(CarportService.ACTION_BLE_SCAN_DEVICE_NOT.equals(intent.getAction())){
                String addressMac = intent.getStringExtra(BaseService.EXTRA_DEVICE_MAC);
                onScanBLEDeviceNotCallBack(addressMac);
            }else if(CarportService.ACTION_BLE_PAIR_INFO.equals(intent.getAction())){

                String addressMac = intent.getStringExtra(CarportService.EXTRA_PAIR_REMOTE_MAC);
                int status = intent.getIntExtra(CarportService.EXTRA_STATUS,0);
                onBLECarportPairRemoteMacCallBack(status,addressMac);
            }else if(CarportService.ACTION_BLE_CARPORT_FW_INFO.equals(intent.getAction())){
                String fwData = intent.getStringExtra(CarportService.EXTRA_FIRMWARE_DATA);
                onBLECarportFirmwareData(fwData);
            }else if(CarportService.ACTION_BLE_TRANSMISSION_PACK.equals(intent.getAction())){
                int pack = intent.getIntExtra(CarportService.EXTRA_TRANSMISSION_PACK_PACK,0);
                int type = intent.getIntExtra(CarportService.EXTRA_TRANSMISSION_PACK_TYPE,0);

                onBLECarportTransmission(  pack,  type);
            }else if(CarportService.ACTION_BLE_LOG_DATA.equals(intent.getAction())){
                byte[] data = intent.getByteArrayExtra("data");
                onBLECarportLogData(data);
            }
        }
    };
}
