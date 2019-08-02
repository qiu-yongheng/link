package com.omni.ble.library.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.omni.ble.library.model.CommandType;
import com.omni.ble.library.model.GattAttributes;
import com.omni.ble.library.utils.CommandUtil;
import com.omni.lib.utils.CRCUtil;
import com.omni.lib.utils.MacUtils;
import com.omni.lib.utils.PrintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Description:车位锁 蓝牙服务 <br />
 * Created by CxiaoX on 2015/12/15 14:42.
 */
public class CarportService extends BaseService {
    private final static String TAG= CarportService.class.getSimpleName();

    @Override
    public UUID getServiceUUID( ) {
        return GattAttributes.UUID_CARPORT_SERVICE;
    }


    @Override
    public UUID getWriteUUID() {
        return GattAttributes.UUID_CARPORT_CHARACTERISTIC_WRITE;
    }

    @Override
    public UUID getNotifyUUID() {
        return GattAttributes.UUID_CARPORT_CHARACTERISTIC_NOTIFY;
    }

    private final IBinder mBinder = new LocalBinder();



    private int carportMode =MODE_CARPORT_MANUAL;
    private static final int MODE_CARPORT_AUTO = 3;
    private static final int MODE_CARPORT_MANUAL=1;

    /* 广播通知 */
    /**
     * 获取到通信KEY的广播
     */
    public final static String ACTION_BLE_GET_KEY="com.omni.ble.library.carport.ACTION_BLE_GET_KEY";


    /**
     * 获取通信KEY错误的广播
     */
    public final static String ACTION_BLE_GET_KEY_ERROR="com.omni.ble.library.carport.ACTION_BLE_GET_KEY_ERROR";
    /**
     * 获取配对信息的广播
     */
    public final static String ACTION_BLE_PAIR_STATUS="com.omni.ble.library.carport.ACTION_BLE_PAIR_STATUS";
    public final static String ACTION_BLE_PAIR_INFO="com.omni.ble.library.carport.ACTION_BLE_PAIR_INFO";
    public final static String ACTION_BLE_MODIFY_KEY="com.omni.ble.library.carport.ACTION_BLE_MODIFY_KEY";
    public final static String ACTION_BLE_COMMAND_ERROR="com.omni.ble.library.carport.ACTION_BLE_COMMAND_ERROR";




    /**
     * 获取车位锁信息的广播
     */
    public final static String ACTION_BLE_CARPORT_INFO="com.omni.ble.library.carport.ACTION_BLE_KEY_LOCK_INFO";
    /**
     * 获取设备自身的KEY的广播
     */
    public final static String ACTION_BLE_GET_MAC="com.omni.ble.library.carport.ACTION_BLE_GET_MAC";
    /**
     * 车位锁 不拦车 广播
     */
    public final static String ACTION_BLE_CARPORT_DOWN="com.omni.ble.library.carport.ACTION_BLE_KEY_LOCK_OPEN";
    /**
     * 车位锁 拦车 广播
     */
    public final static String ACTION_BLE_CARPORT_UP="com.omni.ble.library.carport.ACTION_BLE_KEY_LOCK_CLOSE";

    public final static String ACTION_BLE_CARPORT_FW_INFO="com.omni.ble.library.carport.ACTION_BLE_SCOOTER_FW_INFO";


    public static  final String EXTRA_MAC="MAC";

    public static  final String EXTRA_CARPORT_STATUS="carport_status";
    public static  final String EXTRA_TIMESTAMP="timestamp";
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

    //===============handler 信息 类型
    /**  handler 手机蓝牙开启时连接设备 */





    private final static int HANDLER_WHAT_DISCONNECT_BLE=55;



