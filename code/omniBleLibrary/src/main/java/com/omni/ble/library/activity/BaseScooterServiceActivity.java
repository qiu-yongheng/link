package com.omni.ble.library.activity;


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
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.omni.ble.library.service.BaseService;
import com.omni.ble.library.service.CarportService;
import com.omni.ble.library.service.ScooterService;

import java.util.List;

/**
 * 滑板车<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseScooterServiceActivity extends AppCompatActivity {
    private static final String TAG=BaseScooterServiceActivity.class.getSimpleName();
    protected ScooterService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((ScooterService.LocalBinder)service).getService();
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
    protected  void onServiceConnectedCallBack(ScooterService mBLEService){

    }





    /**
     * 连接滑板车
     * @param scooterMac  滑板车锁MAC地址
     */
    protected  void connectScooter(String scooterMac){
        mService.connect(scooterMac);
    }

    /**
     * 获取通信KEY 指令
     * @param deviceKey  设备KEY
     */
    protected  void sendGetKeyCommand(String deviceKey){
        mService.sendGetKey(deviceKey);
    }
    /**
     * 获取通信KEY 指令
     * @param deviceKey  设备KEY
     */
    protected  void sendGetKeyCommand(byte[] deviceKey){
        mService.sendGetKey(deviceKey);
    }
    /**
     * 发送 开锁回复指令
     */
    protected  void sendOpenResponseCommand( ){
        mService.sendScooterOpenResponse();
    }

    /**
     * 发送 锁回复指令
     */
    protected  void sendCloseResponseCommand( ){
        mService.sendScooterCloseResponse();
    }

    /**
     * 获取锁设备信息指令
     */
    protected  void sendGetDeviceInfo(){

        mService.sendDeviceInfo();
    }

    /**
     * 获取锁设备信息指令
     */
    protected  void sendGetScooterInfo(){

        mService.sendScooterInfo();
    }

    /**
     * 发送滑板车关锁指令
     */
    protected  void sendScooterCloseCommand(){
        mService.sendScooterClose();
    }



    /**
     * 发送滑板车开锁指令
     * @param uid 用户ID
     */
    protected  void sendScooterOpenCommand(int uid){
        mService.sendScooterOpen(uid,(byte)1);
    }

    /**
     *发送滑板车开锁指令
     * @param uid  用户ID
     * @param timestamp 开锁时间戳
     */
    protected  void sendScooterOpenCommand(int uid, long timestamp){
        mService.sendScooterOpen(uid,(byte)1,timestamp);
    }

    protected  void sendTransmissionData(int pack,byte[] data){
        mService.sendTransmissionData(pack,data);
    }

    protected  void sendLogInfo(){
        mService.sendLogInfo((byte)0x8A);
    }
    protected  void sendLogInfo(byte deviceType){
        mService.sendLogInfo((byte)deviceType);
    }

    protected  void sendNoLogInfo(){
        mService.sendNoLogInfo();
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
     * 获取旧数据
     */
    protected  void sendGetOldData(){
        mService.sendGetOldData();
    }


    protected  void onBLEScooterFWData(int npack){

    }

    /**
     * 发送获取 RFID 卡信息
     */
    protected  void sendReadCardId(){
        mService.sendReadCardId();
    }

    /**
     * 弹出电池
     */
    protected  void sendPopUp(){
        mService.sendGetPopUp();
    }

    /**
     * 控制滑板车 附加设备如电池盖，钢缆等。
     * @param optType
     */
    protected  void sendExtraDevice(byte optType){
        mService.sendExtraDevice(optType);
    }

    /**
     * 滑板车整体关机
     */
    protected  void sendPowerControl(byte opt){
        mService.sendShutDown(opt);
    }

    protected  void sendPowerControl(byte deviceType,byte opt){
        mService.sendShutDown(deviceType,opt);
    }

    protected  void sendGetFirwareInfoDetail(int pack ,byte deviceType){

        mService.sendGetFirmwareInfoDetail(pack,deviceType);
    }

    /**
     *
     * @param light  2-开启,1-关闭
     * @param speed  1-低速,2-中速,3-高速
     * @param you    2 -开启油门响应
     * @param tLight 2-开启尾灯闪烁
     */
    protected  void sendScooterSet(int light,int speed,int you,int tLight){
        mService.sendScooterSet((byte)light,(byte)speed,(byte)you,(byte)tLight);
    }
    protected  void sendScooterSet(byte light,byte speed,byte you,byte tLight){
        mService.sendScooterSet(light,speed,you,tLight);
    }

    /**
     * 控制彩灯
     * @param isOpen
     * @param color
     * @param speed
     * @param brightness
     * @param effect
     */
    protected void sendColorLight(int isOpen, int color, int speed, int brightness, int effect) {
        mService.sendColorLight(isOpen, color, speed, brightness, effect);
    }


    /**
     * 清除旧数据
     */
    protected  void sendClearOldData(){
        mService.sendClearOldData();
    }

    /**
     * 发送断开 滑板车连接信息
     */
    protected  void sendDisconnectScooter( ){
        mService.disconnect();
    }

    /**
     * 扫描要连接的蓝牙设备
     * @param deviceAddress  设备的蓝牙地址
     * @param scanPeriod 扫描的时长，单位 毫秒数
     */
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
        registerLocalReceiver();
    }

    protected void registerLocalReceiver(){
        Log.i(TAG, "onStart: 注册本地广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScooterService.ACTION_BLE_GET_KEY);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_OPEN);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_CLOSE);

        intentFilter.addAction(ScooterService.ACTION_BLE_WRITE_NOTIFY);
        intentFilter.addAction(ScooterService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_INFO);
        intentFilter.addAction(ScooterService.ACTION_BLE_DEVICE_INFO);
        intentFilter.addAction(ScooterService.ACTION_BLE_COMMAND_ERROR);
        intentFilter.addAction(ScooterService.ACTION_BLE_GET_KEY_ERROR);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCAN_DEVICE);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCAN_DEVICE_NOT);
        intentFilter.addAction(ScooterService.ACTION_BLE_LOG_DATA);

        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_FW_INFO);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_OLD_DATA);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_CLEAR_OLD_DATA);
        intentFilter.addAction(ScooterService.ACTION_BLE_TRANSMISSION_PACK);
        intentFilter.addAction(ScooterService.EXTRA_RFID_READ_STATUS);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_READ_ID);
        intentFilter.addAction(ScooterService.ACTION_BLE_STATE_ON);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_FW_INFO_ING);
        intentFilter.addAction(ScooterService.ACTION_BLE_CONTROL_EXTR_DEVICE);
        intentFilter.addAction(ScooterService.ACTION_BLE_SCOOTER_SET);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Intent gattServiceIntent=new Intent(this,ScooterService.class);
        //启动服务
        startService(gattServiceIntent);
        //绑定服务。绑定服务的目的是和服务进行通信
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    protected  void unRegisterReceiver(){
        Log.i(TAG, "onStop: 注销本地广播");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unRegisterReceiver();
    }


    /**
     * 车位锁  拦车后的回到
     * @param status  拦车状态
     * @param timestamp 时间戳
     * @param time 不拦车时间
     */
    protected  void onBLEScooterCloseCallBack(int status, long timestamp, long time){

        Log.i(TAG, "onBLEScooterCloseCallBack: "+String.format("status=%s,timestamp=%s,time=%s",status,timestamp,time));
        if(status==0){
//            Toast.makeText(BaseCarportServiceActivity.this, "开始拦车", Toast.LENGTH_SHORT).show();
        }else if(status==1){
//            Toast.makeText(BaseCarportServiceActivity.this, "拦车成功", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(BaseScooterServiceActivity.this, "拦车超时", Toast.LENGTH_SHORT).show();
        }
    }

    protected  void onBLEScooterOpenCallBack(int status, long timestamp){
        Log.i(TAG, "onBLEScooterOpenCallBack: status="+status+",timestamp="+timestamp);
        if(status==0){
//            Toast.makeText(BaseCarportServiceActivity.this, "开始不拦车", Toast.LENGTH_SHORT).show();
        }else if(status==1){
//            Toast.makeText(BaseCarportServiceActivity.this, "不拦车成功", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(BaseScooterServiceActivity.this, "不拦车超时", Toast.LENGTH_SHORT).show();
        }
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


    protected  void onBLEScooterFirmwareData(String  fwData){  }

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
    protected  void onBLEDeviceInfo(int  voltage, int status, String version){
        
        Log.i(TAG, "onBLEDeviceInfo: "+String.format("v=%s,s=%s,v=%s",voltage,status,version));

        if((status&0x01)!=0){
            Log.i(TAG, "onBLEDeviceInfo:  开锁状态");
        }
        if((status&0x02)!=0){
            Log.i(TAG, "onBLEDeviceInfo:  关锁状态");
        }
        if((status&0x40)!=0){
            Log.i(TAG, "onBLEDeviceInfo:  有旧数据");
        }
    }

    protected  void onBLEScooterInfo(int  power, int speedMode, int speed,int mileage,int prescientMileage){


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


    protected  void onBLEScooterTransmission(int pack, int type){

    }

    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: all  action="+intent.getAction());
            if(ScooterService.ACTION_BLE_GET_KEY.equals(intent.getAction()) ){
                String mac=intent.getStringExtra("mac");
                byte bkey=intent.getByteExtra("key",(byte)0);


                onBLEGetKey();
                onBLEGetKey(mac,bkey);
            }else if( ScooterService.ACTION_BLE_WRITE_NOTIFY.equals(intent.getAction())){
              //  Log.i(TAG, "onReceive: write notify thread name="+Thread.currentThread().getName());
                onBLEWriteNotify();
            } else if(ScooterService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            } else if(ScooterService.ACTION_BLE_DEVICE_INFO.equals(intent.getAction())){
                Log.i(TAG, "onReceive:info  thread name="+Thread.currentThread().getName());
                int voltage = intent.getIntExtra(ScooterService.EXTRA_VOLTAGE,0);
                int status = intent.getIntExtra(ScooterService.EXTRA_CARPORT_STATUS,0);
                String version = intent.getStringExtra(ScooterService.EXTRA_VERSION);

                onBLEDeviceInfo(voltage,status,version);

            }else if(ScooterService.ACTION_BLE_SCOOTER_INFO.equals(intent.getAction())){
                int power = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_POWER,0);
                int speedMode = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_SPEED_MODE,0);
                int speed = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_SPEED,0);
                int mileage = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_MILEAGE,0);
                int prescientMileage = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_PRESCIENT_MILEAGE,0);

                onBLEScooterInfo(power,speedMode,speed,mileage,prescientMileage);
            } else if(ScooterService.ACTION_BLE_GET_KEY_ERROR.equals(intent.getAction())){
                onBLEGetKeyError();
            }else if(ScooterService.ACTION_BLE_COMMAND_ERROR.equals(intent.getAction())){
                int errorStatus = intent.getIntExtra(ScooterService.EXTRA_ERROR_STATUS,0);


                onBLECommandError(errorStatus);
            }
            else if(ScooterService.ACTION_BLE_SCOOTER_OPEN.equals(intent.getAction())){
                int status = intent.getIntExtra(ScooterService.EXTRA_CARPORT_STATUS,0);
                long timestamp = intent.getLongExtra(ScooterService.EXTRA_TIMESTAMP,0L);
                onBLEScooterOpenCallBack(status,timestamp);

            }
            else if(ScooterService.ACTION_BLE_SCOOTER_CLOSE.equals(intent.getAction())){
                int status = intent.getIntExtra(ScooterService.EXTRA_CARPORT_STATUS,0);
                long timestamp = intent.getLongExtra(ScooterService.EXTRA_TIMESTAMP, 0L);
                long time = intent.getLongExtra(ScooterService.EXTRA_TIME,0L);
                onBLEScooterCloseCallBack(status, timestamp, time);

            }  else if(ScooterService.ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BaseService.EXTRA_SCAN_DEVICE);
                onScanBLEDeviceCallBack(bluetoothDevice);
            }
            else if(ScooterService.ACTION_BLE_SCAN_DEVICE_NOT.equals(intent.getAction())){
                String addressMac = intent.getStringExtra(BaseService.EXTRA_DEVICE_MAC);
                onScanBLEDeviceNotCallBack(addressMac);
            } else if(ScooterService.ACTION_BLE_SCOOTER_FW_INFO.equals(intent.getAction())){
                String fwData = intent.getStringExtra(CarportService.EXTRA_FIRMWARE_DATA);
                onBLEScooterFirmwareData(fwData);
            }else if(ScooterService.ACTION_BLE_TRANSMISSION_PACK.equals(intent.getAction())){
                int pack = intent.getIntExtra(CarportService.EXTRA_TRANSMISSION_PACK_PACK,0);
                int type = intent.getIntExtra(CarportService.EXTRA_TRANSMISSION_PACK_TYPE,0);
                onBLEScooterTransmission(  pack,  type);
            }else if(ScooterService.ACTION_BLE_LOG_DATA.equals(intent.getAction())){
                byte[] data = intent.getByteArrayExtra("data");

                onBLEScooterLogData(data);
            }else if(ScooterService.ACTION_BLE_SCOOTER_OLD_DATA.equals(intent.getAction())){
                long timestamp = intent.getLongExtra(ScooterService.EXTRA_SCOOTER_OLD_TIMESTAMP, 0L);
                long openTime = intent.getLongExtra(ScooterService.EXTRA_SCOOTER_OLD_OPENTIME,0L);
                long userId = intent.getLongExtra(ScooterService.EXTRA_SCOOTER_OLD_USERID,0L);

                onBLEScooterOldData(timestamp,openTime,userId);
            }else if(ScooterService.ACTION_BLE_SCOOTER_CLEAR_OLD_DATA.equals(intent.getAction())){
                int status =  intent.getIntExtra(ScooterService.EXTRA_SCOOTER_CLEAR_OLD_STATUS, 0);
                onBLEScooterClearOldData(status);
            }else if (ScooterService.ACTION_BLE_STATE_ON.equals(intent.getAction())){
                onSystemBLEOpen();
            }else if (ScooterService.ACTION_BLE_SCOOTER_READ_ID.equals(intent.getAction())){
                int status = intent.getIntExtra(ScooterService.EXTRA_RFID_READ_STATUS,0);
                String cardId = intent.getStringExtra(ScooterService.EXTRA_RFID_READ_ID);
                onBLEScooterReadId(status,cardId);
            }else if (ScooterService.ACTION_BLE_CONTROL_EXTR_DEVICE.equals(intent.getAction())){
                int controlType = intent.getIntExtra(ScooterService.EXTRA_EXTR_DEVICE_TYPE,0);
                int status= intent.getIntExtra(ScooterService.EXTRA_EXTR_DEVICE_STATUS,0);
                onBLEControlExtraDevice(controlType,status);
            }else if(ScooterService.ACTION_BLE_SCOOTER_FW_INFO_ING.equals(intent.getAction())){
                int pack = intent.getIntExtra(ScooterService.EXTRA_FIRMWARE_NPACK,0);
                onBLEScooterFWData(pack);
            }else if(ScooterService.ACTION_BLE_SCOOTER_SET.equals(intent.getAction())){
                int setStatus = intent.getIntExtra(ScooterService.EXTRA_SCOOTER_SET_STATUS,0);
                onBLEScooterSet(setStatus);
            }


        }
    };


    /**
     * 滑板车设置的回调
     * @param setStatus 设置状态，0-成功，1-失败。
     */
    protected  void onBLEScooterSet(int setStatus){

    }

    protected void onBLEScooterReadId(int status ,String cardId){

    }


    /**
     * 外部设备操作的回调
     * @param controlType  操作类型 1：解锁电池
     * @param status 返回状态，0：成功，1：失败
     */
    protected void onBLEControlExtraDevice(int controlType, int status){

    }




    /**
     * 新增系统蓝牙开启
     */
    protected void onSystemBLEOpen(){

    }

    protected void onBLEScooterOldData(long timestamp, long openTime, long userId) {

    }

    protected void onBLEScooterLogData(byte[] data) {

    }

    protected void onBLEScooterClearOldData(int status) {

    }

}
