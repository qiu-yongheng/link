package com.omni.ble.library.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.ble.library.model.GattAttributes;
import com.omni.ble.library.utils.CommandUtil;
import com.omni.ble.library.utils.KeyLockCommand;
import com.omni.lib.utils.PrintUtil;

import java.util.UUID;


/**
 * Description: 钥匙盒锁 蓝牙服务 <br />
 * Created by CxiaoX on 2015/12/15 14:42.
 */
public class KeyLockService extends BaseService {
    private final static String TAG=KeyLockService.class.getSimpleName();

    @Override
    public UUID getServiceUUID() {
        return getUUID("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    }

    @Override
    public UUID getWriteUUID() {
        return getUUID("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    }

    @Override
    public UUID getNotifyUUID() {
        return getUUID("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * 获取配对信息的广播
     */

    public final static String ACTION_BLE_KEY_LOCK_MODIFY_KEY="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_MODIFY_KEY";
    public final static String ACTION_BLE_COMMAND_ERROR="com.omni.ble.library.keylock.ACTION_BLE_COMMAND_ERROR";
    /**
     * 获取钥匙锁信息的广播
     */
    public final static String ACTION_BLE_KEY_LOCK_INFO ="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_INFO";
    /**
     * 钥匙锁，钥匙被取走通知
     */
    public final static String ACTION_BLE_KEY_LOCK_LEFT ="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_LEFT";
    /** 钥匙锁，钥匙被放回通知  */
    public final static String ACTION_BLE_KEY_LOCK_BACK ="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_BACK";
    public final static String ACTION_BLE_KEY_LOCK_SET_TIMESTAMP="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_SET_TIMESTAMP";
    public final static String ACTION_BLE_KEY_LOCK_ALERT="com.omni.ble.library.keylock.ACTION_BLE_KEY_LOCK_ALERT";

    /** 钥匙锁 开锁的广播通知 */
    public final static String ACTION_BLE_KEY_LOCK_OPEN ="com.omni.ble.library.carport.ACTION_BLE_KEY_LOCK_OPEN";
    /**  钥匙锁 关锁的广播通知  */
    public final static String ACTION_BLE_KEY_LOCK_CLOSE ="com.omni.ble.library.carport.ACTION_BLE_KEY_LOCK_CLOSE";
    /**
     * 钥匙盒锁 输入备份密码的广播
     */
    public final static String ACTION_BLE_KEY_LOCK_INPUT_PASSWORD ="com.omni.ble.library.ACTION_BLE_KEY_LOCK_INPUT_PASSWORD";


    /**
     * 钥匙盒锁 获取密码开锁记录的广播
     */
    public final static String ACTION_BLE_KEY_LOCK_OPEN_RECORD ="com.omni.ble.library.ACTION_BLE_KEY_LOCK_OPEN_RECORD";


    public static  final String EXTRA_MAC="MAC";
    public static  final String EXTRA_VOLTAGE="voltage";
    public static  final String EXTRA_VERSION="version";
    public static  final String EXTRA_KEY_LOCK_LEFT="left_key_status";
    public static  final String EXTRA_KEY_LOCK_BACK="back_key_status";
    public static  final String EXTRA_KEY_LOCK_RESULT="result";
    public static  final String EXTRA_KEY_LOCK_TIMESTAMP="key_lock_timestamp";
    public static  final String EXTRA_KEY_LOCK_STATUS ="key_lock_status";
    public static  final String EXTRA_KEY_LOCK_CLOSE_STATUS ="key_lock_close_status";
    public static  final String EXTRA_KEY_STATUS ="key_status";

    public static  final String EXTRA_TIME="time";

    //========连接蓝牙设备时的 连接状态
    /** 断开连接*/
    public static final int STATE_DISCONNECTED =0;
    /** 正在连接 */
    public static final int STATE_CONNECTING =1;
    /** 找到蓝牙连接*/
    public static final int STATE_CONNECTED =2;
    /** 找到服务*/
    public static final int STATE_FIND_SERVICE =3;
    public static final int STATE_BIND_CONNECTED =4;
    /**  关闭 gatt 重新连接设备 */
    public static final int STATE_CLOSE_GATT_RECONNECT = 16;




    // 用来标识是否自动连接
    private boolean autoConnect = false;


    //===============handler 信息 类型
    /**  handler 手机蓝牙开启时连接设备 */
    private final static int HANDLER_WRITE_KEY_ERROR=0;
    private final static int HANDLER_BLE_ON_CONNECT=2;


    private final static int HANDLER_DISCOVER_SERVICES=30;
    private final static int HANDLER_WHAT_DISCONNECT_BLE=55;

    /** 检测当前连接状态，并查看是否重新连接 */
    private final static int HANDLER_STATE_CONNECT=3;
    /**  用handler 控制的下次判断 蓝牙连接状态的 间隔 */
    private final static int HANDLER_STATE_CONNECT_DELAYED_TIME=2000;


    private   Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case HANDLER_WHAT_DISCONNECT_BLE:
                    disconnect();
                    break;
                case HANDLER_WRITE_KEY_ERROR:
                    Log.i(TAG, "handleMessage:连接蓝牙时，key 写错误");
                    break;
                case HANDLER_DISCOVER_SERVICES:
                    discoverServices(mBLEGatt);
                    break;
                case HANDLER_STATE_CONNECT:
                    if (mConnectionState != STATE_BIND_CONNECTED) {
                        int preConnectionState = msg.arg1; //之前的连接状态
                        if (preConnectionState == mConnectionState) {
//                            Log.i(TAG, "handleMessage:3s过去 还是原来的状态");
                            //5s中后还没有改变原来的连接状态
                            //还没有绑定连接

//
                        }else if(preConnectionState==STATE_CLOSE_GATT_RECONNECT){

                        }
                    }
                    break;


            }
        }
    };

    /** 开锁扫描 */
    public static final String ACTION_BLE_SCAN_START="com.omni.ble.library.ACTION_BLE_SCAN_START";
    /** 停止扫描 */
    public static final String ACTION_BLE_SCAN_STOP="com.omni.ble.library.ACTION_BLE_SCAN_STOP";

    /** 扫描到指定的设备 --扫描到 钥匙盒锁 */
    public static final String ACTION_BLE_SCAN_DEVICE ="com.omni.ble.library.ACTION_BLE_SCAN_DEVICE";



    public BluetoothGatt getmBLEGatt() {
        return mBLEGatt;
    }


    private final BroadcastReceiver sanDeviceReceiver =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BLE_SCAN_STOP.equals(intent.getAction())){


            }
            else if(ACTION_BLE_SCAN_START.equals(intent.getAction())){
                // 开始扫描时
                Log.i(TAG, "onReceive: 开始扫描设备的广播");
            }else if(ACTION_BLE_SCAN_DEVICE.equals(intent.getAction())){


            }
        }
    };


