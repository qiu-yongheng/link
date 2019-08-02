package com.omni.ble.library.activity;


import android.app.Activity;
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
import com.omni.ble.library.service.CarportService;
import com.omni.ble.library.service.KeyLockService;

/**
 * 钥匙盒锁<br />
 * created by CxiaoX at 2017/12/22 11:55.
 */

public class BaseKeyLockServiceActivity extends Activity {
    private static final String TAG=BaseKeyLockServiceActivity.class.getSimpleName();
    protected KeyLockService mService=null;
    protected final ServiceConnection mServiceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService= ((KeyLockService.LocalBinder)service).getService();
            Log.i(TAG, "onServiceConnected: 获取到蓝牙Service 的绑定");
            onServiceConnectedCallBack(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 操作 钥匙锁的服务器绑定 回调
     * @param mBLEService
     */
    protected  void onServiceConnectedCallBack(KeyLockService mBLEService){

    }


    /**
     * 操作  连接锁
     * @param keyLockMac  钥匙盒锁MAC地址
     */
    protected  void connectKeyLock(String keyLockMac){
        mService.connect(keyLockMac);
    }


    /**
     * 操作  断开与锁的蓝牙连接
     */
    protected void disconnectKeyLock(){
        mService.disconnect();
    }

    /**
     * 操作 发送指令，去获取通信KEY
     * @param deviceKey  8字节设备KEY
     */
    protected  void sendGetKeyCommand(String deviceKey){
        mService.sendGetKey(deviceKey);
    }

    /**
     * 操作 发送指令，去获取设备信息
     */
    protected  void sendGetKeyLockInfoCommand(){
        mService.sendGetKeyLockInfo();
    }

    /**
     * 操作 发送指令，去设置报警开关
     * @param isOpen true 表示开启报警，false表示关闭
     */
    protected  void sendSetAlert(boolean isOpen){
        mService.sendSetAlert(isOpen);
    }


    /**
     * 操作 发送指令 发送开匣指令
     * @param uid  用户ID
     * @param timestamp 开锁时间戳
     */
    protected  void sendKeyLockOpenCommand(int uid,long timestamp){
        mService.sendKeyLockOpen(uid,timestamp);
    }

    /**
     *  操作 发送 同步时间戳
     * @param timestamp  使用 UNIX 时间戳格式
     */
    protected  void sendKeyLockSetTimestamp( long timestamp){
        mService.sendKeyLockSetTimestamp(timestamp);
    }
    /**
     * 操作 获取开锁记录信息
     */
    protected  void sendKeyLockOpenRecord(  ){
        mService.sendGetOpenRecord();
    }

    /**
     * 操作 清空开锁记录信息
     */
    protected  void sendKeyLockClearOpenRecord(  ){
        mService.sendClearOpenRecord();
    }
    /**
     * 操作 设置开锁键盘密码
     */
    protected  void sendKeyLockInputPWD(long password  ){
        mService.sendInputPassword((byte)1,password);
    }

    /**
     * 操作 查询键盘密码
     * @param password
     */
    protected  void sendKeyLockSearchPWD(long password  ){
        mService.sendInputPassword((byte)2,password);
    }


    /**
     * 操作 删除键盘密码
     * @param password
     */
    protected  void sendKeyLockDeletePWD(long password  ){
        mService.sendInputPassword((byte)3,password);
    }

    /**
     * 操作 清除所有密码
     */
    protected  void sendKeyLockPWDClear(   ){
        mService.sendInputPassword((byte)4,0);
    }
    /**
     * 操作 设置密码键盘的参数
     * @param maxLen  密码的最大长度
     * @param validTime 密码的有效时间
     */
    protected  void sendKeyLockPwdConfigs(byte maxLen,long validTime  ){
        mService.sendPwdConfigs(maxLen,validTime);
    }
    /**
     * 操作  修改连接锁时使用的 8字节设备KEY
     * @param deviceKey
     */
    protected  void sendModifyDeviceKeyCommand(String deviceKey){
        mService.sendModifyDeviceKey(deviceKey);
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
     * 操作  清空自定义的8字节设备KEY，恢复到默认KEY
     */
    protected  void sendClearDeviceKey( ){
        mService.sendClearDeviceKey( );
    }
    /**
     * 操作  设备关机，不再发送蓝牙广播
     */
    protected  void sendShutDown( ){
        mService.sendShutdown( );
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
        intentFilter.addAction(KeyLockService.ACTION_BLE_WRITE_NOTIFY);
        intentFilter.addAction(KeyLockService.ACTION_BLE_OPT_GET_KEY_WITH_MAC);
        intentFilter.addAction(KeyLockService.ACTION_BLE_OPT_GET_KEY_ERROR);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_OPEN);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_CLOSE);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_INFO);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_LEFT);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_BACK);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_SET_TIMESTAMP);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_ALERT);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_MODIFY_KEY);
        intentFilter.addAction(KeyLockService.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_INPUT_PASSWORD);
        intentFilter.addAction(KeyLockService.ACTION_BLE_KEY_LOCK_OPEN_RECORD);



        intentFilter.addAction(KeyLockService.ACTION_BLE_TRANSMISSION_PACK);


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);

    }

    protected void startBindService(){
        Log.i(TAG, "startBindService: 启动服务");
        Intent gattServiceIntent=new Intent(this,KeyLockService.class);
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
     * 回调   锁关锁的信息回调
     * @param keyLockStatus  锁状态
     * @param keyStatus 钥匙状态
     * @param timestamp 开锁时间戳
     */
    protected  void onBLECloseCallBack(int keyLockStatus,int keyStatus,long timestamp){

        Log.i(TAG, "onBLEOpenCallBack: "+String.format("keyLockStatus=%s,keyStatus=%s,timestamp=%s",keyLockStatus,keyStatus,timestamp));
        if(keyLockStatus==0){
            Toast.makeText(this, "关匣 失败", Toast.LENGTH_SHORT).show();
        }else if(keyLockStatus==1){
            Toast.makeText(this, "关匣 成功", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "onBLECloseCallBack: 钥匙情况 0x="+String.format("%X",keyStatus));
        Log.i(TAG, "onBLECloseCallBack: "+String.format("开匣时间戳=%s",timestamp));

    }
    /**
     * 回调   锁开锁的信息回调
     * @param status  开锁状态，1表示成功，2表示失败
     * @param timestamp 开锁的时间戳
     */
    protected  void onBLEOpenCallBack(int status, long timestamp){
        Log.i(TAG, "onBLEOpenCallBack: status="+status+",timestamp="+timestamp);
        if(status==1){
            Toast.makeText(this, "开匣成功", Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(this, "开匣失败", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 回调  钥匙锁 中钥匙被取走回调
     * @param index 钥匙下标
     */
    protected  void onBLEKeyLockLeft(int  index){    }

    /**
     * 回调  设置锁内部时间戳的结果回调
     * @param result
     */
    protected  void onBLEKeyLockSetTimestamp(int  result){    }

    /**
     *  回调  设置锁报警状态的回调
     * @param result
     */
    protected  void onBLEKeyLockSetAlert(int  result){    }

    /**
     * 回调 钥匙锁 中钥匙被放回调
     * @param index 钥匙下标
     */
    protected  void onBLEKeyLockBack(int  index){    }

    /**
     * 回调 修改了锁设备KEY的结果回调
     * @param status
     */
    protected  void onBLEModifyKeyStatus(int  status){

    }

    /**
     * 回调  锁信息的回调
     * @param voltage  电压值
     * @param status   状态值
     * @param version  硬件版本信息
     */
    protected  void onBLEKeyLockInfo(int  voltage, int status, long keyLockTimestamp, String version){
        
        Log.i(TAG, "onBLEKeyLockInfo: "+String.format("v=%s,s=%s,localTime=%s,version=%s",voltage,status,keyLockTimestamp,version));

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
    /**
     * 回调，蓝牙设备断开连接的回调
     */
    protected  void onBLEDisconnected(){

    }
    /**
     * 回调，获取到通信的KEY的出错的回调
     */
    protected  void onBLEGetKeyError(){

    }

    /**
     * 回调，获取到通信的KEY的回调，在主线程中回调获
     */
    protected  void onBLEGetKey(){

    }

    /**
     * 回调，获取到通信的KEY的回调，在主线程中回调
     * @param mac   连接的蓝牙设备的MAC地址
     * @param ckey  蓝牙通信使用的KEY
     */
    protected  void onBLEGetKey(String mac,byte ckey){

        Log.i(TAG, "onBLEGetKey: mac="+mac+",ckey="+ckey);
    }
    /**
     * 回调  注册完蓝牙通知的回调,在主线程中回调。
     */
    protected  void onBLEWriteNotify(){
        Log.i(TAG, "onBLEWriteNotify: write notify thread name="+Thread.currentThread().getName());

    }

    /**
     * 回调 指令发送错误的回调
     * @param status 错误状态码
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

    protected  void onBLEKeyLockTransmission(int pack,int type){

    }

    protected  void onBLEKeyLockInputPassword(int type ,int total,int status){

    }
    protected  void onBLEKeyLockOpenRecordCallback(int recordIndex,long timestamp,long time){

    }
    protected  void onBLEKeyLockOpenRecordCallback(int recordIndex,long openKey,long timestamp,int time,int keyStatus){

    }


    protected  void sendTransmissionData(int pack,byte[] data){
        mService.sendTransmissionData(pack,data);
    }

    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i(TAG, "onReceive: all  action="+intent.getAction());
            if(BaseService.ACTION_BLE_OPT_GET_KEY_WITH_MAC.equals(intent.getAction()) ){
                String mac=intent.getStringExtra("mac");
                Byte ckey=intent.getByteExtra("ckey",(byte)0);
                onBLEGetKey();
                onBLEGetKey(mac,ckey);
            }else if( KeyLockService.ACTION_BLE_WRITE_NOTIFY.equals(intent.getAction())){
              //  Log.i(TAG, "onReceive: write notify thread name="+Thread.currentThread().getName());
                onBLEWriteNotify();
            }else if(KeyLockService.ACTION_BLE_DISCONNECTED.equals(intent.getAction())){
                onBLEDisconnected();
            } else if(KeyLockService.ACTION_BLE_KEY_LOCK_INFO.equals(intent.getAction())){
//                Log.i(TAG, "onReceive:info  thread name="+Thread.currentThread().getName());
                int voltage = intent.getIntExtra(KeyLockService.EXTRA_VOLTAGE,0);
                int status = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_STATUS,0);
                long keyLockTimestamp = intent.getLongExtra(KeyLockService.EXTRA_KEY_LOCK_TIMESTAMP,0L);
                String version = intent.getStringExtra(KeyLockService.EXTRA_VERSION);

                onBLEKeyLockInfo(voltage,status,keyLockTimestamp,version);

            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_MODIFY_KEY.equals(intent.getAction())){
                int status = intent.getIntExtra(KeyLockService.EXTRA_MODIFY_KEY_STATUS,0);

                onBLEModifyKeyStatus(status);
            }else if(BaseService.ACTION_BLE_OPT_GET_KEY_ERROR.equals(intent.getAction())){
                onBLEGetKeyError();
            }else if(KeyLockService.ACTION_BLE_COMMAND_ERROR.equals(intent.getAction())){
                int errorStatus = intent.getIntExtra(CarportService.EXTRA_ERROR_STATUS,0);
                onBLECommandError(errorStatus);
            }
            else if(KeyLockService.ACTION_BLE_KEY_LOCK_OPEN.equals(intent.getAction())){
                // 开匣广播
                int status = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_STATUS,0);
                long timestamp = intent.getLongExtra(KeyLockService.EXTRA_TIMESTAMP,0L);
                onBLEOpenCallBack(status,timestamp);

            }
            else if(KeyLockService.ACTION_BLE_KEY_LOCK_CLOSE.equals(intent.getAction())){
                // 上匣广播
                int keyLockStatus = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_CLOSE_STATUS,0);
                long timestamp = intent.getLongExtra(KeyLockService.EXTRA_TIMESTAMP, 0L);
                int keyStatus = intent.getIntExtra(KeyLockService.EXTRA_KEY_STATUS,0);
                onBLECloseCallBack(keyLockStatus,  keyStatus,timestamp);

            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_LEFT.equals(intent.getAction())){
                // 钥匙被取走广播
                int keyStatus = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_LEFT,0);
                onBLEKeyLockLeft(keyStatus);
            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_BACK.equals(intent.getAction())){
                // 钥匙被 放回广播
                int status = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_BACK,0);
                onBLEKeyLockBack(status);
            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_SET_TIMESTAMP.equals(intent.getAction())){
                // 设置钥匙锁  时间戳
                int result = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_RESULT,0);
                onBLEKeyLockSetTimestamp(result);
            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_ALERT.equals(intent.getAction())){
                int result = intent.getIntExtra(KeyLockService.EXTRA_KEY_LOCK_RESULT,0);
                onBLEKeyLockSetAlert(result);
            }else if(KeyLockService.ACTION_BLE_TRANSMISSION_PACK.equals(intent.getAction())){
                int pack = intent.getIntExtra(KeyLockService.EXTRA_TRANSMISSION_PACK_PACK,0);
                int type = intent.getIntExtra(KeyLockService.EXTRA_TRANSMISSION_PACK_TYPE,0);

                onBLEKeyLockTransmission(  pack,  type);
            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_INPUT_PASSWORD.equals(intent.getAction())){
                int total = intent.getIntExtra(KeyLockService.EXTRA_PASSWORD_NUMBER,0);
                int status = intent.getIntExtra(KeyLockService.EXTRA_STATUS,0);
                int type = intent.getIntExtra(KeyLockService.EXTRA_PASSWORD_TYPE,0);

                onBLEKeyLockInputPassword(  type,total,  status);
            }else if(KeyLockService.ACTION_BLE_KEY_LOCK_OPEN_RECORD.equals(intent.getAction())){
                int time = intent.getIntExtra(KeyLockService.EXTRA_RECORD_TIME,0);
                long timestamp = intent.getLongExtra(KeyLockService.EXTRA_RECORD_TIMESTAMP,0L);
                long openKey = intent.getLongExtra(KeyLockService.EXTRA_RECORD_OPEN_KEY,0L);
                int recordIndex = intent.getIntExtra(KeyLockService.EXTRA_RECORD_INDEX,0);
                int keyStatus = intent.getIntExtra(KeyLockService.EXTRA_RECORD_KEY_STATUS,0);

                onBLEKeyLockOpenRecordCallback(  recordIndex,openKey,  timestamp,time,keyStatus);
            }
        }
    };
}