    private final static int HANDLER_AUTO_SEND_DOWN=100;
    private Handler carportHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_AUTO_SEND_DOWN:
                    Log.i(TAG, "carportMode="+carportMode);
                    if(carportMode==MODE_CARPORT_AUTO) {
                        Log.i(TAG, "handleMessage: 10 s 后自动发送");
                        int uid = msg.arg1;


                        sendCarportDown(uid, (byte) 3);

                        Message newMessage = new Message();
                        newMessage.what = HANDLER_AUTO_SEND_DOWN;
                        newMessage.arg1 = uid;

                        carportHandler.sendMessageDelayed(newMessage, 10000);
                    }

                    break;


                case HANDLER_WHAT_DISCONNECT_BLE:
                    disconnect();
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
    public static final String ACTION_BLE_SCAN_START="com.omni.ble.library.carport.ACTION_BLE_SCAN_START";
    /** 停止扫描 */
    public static final String ACTION_BLE_SCAN_STOP="com.omni.ble.library.carport.ACTION_BLE_SCAN_STOP";



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

                if(state== BluetoothAdapter.STATE_OFF) {
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "372 onReceive: ble  当前状态为关");

                    if(mBLEGatt!=null) {
                        mBLEGatt.close();
                        mBLEGatt = null;
                    }
                }else if(state== BluetoothAdapter.STATE_ON){
                    Log.i(TAG, "onReceive: ble  state=" + state);
                    Log.i(TAG, "381 onReceive: ble  当前状态为开");

                    sendLocalBroadcast(ACTION_BLE_STATE_ON);
                }else if(state== BluetoothAdapter.STATE_TURNING_ON){
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


    private List<Byte> dataBytes= new ArrayList<>();

    @Override
    public void onHandFirmwareDataCommand(int nPack, byte[] command) {
        Log.i(TAG, "onHandFirmwareDataCommand: 第几包数据="+nPack);
        Log.i(TAG, "onHandFirmwareDataCommand:  固件信息="+ PrintUtil.toHexString(command));

        for(Byte b:command) dataBytes.add(b);

        if(nPack==totalPack-1){
            // 最后一包数据
            // 将接收到的数据转换成String
            int len = 0;
            for(int i=dataBytes.size()-1;i>=0;i--){
                byte item = dataBytes.get(i);
                if(item!=(byte)0x00) {
                    len=i+1;
                    break;
                }
            }
            byte[] allFirmwareData = new byte[len];
            for(int i=0;i<len;i++) {

                allFirmwareData[i]=dataBytes.get(i);
            }
            int calcCRC16 = CRCUtil.calcCRC16(allFirmwareData);
            Log.i(TAG, "onHandFirmwareDataCommand: 计算的CRC16="+ String.format("0x%02X",calcCRC16));
            String firmwareData = new String(allFirmwareData);
            Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);

            Intent intent = new Intent(ACTION_BLE_CARPORT_FW_INFO);
            intent.putExtra(EXTRA_FIRMWARE_DATA,firmwareData);
            sendLocalBroadcast(intent);


            if(calcCRC16 == firmwareInfoCRC16){
                //
//                String firmwareData = new String(allFirmwareData);
                Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);
            }else{
                Log.i(TAG, "onHandFirmwareDataCommand:获取到的CRC 和计算CRC 不一样");
            }
        }else{
            // 非最后一包数据
            sendGetFirmwareInfoDetail(nPack+1,(byte)0x80);
        }

    }

    @Override
    public void onHandNotifyCommand(String mac, byte[] command) {
        super.onHandNotifyCommand(mac, command);
        Log.i(TAG, "onHandNotifyCommand: command CODE= "+ String.format("0x%02X",command[5]));
        Log.i(TAG, "onHandNotifyCommand: 解密后= "+ PrintUtil.toHexString(command));
        switch (command[5]){
            case CommandType.CONTROL_DOWN:
                handCommandDown(command);
                break;
            case CommandType.CONTROL_UP:
                handCommandUp(command);
                break;
            case CommandType.CARPORT_DEVICE_INFO:
                handCommandInfo(command);
                break;
            case CommandType.DEVICE_MODEL:
                handCommandModel(command);
                break;
            case CommandType.DEVICE_LOCAL_MAC:
                handCommandMacGet(command);
                break;
            case CommandType.PAIR_REMOTE:
                handCommandPairStatus(command);
                break;
            case CommandType.MODIFY_DEVICE_KEY:
                handModifyKey(command);
                break;
            case CommandType.DEVICE_MAC_HAND_PAIR:
                handPairInfo(command);
            case CommandType.DEVICE_DATA:
                handFirmwareData(command);
                break;

        }
    }

    @Override
    protected void callbackLogData(byte[] logData) {
        Log.i(TAG, "callbackLogData: byte[]="+ PrintUtil.toHexString(logData));
        Log.i(TAG, "callbackLogData="+new String(logData,0,logData.length));


        Intent intent = new Intent(ACTION_BLE_LOG_DATA);

        intent.putExtra("data",logData);
        sendLocalBroadcast(intent);


    }

    protected void callbackPairStatus(int status ){}
    protected void callbackPairInfo(int status , String pairMac){}
    protected void callbackModifyKey(int status ){}



    /**
     * 蓝牙设备返回的 车位锁信息
     * @param voltage  车位锁电池电压值
     * @param carportStatus 车位锁拦车状态
     * @param version 车锁硬件版本号
     */
    protected void callbackCarportInfo(int voltage, int carportStatus, String version ){}
    /**
     * 蓝牙设备返回的 车位锁已配对的遥控器的mac地址
     * @param macAddress  MAC地址数组形式(型如 [0xFF,0xFF,0x00,0x10,0x01,0x00])
     */
    protected void callbackCarportPairMac(byte[] macAddress){}

    /**
     * 蓝牙设备返回的 车位锁已配对的遥控器的mac地址
     * @param macAddress  MAC地址字符串形式(型如 FF:FF:00:10:01:00)
     */
    protected void callbackCarportPairMac(String macAddress){}


    @Override
    protected void callbackCommunicationKey(String mac, byte communicationKey) {
        Intent intent = new Intent(ACTION_BLE_GET_KEY);
        intent.putExtra("mac",mac);
        intent.putExtra("key",communicationKey);
        sendLocalBroadcast(intent);
    }

    @Override
    protected void callbackCommunicationKeyError() {
        sendLocalBroadcast(ACTION_BLE_GET_KEY_ERROR);
    }


    public static  final String EXTRA_STATUS="status";
    public static  final String EXTRA_FIRMWARE_DATA="firmware_data";
    public static  final String EXTRA_MODIFY_KEY_STATUS="status";
    public static  final String EXTRA_ERROR_STATUS="error_status";
    private void handCommandPairStatus(byte[] command){
        int status = command[6];

        callbackPairStatus(status);

        Intent intent = new Intent(ACTION_BLE_PAIR_STATUS);
        intent.putExtra(EXTRA_STATUS,status);
        sendLocalBroadcast(intent);
    }

    public static final String EXTRA_PAIR_REMOTE_MAC="extra_pair_remote_mac";
    private void handPairInfo(byte[] command){
        int status = command[6];
        String pairMac = String.format("%02X:%02X:%02X:%02X:%02X:%02X",command[7],command[8],command[9],command[10],command[11],command[12]);

        callbackPairInfo(status,pairMac);

        Intent intent = new Intent(ACTION_BLE_PAIR_INFO);
        intent.putExtra(EXTRA_STATUS,status);
        intent.putExtra(EXTRA_PAIR_REMOTE_MAC,pairMac);
        sendLocalBroadcast(intent);
    }


    private int firmwareInfoCRC16=0;
    private void handFirmwareData(byte[] command){

        int type = command[6];
        if(type==1){
            dataBytes.clear();
            // 系统信息
            totalPack = ((command[7]&0xFF)<<8) |(command[8]&0xFF); // 总包数
            firmwareInfoCRC16 = ((command[9]&0xFF)<<8) |(command[10]&0xFF);
            byte deviceType = command[11];
            Log.i(TAG, "handFirmwareData: totalPack="+totalPack);
            Log.i(TAG, "handFirmwareData: firmwareInfoCRC16 ="+ String.format("0x%02X",firmwareInfoCRC16));
            Log.i(TAG, "handFirmwareData: deviceType="+deviceType);
            // 获取第一包 固件信息
            sendGetFirmwareInfoDetail( 0,deviceType);
        }

    }



    private void handModifyKey(byte[] command){
        int status = command[6];
        callbackModifyKey(status);
        Intent intent = new Intent(ACTION_BLE_MODIFY_KEY);
        intent.putExtra(EXTRA_MODIFY_KEY_STATUS,status);
        sendLocalBroadcast(intent);
    }


    @Override
    protected void callbackCommandError(int status) {
        Intent intent = new Intent(ACTION_BLE_COMMAND_ERROR);
        intent.putExtra(EXTRA_ERROR_STATUS,status);
        sendLocalBroadcast(intent);
    }



    private void handCommandInfo(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        int voltage = ((command[6]&0xFF)<<8) | (command[7]&0xFF);
        int carportStatus = command[8]&0xFF;
        String version = String.format("%s.%s.%s",command[10],command[11],command[12]);
        Log.i(TAG, "handCommandInfo: voltage="+voltage);
        callbackCarportInfo(voltage,carportStatus,version);

        Intent intent = new Intent(ACTION_BLE_CARPORT_INFO);
        intent.putExtra(EXTRA_VOLTAGE,voltage);
        intent.putExtra(EXTRA_VERSION,version);
        intent.putExtra(EXTRA_CARPORT_STATUS,carportStatus);
        sendLocalBroadcast(intent);
    }
    private void handCommandModel(byte[] command){
        Log.i(TAG, "handCommandModel: 设备信息");
        int result =   (command[6]&0xFF);

        // 返回设置成功
        sendModelSetResponse();
    }


    private void handCommandMacGet(byte[] command){
        Log.i(TAG, "handCommandMacGet: 获取车位锁配对MAC" );
        int result =   (command[6]&0xFF);
        if(result==1){
            byte[] macBytes = new byte[6];
            for(int i=0;i<6;i++){
                macBytes[i]=command[i+7];
            }

            callbackCarportPairMac(macBytes);
            callbackCarportPairMac(MacUtils.getMacStr(macBytes));


        }
    }

    private void handCommandDown(byte[] command){
        Log.i(TAG, "handCommandDown: 不拦车" );
        int status = command[6];
        long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);

        Log.i(TAG, "handCommandDown: timestamp="+timestamp);
        Log.i(TAG, "handCommandDown: status="+status);
        Intent intent = new Intent(ACTION_BLE_CARPORT_DOWN);
        intent.putExtra(EXTRA_CARPORT_STATUS, status);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        sendLocalBroadcast(intent);

        if(status==0){
            Log.i(TAG, "handCommandDown: status= 开始不拦车");
        }else if(status==1){
            Log.i(TAG, "handCommandDown: status= 不拦车成功");
            sendCarportDownResponse();
        }else if(status==2){
            Log.i(TAG, "handCommandDown: status= 不拦车超时");
            sendCarportDownResponse();
        }


    }

    private void handCommandUp(byte[] command){
        Log.i(TAG, "handCommandUp: 拦车" );
        int status = command[6];
        // 拦车时间戳
        long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);
        Log.i(TAG, "handCommandUp: 时间戳="+ timestamp);

        // 不拦车时间
        long time= ((command[11]&0xFF)<<24 )|((command[12]&0xFF)<<16)|((command[13]&0xFF)<<8)| (command[14]&0xFF);
        Log.i(TAG, "handCommandUp: 不拦车时间="+ time);
        Intent intent = new Intent(ACTION_BLE_CARPORT_UP);
        intent.putExtra(EXTRA_CARPORT_STATUS, status);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_TIME, time);
        sendLocalBroadcast(intent);
        if(status==1 || status==2){

            sendCarportUpResponse();
        }
    }


    /**
     * 使用默认 OmniW4GX 发送获取KEY指令
     * @return 发送的指令
     */
    public byte[] sendGetKey(){
        Log.i(TAG, "sendGetKey: 发送获取操作KEY指令");
        String deviceKey ="OmniW4GX";
        byte[] crcOrder= CommandUtil.getCRCCommunicationKey(deviceKey);

        return writeToDevice(crcOrder);
    }

    /**
     * 使用设备KEY 去获取通信KEY
     * @param deviceKey  设备KEY
     * @return 发送的指令
     */
    public byte[] sendGetKey(String deviceKey){
        byte[] crcOrder= CommandUtil.getCRCCommunicationKey(deviceKey);
        return writeToDevice(crcOrder);
    }

    /**
     * 更新 设备KEY指令
     * @param newDeviceKey  新的KEY
     * @return 发送的指令
     */
    public byte[] sendModifyDeviceKey(String newDeviceKey){
        Log.i(TAG, "sendModifyDeviceKey: 发送修改设备KEY指令");

        byte[] crcOrder= CommandUtil.getCRCModifyDeviceKey(mBLECommunicationKey, newDeviceKey);

        return writeToDevice(crcOrder);
    }

    /**
     * 请求自定义的KEY ，恢复到默认的KEY
     * @return 发送的指令
     */
    public byte[] sendClearDeviceKey( ){
        Log.i(TAG, "sendClearDeviceKey: 发送清除设备KEY指令");

        byte[] crcOrder= CommandUtil.getCRCClearDeviceKey(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }

    public byte[] sendGetOldData( ){
        Log.i(TAG, "sendClearDeviceKey: 发送清除设备KEY指令");
        byte[] crcOrder= CommandUtil.getCRCGetOldData(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }
    public byte[] sendClearOldData( ){
        Log.i(TAG, "sendClearDeviceKey: 发送清除设备KEY指令");

        byte[] crcOrder= CommandUtil.getCRCClearOldData(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }

    /**
     * 获取 车位锁配对的遥控的MAC 地址
     * @return
     */
    public byte[] sendGetPairInfo( ){
        Log.i(TAG, "sendGetPairInfo: 获取车位锁已经配对了的遥控器 MAC 地址 ");

        byte[] crcOrder= CommandUtil.getCRCGetPairInfo(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }



    public byte[] sendSystemConfig(int pack,byte[] data){
        byte[] crcOrder= CommandUtil.getCRCTransmissionData(pack,data);
        Log.i(TAG, "sendSystemConfig: data[]="+ PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }


    /**
     * 发送获取固件信息指令
     * @return 发送的指令
     */
    public byte[] sendGetFirmwareInfo(){
        Log.i(TAG, "sendGetFirmwareInfo:获取固件信息");
        byte[] crcOrder= CommandUtil.getCRCFirmwareInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }



    public byte[] sendGetFirmwareInfoDetail(int npack,byte deviceType){
        Log.i(TAG, "sendGetFirmwareInfo:获取固件信息");
        byte[] crcOrder= CommandUtil.getCRCFirmwareInfoDetail(mBLECommunicationKey,npack,deviceType);
        return writeToDevice(crcOrder);
    }

    public byte[] sendModel(byte model){
        Log.i(TAG, "sendModel:发送获取操作KEY指令");
        byte[] crcOrder= CommandUtil.getCRCModelCommand(mBLECommunicationKey,model);
        return writeToDevice(crcOrder);
    }






    /**
     *
     * @param MAC 遥控器MAC地址
     * @return
     */
    public byte[] sendPairRemote(byte[] MAC){
        Log.i(TAG, "sendPairRemote:发送 车位锁 配对遥控器 指令");
        Log.i(TAG, "sendPairRemote: 遥控器的MAC地址="+ PrintUtil.toHexString(MAC));

        byte[] crcOrder= CommandUtil.getCRCPairRemote(mBLECommunicationKey,MAC);
        Log.i(TAG, "sendPairRemote: 车位锁配对遥控器指令="+ PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }

    public byte[] sendGetPairMacCommand(){
        byte[] crcOrder= CommandUtil.getCRCGetLocalMac(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendCarportDown(){
        Log.i(TAG, "sendScooterOpen:发送不拦车指令");
        long timestamp = System.currentTimeMillis()/1000;
        byte[] crcOrder= CommandUtil.getCRCCarportDown(mBLECommunicationKey,1,timestamp);
        return writeToDevice(crcOrder);
    }

    /**
     *
     * @param uid   用户ID
     * @param mode  不拦车模式  1：手动模式,3:自动模式
     * @return
     */
    public byte[] sendCarportDown(int uid,byte mode){
        Log.i(TAG, "sendScooterOpen:发送不拦车指令");
        long timestamp = System.currentTimeMillis()/1000;
        byte[] crcOrder= CommandUtil.getCRCCarportDown(mBLECommunicationKey,mode,uid,timestamp);
        return writeToDevice(crcOrder);
    }

    /**
     *
     * @param uid   用户ID
     * @param mode  不拦车模式  1：手动模式,3:自动模式
     * @param timestamp  不拦车 时间戳(秒)，
     * @return
     */
    public byte[] sendCarportDown(int uid,byte mode,long timestamp){

        byte[] crcOrder= CommandUtil.getCRCCarportDown(mBLECommunicationKey,mode,uid,timestamp);
        Log.i(TAG, "sendCarportDown: 发送不拦车指令="+ PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }

    public void startAutoSendDown(int uid,boolean isStart){
        if(isStart){
            carportMode = MODE_CARPORT_AUTO;
            Message msg = new Message();
            msg.what =HANDLER_AUTO_SEND_DOWN;

            msg.arg1=uid;
            carportHandler.sendMessage(msg);
        }

    }

    public void stopAutoSendDown(  ){
        carportMode = MODE_CARPORT_MANUAL;
    }


    public byte[] sendLogInfo(byte deviceType){
        isLogData = true;
        byte[] crcCommand = CommandUtil.getCRCFirmwarLogInfo(mBLECommunicationKey,deviceType);
        return writeToDevice(crcCommand);
    }

    public byte[] sendDeviceInfo(){
        Log.i(TAG, "sendScooterOpen:发送 获取车位锁信息指令");
        byte[] crcOrder= CommandUtil.getCRCDeviceInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendCarportUp(){
        Log.i(TAG, "sendScooterOpen:发送 拦车指令");
        byte[] crcOrder= CommandUtil.getCRCCarportUp(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendCarportUpResponse(){
        Log.i(TAG, "sendScooterCloseResponse:发送 拦车指令回应");
        byte[] crcOrder= CommandUtil.getCRCCarportUpResponse(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendModelSetResponse(){
        Log.i(TAG, "sendScooterOpen:发送 设置模式 回应");
        byte[] crcOrder= CommandUtil.getCRCModelResponse(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendCarportDownResponse(){
        Log.i(TAG, "sendScooterOpen:发送 不拦车指令回应");
        byte[] crcOrder= CommandUtil.getCRCCarportDownResponse(mBLECommunicationKey);
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






    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic ){
        if(mBLEAdapter==null||mBLEGatt==null){
            Log.w(TAG, "setCharacteristicNotification: BluetoothAdapter not initialized");
            return ;
        }
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



    public class LocalBinder extends Binder {
        public CarportService getService(){
            return CarportService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 进入服务的初始化方法");

        //设置广播监听
        registerReceiver(bleStateReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        //注册本地广播，主要是扫描设备的时候用到
        IntentFilter ifScanDevice = new IntentFilter();

        ifScanDevice.addAction(ACTION_BLE_SCAN_STOP);
        ifScanDevice.addAction(ACTION_BLE_SCAN_START);
        ifScanDevice.addAction(ACTION_BLE_SCAN_DEVICE);
        LocalBroadcastManager.getInstance(this).registerReceiver(sanDeviceReceiver, ifScanDevice);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
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