    public static final String ACTION_BLE_STATE_ON="com.omni.ble.library.carport.ACTION_BLE_STATE_ON";
    /**
     * 用来连接系统蓝牙开关
     */
    private final BroadcastReceiver bleStateReceiver =new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
                Log.i(TAG, "onReceive: ble  开关状态改变");

                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

                if(state==BluetoothAdapter.STATE_OFF) {
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "372 onReceive: ble  当前状态为关");

                    if(mBLEGatt!=null) {
                        mBLEGatt.close();
                        mBLEGatt = null;
                    }
                }else if(state==BluetoothAdapter.STATE_ON){
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "381 onReceive: ble  当前状态为开");

//                    handler.sendEmptyMessage(HANDLER_BLE_ON_CONNECT);
                    handler.sendEmptyMessageDelayed(HANDLER_BLE_ON_CONNECT,1000);
                    sendLocalBroadcast(ACTION_BLE_STATE_ON);
                }else if(state==BluetoothAdapter.STATE_TURNING_ON){
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "388 onReceive: ble  当前状态为正在开");
                    autoConnect=true;
                }else{
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "392 onReceive: ble  当前状态为正在关");
                    autoConnect=false;
                }

            }
        }
    };





    protected  void bleConnectionStateChange(final BluetoothGatt gatt,final int status,final int newState){}
    protected  void bleConnection(final BluetoothGatt gatt,final int status,final int newState){}
    protected  void bleDisconnection(final BluetoothGatt gatt,final int status,final int newState){}



    private void handCommand(String mac,byte[] command){
        Log.i(TAG, "handCommand: command CODE= "+String.format("0x%02X",command[5]));
        Log.i(TAG, "handCommand: 解密后= "+PrintUtil.toHexString(command));

    }

    @Override
    public void onHandNotifyCommand(String mac, byte[] command) {
        super.onHandNotifyCommand(mac, command);
        switch (command[5]){
            case CommandType.KEY_LOCK_COMMUNICATION_KEY:
                handCommunicationKey(mac,command);
                break;
            case CommandType.KEY_LOCK_ERROR:
                handCommandError(command);
                break;
            case CommandType.KEY_LOCK_OPEN:
                handCommandOpen(command);
                break;
            case CommandType.KEY_LOCK_CLOSE:
                handCommandClose(command);
                break;
            case CommandType.KEY_LOCK_INFO:
                handCommandInfo(command);
                break;
            case CommandType.KEY_LOCK_LEFT:
                handCommandLeft(command);
                break;
            case CommandType.KEY_LOCK_BACK:
                handCommandBack(command);
                break;
            case CommandType.KEY_LOCK_SET_TIMESTAMP:
                handSetTimestamp(command);
                break;
            case CommandType.KEY_LOCK_ALERT:
                handAlert(command);
                break;
            case CommandType.KEY_LOCK_MODIFY_DEVICE_KEY:
                handModifyKey(command);
                break;
            case CommandType.KEY_LOCK_OPEN_RECORD:
                handOpenRecord(command);
                break;
            case CommandType.KEY_LOCK_CLEAR_OPEN_RECORD:
                handClearOpenRecord(command);
                break;
            case CommandType.KEY_LOCK_INPUT_PWD:
                handInputPwd(command);
                break;
            case CommandType.KEY_LOCK_PWD_CONFIGS:
                Log.i(TAG, "handCommand: 回应！");
                break;

        }

    }

    protected void callbackModifyKey(int status ){}
    protected void callbackOpenRecord(int recordIndex,long openTimestamp,long openTime ){


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_OPEN_RECORD);
        intent.putExtra(EXTRA_RECORD_INDEX,recordIndex);
        intent.putExtra(EXTRA_RECORD_TIMESTAMP,openTimestamp);
        intent.putExtra(EXTRA_RECORD_TIME,openTime);
        sendLocalBroadcast(intent);
    }

    protected void callbackOpenRecord(int recordIndex,long openKey,long openTimestamp,int openTime,int keyStatus){


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_OPEN_RECORD);
        intent.putExtra(EXTRA_RECORD_INDEX,recordIndex);
        intent.putExtra(EXTRA_RECORD_TIMESTAMP,openTimestamp);
        intent.putExtra(EXTRA_RECORD_TIME,openTime);
        intent.putExtra(EXTRA_RECORD_OPEN_KEY,openKey);
        intent.putExtra(EXTRA_RECORD_KEY_STATUS,keyStatus);
        sendLocalBroadcast(intent);
    }



    @Override
    protected void callbackCommandError(int status) {
        super.callbackCommandError(status);
    }

    @Override
    protected void callbackCommunicationKeyError() {
        super.callbackCommunicationKeyError();
    }

    protected void callbackKeyLockLeft(int status ){}
    protected void callbackKeyLockBack(int status ){}
    protected void callbackKeyLockSetTimestamp(int result ){}
    protected void callbackKeyLockAlert(int result ){}

    /**
     * 蓝牙设备返回的 车位锁信息
     * @param voltage  车位锁电池电压值
     * @param carportStatus 车位锁拦车状态
     * @param version 车锁硬件版本号
     */
    protected void callbackKeyLockInfo(int voltage, int carportStatus, String version ){}


    public static  final String EXTRA_STATUS="status";
    public static  final String EXTRA_PASSWORD_NUMBER="password_num";
    public static  final String EXTRA_PASSWORD_TYPE="password_type";
    public static  final String EXTRA_MODIFY_KEY_STATUS="status";
    public static  final String EXTRA_ERROR_STATUS="error_status";
    public static  final String EXTRA_RECORD_INDEX="record_index";
    public static  final String EXTRA_RECORD_TIMESTAMP="record_timestamp";
    public static  final String EXTRA_RECORD_OPEN_KEY="open_key";
    public static  final String EXTRA_RECORD_TIME="record_time";
    public static  final String EXTRA_RECORD_KEY_STATUS="key_status";


    private void handModifyKey(byte[] command){
        int status = command[6];
        callbackModifyKey(status);
        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_MODIFY_KEY);
        intent.putExtra(EXTRA_MODIFY_KEY_STATUS,status);
        sendLocalBroadcast(intent);
    }
    private void handOpenRecord(byte[] command){
        int  recordIndex = ((command[6]&0xFF)<<8) | command[7];

        long openKey=((command[8]&0xFF)<<24 )|((command[9]&0xFF)<<16)|((command[10]&0xFF)<<8)| (command[11]&0xFF);
        long openTimestamp=((command[12]&0xFF)<<24 )|((command[13]&0xFF)<<16)|((command[14]&0xFF)<<8)| (command[15]&0xFF);


        int openTime =( (command[16]&0xFF)<<8)| (command[17]&0xFF);
        int keyStatus =  command[18];


        sendOpenRecordResponse(recordIndex);

        Log.i(TAG, "handOpenRecord: recordIndex="+recordIndex);
        Log.i(TAG, "handOpenRecord: openTimestamp="+openTimestamp);
        Log.i(TAG, "handOpenRecord: openTime="+openTime);
//        callbackOpenRecord(recordIndex,openTimestamp,openTime);
        callbackOpenRecord(recordIndex,openKey,openTimestamp,openTime,keyStatus);

        // 返回钥匙锁密码开锁 回应

    }
    private void handClearOpenRecord(byte[] command){
        int status = command[6];


        Log.i(TAG, "handClearOpenRecord: status="+status);


    }
    private void handInputPwd(byte[] command){
        Log.i(TAG, "handInputPwd: "+ PrintUtil.toHexString(command));
        int status = command[6]; //// 状态
        int total = command[7]; // 当前密码总数
        int optType = command[8]; // 操作类型

        Log.i(TAG, "handInputPwd: num="+total);
        Log.i(TAG, "handInputPwd: status="+status);

        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_INPUT_PASSWORD);
        intent.putExtra(EXTRA_STATUS,status);
        intent.putExtra(EXTRA_PASSWORD_NUMBER,total);
        intent.putExtra(EXTRA_PASSWORD_TYPE,optType);
        sendLocalBroadcast(intent);
    }
    private void handCommandError(byte[] command){
        Log.i(TAG, "handCommandError: 指令失败" );
        int status = command[6];
        callbackCommandError(status);
        Intent intent = new Intent(ACTION_BLE_COMMAND_ERROR);
        intent.putExtra(EXTRA_ERROR_STATUS,status);
        sendLocalBroadcast(intent);
    }

    private void handCommandInfo(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        int voltage = ((command[6]&0xFF)<<8) | (command[7]&0xFF);
        int keyLockStatus = command[8]&0xFF;
        long keyLockTime =((command[9]&0xFF)<<24 )|((command[10]&0xFF)<<16)|((command[11]&0xFF)<<8)| (command[12]&0xFF);
        String version = String.format("%s.%s.%s",command[13],command[14],command[15]);
        Log.i(TAG, "handCommandInfo: voltage="+voltage);
        callbackKeyLockInfo(voltage,keyLockStatus,version);


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_INFO);
        intent.putExtra(EXTRA_VOLTAGE,voltage);
        intent.putExtra(EXTRA_VERSION,version);
        intent.putExtra(EXTRA_KEY_LOCK_TIMESTAMP,keyLockTime);
        intent.putExtra(EXTRA_KEY_LOCK_STATUS,keyLockStatus);
        sendLocalBroadcast(intent);
    }
    private void handCommandLeft(byte[] command){
        Log.i(TAG, "handCommandLeft: 钥匙取走" );
        int keyStatus = (command[6]&0xFF)  ;
        callbackKeyLockLeft(keyStatus);

        // 回复钥匙取走通知
        sendLeftResponse();


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_LEFT);
        intent.putExtra(EXTRA_KEY_LOCK_LEFT,keyStatus);
        sendLocalBroadcast(intent);
    }

    private void handCommandBack(byte[] command){
        Log.i(TAG, "handCommandLeft: 钥匙 放回" );
        int status= (command[6]&0xFF)  ;
        callbackKeyLockBack(status);

        sendBackResponse();
        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_BACK);

        intent.putExtra(EXTRA_KEY_LOCK_BACK,status);
        sendLocalBroadcast(intent);
    }
    private void handSetTimestamp(byte[] command){
        Log.i(TAG, "handSetTimestamp: 设置时间戳" );
        int result= ((command[6]&0xFF))  ;
        callbackKeyLockSetTimestamp(result);


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_SET_TIMESTAMP);
        intent.putExtra(EXTRA_KEY_LOCK_RESULT,result);
        sendLocalBroadcast(intent);
    }

    private void handAlert(byte[] command){
        Log.i(TAG, "handAlert: 设置报警开关" );
        int result= ((command[6]&0xFF))  ;
        callbackKeyLockAlert(result);


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_ALERT);
        intent.putExtra(EXTRA_KEY_LOCK_RESULT,result);
        sendLocalBroadcast(intent);
    }



    private void handCommandOpen(byte[] command){
        Log.i(TAG, "handCommandOpen: 开匣" );
        int status = command[6];
        long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);

        // 开锁返回指令
        sendOpenResponse();

        // 发送开锁信息广播
        Log.i(TAG, "handCommandOpen: timestamp="+timestamp);
        Log.i(TAG, "handCommandOpen: status="+status);
        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_OPEN);
        intent.putExtra(EXTRA_KEY_LOCK_STATUS, status);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        sendLocalBroadcast(intent);

        if(status==1){
            Log.i(TAG, "handCommandOpen: status= 开匣成功");

        }else if(status==2){
            Log.i(TAG, "handCommandOpen: status= 开匣失败");

        }



    }

    private void handCommandClose(byte[] command){
        Log.i(TAG, "handCommandClose: 关匣" );
        int status = command[6]; // 关匣状态
        int keyStatus =command[7] ;// 匣内钥匙状态，bits 位标识

        // 拦车时间戳
        long timestamp= ((command[8]&0xFF)<<24 )|((command[9]&0xFF)<<16)|((command[10]&0xFF)<<8)| (command[11]&0xFF);


        Intent intent = new Intent(ACTION_BLE_KEY_LOCK_CLOSE);
        intent.putExtra(EXTRA_KEY_LOCK_CLOSE_STATUS, status); // 钥匙锁开关状态
        intent.putExtra(EXTRA_KEY_STATUS, keyStatus);// 钥匙锁内 钥匙存在状态
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);

        sendLocalBroadcast(intent);


        // 关匣返回
        sendKeyLockCloseResponse();

    }


    public byte[] sendGetKey(){
        Log.i(TAG, "sendGetKey: 发送获取操作KEY指令");
        String deviceKey ="OmniW4GX";
        byte[] crcOrder= CommandUtil.getCRCCommunicationKey(deviceKey);

        return writeToDevice(crcOrder);
    }

    public byte[] sendModifyDeviceKey(String newDeviceKey){
        Log.i(TAG, "sendModifyDeviceKey: 发送修改设备KEY指令");

        byte[] crcOrder= KeyLockCommand.getCRCModifyDeviceKey(mBLECommunicationKey, newDeviceKey);

        return writeToDevice(crcOrder);
    }

    public byte[] sendClearDeviceKey( ){
        Log.i(TAG, "sendClearDeviceKey: 发送清除设备KEY指令");

        byte[] crcOrder= KeyLockCommand.getCRCClearDeviceKey(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }
    public byte[] sendPwdConfigs(byte maxLen,long validTime ){
        Log.i(TAG, "sendPwdConfigs: 配置随机密码参数");

        byte[] crcOrder= KeyLockCommand.getCRCPwdConfigs(mBLECommunicationKey,maxLen,validTime);

        return writeToDevice(crcOrder);
    }
    public byte[] sendShutdown( ){
        Log.i(TAG, "sendShutdown: 关机指令");

        byte[] crcOrder= KeyLockCommand.getCRCShutDown(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }

    public byte[] sendInputPassword(byte type,long password ){
        Log.i(TAG, "sendInputPassword: 写入备份密码");

        byte[] crcOrder= KeyLockCommand.getCRCInputPWD(mBLECommunicationKey,type,password);
        return writeToDevice(crcOrder);
    }
    public byte[] sendGetOpenRecord( ){
        Log.i(TAG, "sendGetOpenRecord: 获取开锁记录指令");

        byte[] crcOrder= KeyLockCommand.getCRCOpenRecord(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }
    public byte[] sendClearOpenRecord( ){
        Log.i(TAG, "sendClearOpenRecord: 清空开锁记录指令");

        byte[] crcOrder= KeyLockCommand.getCRCCleartOpenRecord(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }
    /**
     * 设置 报警开关
     * @return
     */
    public byte[] sendSetAlert(boolean isOpen){
        Log.i(TAG, "sendSetAlert: 设置 报警开关");

        byte[] crcOrder= KeyLockCommand.getCRCAlertOpen(mBLECommunicationKey,isOpen);

        return writeToDevice(crcOrder);
    }

    public byte[] sendGetKey(String deviceKey){
        Log.i(TAG, "sendGetKey:发送获取操作KEY指令");
        byte[] crcOrder=KeyLockCommand.getCRCCommunicationKey(deviceKey);

        return writeToDevice(crcOrder);
    }



    public byte[] sendGetKeyLockInfo(){
        Log.i(TAG, "sendScooterOpen:发送 获取车位锁信息指令");
        byte[] crcOrder= KeyLockCommand.getCRCKeyLockInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendKeyLockOpen(int uid,long timestamp){
        Log.i(TAG, "sendKeyLockOpen:发送 开匣指令");
        byte[] crcOrder= KeyLockCommand.getCRCOpen(mBLECommunicationKey,uid,timestamp);
        return writeToDevice(crcOrder);
    }

    public byte[] sendKeyLockSetTimestamp(long timestamp){
        Log.i(TAG, "sendKeyLockSetTimestamp:发送 同步时间戳");
        byte[] crcOrder= KeyLockCommand.getCRCSetTimestamp(mBLECommunicationKey,  timestamp);
        return writeToDevice(crcOrder);
    }

    public byte[] sendOpenResponse(){
        Log.i(TAG, "sendOpenResponse:  开锁指令返回");
        byte[] crcOrder= KeyLockCommand.getCRCResponseOpen(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendLeftResponse(){
        Log.i(TAG, "sendLeftResponse:  发送取走钥匙的返回");
        byte[] crcOrder= KeyLockCommand.getCRCResponseLeft(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    public byte[] sendOpenRecordResponse(byte index){
        Log.i(TAG, "sendOpenRecordResponse:  发送获取到开锁记录的返回");
        byte[] crcOrder= KeyLockCommand.getCRCResponseOpenRecord(mBLECommunicationKey,index);
        return writeToDevice(crcOrder);
    }

    public byte[] sendOpenRecordResponse(int index){
        Log.i(TAG, "sendOpenRecordResponse:  发送获取到开锁记录的返回");
        byte[] crcOrder= KeyLockCommand.getCRCResponseOpenRecord(mBLECommunicationKey,index);
        return writeToDevice(crcOrder);
    }

    public byte[] sendBackResponse(){
        Log.i(TAG, "sendBackResponse:  发送放回钥匙的返回");
        byte[] crcOrder= KeyLockCommand.getCRCResponseBack(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendKeyLockCloseResponse(){
        Log.i(TAG, "sendKeyLockCloseResponse:发送 关匣 返回 回应");
        byte[] crcOrder= KeyLockCommand.getCRCResponseClose(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }




    @Override
    public boolean stopService(Intent name) {
        close();
        return super.stopService(name);
    }


    /**
     * 连接蓝牙设备，断开时自动重连。
     * @param address 要连接的蓝牙设置地址
     * @return 连接成功返回true，否则返回false
     */
    public boolean autoConnect(final String address) throws  IllegalArgumentException{
        Log.i(TAG, "autoConnect: 自动连接 蓝牙");
        autoConnect = true;

        return connect(address);
    }








    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic ){
        if(mBLEAdapter==null||mBLEGatt==null){
            Log.w(TAG, "setCharacteristicNotification: BluetoothAdapter not initialized");
            return ;
        }

        Log.i(TAG, "setCharacteristicNotification: 注册 通知");
        
        BluetoothGattDescriptor descriptor=characteristic.getDescriptor(GattAttributes.UUID_NOTIFICATION_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBLEGatt.writeDescriptor(descriptor);
    }


    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     */
    private void close() {
        if(mBLEGatt==null) return ;
        mBLEGatt.close();
        mBLEGatt= null;

    }

    private synchronized  void disconnectAndColse(BluetoothGatt gatt){
        if(gatt==null) return ;
        gatt.disconnect();
        try {
            Thread.sleep(200);
            gatt.close();
            gatt=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void discoverServices(BluetoothGatt gatt){
//        if(gatt!=null) gatt.discoverServices();
//    }

    public class LocalBinder extends Binder{
        public KeyLockService getService(){
            return KeyLockService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 进入服务的初始化方法");
        if(init()) Log.i(TAG, "onCreate: 初始成功");


        //设置广播监听
        registerReceiver(bleStateReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        //注册本地广播，主要是扫描设备的时候用到
        IntentFilter ifScanDevice = new IntentFilter();

        ifScanDevice.addAction(ACTION_BLE_SCAN_STOP);
        ifScanDevice.addAction(ACTION_BLE_SCAN_START);
        ifScanDevice.addAction(ACTION_BLE_SCAN_DEVICE);
        LocalBroadcastManager.getInstance(this).registerReceiver(sanDeviceReceiver, ifScanDevice);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * @return  true if the initialization is successful.
     */
    public boolean init(){
        if(mBLEManager==null){
            mBLEManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBLEManager==null){
                Log.e(TAG, "init: Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBLEAdapter = mBLEManager.getAdapter();
        if(mBLEAdapter==null){
            Log.e(TAG, "init: Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.i(TAG, "init: mBleAdapter="+mBLEAdapter.toString());
        return true;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,  Service.START_FLAG_REDELIVERY, startId);
    }


    public boolean isBleEnabled(){
        return mBLEAdapter.isEnabled();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleStateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sanDeviceReceiver);
    }








}
