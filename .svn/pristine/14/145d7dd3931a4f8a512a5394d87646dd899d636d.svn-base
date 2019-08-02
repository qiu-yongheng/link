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
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.ble.library.model.GattAttributes;
import com.omni.ble.library.utils.ScooterCommandUtil;
import com.omni.lib.utils.CRCUtil;
import com.omni.lib.utils.PrintUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Description:滑板车 蓝牙后台服务 <br />
 * Created by CxiaoX on 2015/12/15 14:42.
 */
public class ScooterService extends BaseService {
    private final static String TAG=ScooterService.class.getSimpleName();

    @Override
    public UUID getServiceUUID( ) {
        return GattAttributes.UUID_SCOOTER_SERVICE;
    }


    @Override
    public UUID getWriteUUID() {
        return GattAttributes.UUID_SCOOTER_CHARACTERISTIC_WRITE;
    }

    @Override
    public UUID getNotifyUUID() {
        return GattAttributes.UUID_SCOOTER_CHARACTERISTIC_NOTIFY;
    }

    private final IBinder mBinder = new LocalBinder();


    /**
     * 获取到通信KEY的广播
     */
    public final static String ACTION_BLE_GET_KEY="com.omni.ble.library.ACTION_BLE_GET_KEY";
    /**
     * 获取通信KEY错误的广播
     */
    public final static String ACTION_BLE_GET_KEY_ERROR="com.omni.ble.library.ACTION_BLE_GET_KEY_ERROR";


    /**
     * 指令错误的回应
     */
    public final static String ACTION_BLE_COMMAND_ERROR="com.omni.ble.library.ACTION_BLE_COMMAND_ERROR";

    /**
     * 获取滑板车信息的广播
     */
    public final static String ACTION_BLE_DEVICE_INFO="com.omni.ble.library.ACTION_BLE_DEVICE_INFO";

    /**
     * 获取滑板车信息的广播
     */
    public final static String ACTION_BLE_SCOOTER_INFO="com.omni.ble.library.ACTION_BLE_SCOOTER_INFO";
    public final static String ACTION_BLE_SCOOTER_OLD_DATA="com.omni.ble.library.ACTION_BLE_SCOOTER_OLD_DATA";
    public final static String ACTION_BLE_SCOOTER_CLEAR_OLD_DATA="com.omni.ble.library.ACTION_BLE_SCOOTER_CLEAR_OLD_DATA";
    public final static String ACTION_BLE_SCOOTER_READ_ID="com.omni.ble.library.ACTION_BLE_SCOOTER_READ_ID";
    public final static String ACTION_BLE_CONTROL_EXTR_DEVICE="com.omni.ble.library.ACTION_BLE_CONTROL_EXTR_DEVICE";
    public final static String ACTION_BLE_SCOOTER_SET="com.omni.ble.library.ACTION_BLE_SCOOTER_SET";

    /**
     * 获取设备自身的KEY的广播
     */
    public final static String ACTION_BLE_GET_MAC="com.omni.ble.library.ACTION_BLE_GET_MAC";

    /**
     * 滑板车锁 开锁 广播
     */
    public final static String ACTION_BLE_SCOOTER_OPEN ="com.omni.ble.library.ACTION_BLE_SCOOTER_OPEN";
    /**
     * 滑板车锁 关锁 广播
     */
    public final static String ACTION_BLE_SCOOTER_CLOSE ="com.omni.ble.library.ACTION_BLE_SCOOTER_CLOSE";

    /**
     * 固件信息，固件信息的完整值
     */
    public final static String ACTION_BLE_SCOOTER_FW_INFO ="com.omni.ble.library.ACTION_BLE_SCOOTER_FW_INFO";
    /**
     * 获取固件信息的过程
     */
    public final static String ACTION_BLE_SCOOTER_FW_INFO_ING ="com.omni.ble.library.ACTION_BLE_SCOOTER_FW_INFO_ING";


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



    /** 开锁扫描 */
    public static final String ACTION_BLE_SCAN_START="com.omni.ble.library.ACTION_BLE_SCAN_START";
    /** 停止扫描 */
    public static final String ACTION_BLE_SCAN_STOP="com.omni.ble.library.ACTION_BLE_SCAN_STOP";



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


    public static final String ACTION_BLE_STATE_ON="com.omni.ble.library.ACTION_BLE_STATE_ON";
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


    private List<Byte> dataBytes= new ArrayList<>();

    @Override
    public void onHandFirmwareDataCommand(int nPack, byte[] command) {
        Log.i(TAG, "onHandFirmwareDataCommand: 第几包数据="+nPack);
        Log.i(TAG, "onHandFirmwareDataCommand:  固件信息="+PrintUtil.toHexString(command));

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
            Log.i(TAG, "onHandFirmwareDataCommand: 计算的CRC16="+String.format("0x%02X",calcCRC16));
            String firmwareData = new String(allFirmwareData);
            Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);

            Intent intent = new Intent(ACTION_BLE_SCOOTER_FW_INFO);
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


            // 将获取固件信息的过程 回调出去。
            Intent intent = new Intent(ACTION_BLE_SCOOTER_FW_INFO_ING);

            intent.putExtra(EXTRA_FIRMWARE_NPACK,nPack);
            sendLocalBroadcast(intent);


            // 非最后一包数据
            //TODO 回调出去
//            sendGetFirmwareInfoDetail(nPack+1,(byte)0x8A);
        }

    }

    @Override
    public void onHandNotifyCommand(String mac, byte[] command) {
        super.onHandNotifyCommand(mac, command);
        Log.i(TAG, "onHandNotifyCommand: command CODE= "+String.format("0x%02X",command[5]));
        Log.i(TAG, "onHandNotifyCommand: 解密后= "+PrintUtil.toHexString(command));
        switch (command[5]){
            case CommandType.SCOOTER_OPEN:
                handCommandOpen(command);
                break;
            case CommandType.SCOOTER_CLOSE:
                handCommandClose(command);
                break;
            case CommandType.CARPORT_DEVICE_INFO:
                handCommandInfo(command);
                break;

            case CommandType.DEVICE_DATA:
                handFirmwareData(command);
                break;
            case CommandType.SCOOTER_INFO:
                handScooterInfo(command);
                break;
            case CommandType.SCOOTER_OLD_DATA:
                handScooterOldData(command);
                break;
            case CommandType.CLEAR_DATA:
                handScooterClearOldData(command);
                break;
            case CommandType.SCOOTER_READ_CARD_ID:
                handScooterReadCardId(command);
                break;
            case CommandType.SCOOTER_POPUP:
                handScooterExtrDevcie(command);
                break;
            case CommandType.SCOOTER_SET:
                handScooterSet(command);
                break;
        }
    }









    @Override
    protected void callbackLogData(byte[] logData) {

        Intent intent = new Intent(ACTION_BLE_LOG_DATA);

        intent.putExtra("data",logData);
        sendLocalBroadcast(intent);


    }

    /**
     * 蓝牙设备返回的 车位锁信息
     * @param voltage  车位锁电池电压值
     * @param carportStatus 车位锁拦车状态
     * @param version 车锁硬件版本号
     */
    protected void callbackScooterIotInfo(int voltage, int carportStatus, String version ){}


    /**
     * 滑板车信息的回调
     * @param power  电量百分比
     * @param speedMode 速度模式
     * @param speed 当前速度
     * @param mileage 单次骑行里程
     * @param prescientMileage 剩余里程
     */
    protected void callbackScooterInfo(int power,int speedMode,int speed,int mileage,int prescientMileage ){}
    protected void callbackScooterOldData(long timestamp,long openTime,long   userId ){}


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
    public static  final String EXTRA_FIRMWARE_NPACK="firmware_npack";
    public static  final String EXTRA_MODIFY_KEY_STATUS="status";
    public static  final String EXTRA_ERROR_STATUS="error_status";





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
            Log.i(TAG, "handFirmwareData: firmwareInfoCRC16 ="+String.format("0x%02X",firmwareInfoCRC16));
            Log.i(TAG, "handFirmwareData: deviceType="+deviceType);
            // 获取第一包 固件信息
            sendGetFirmwareInfoDetail( 0,deviceType);
        }
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
        callbackScooterIotInfo(voltage,carportStatus,version);

        Intent intent = new Intent(ACTION_BLE_DEVICE_INFO);
        intent.putExtra(EXTRA_VOLTAGE,voltage);
        intent.putExtra(EXTRA_VERSION,version);
        intent.putExtra(EXTRA_CARPORT_STATUS,carportStatus);
        sendLocalBroadcast(intent);
    }

    public static  final String EXTRA_SCOOTER_CLEAR_OLD_STATUS ="scooter.clear.old.status";
    public static  final String EXTRA_EXTR_DEVICE_TYPE ="scooter.extr.device.type";
    public static  final String EXTRA_EXTR_DEVICE_STATUS ="scooter.extr.device.status";
    public static  final String EXTRA_SCOOTER_SET_STATUS ="scooter.set.status";
    public static  final String EXTRA_RFID_READ_STATUS ="scooter.rfid.read.status";
    public static  final String EXTRA_RFID_READ_ID ="scooter.rfid.read.ID";
    public static  final String EXTRA_SCOOTER_OLD_TIMESTAMP="scooter.old.timestamp";
    public static  final String EXTRA_SCOOTER_OLD_USERID="scooter.old.userid";
    public static  final String EXTRA_SCOOTER_OLD_OPENTIME="scooter.old.opentime";

    public static  final String EXTRA_SCOOTER_SPEED="scooter.speed";
    public static  final String EXTRA_SCOOTER_POWER="scooter.power";
    public static  final String EXTRA_SCOOTER_SPEED_MODE="scooter.speed.mode";
    public static  final String EXTRA_SCOOTER_MILEAGE="scooter.mileage";
    public static  final String EXTRA_SCOOTER_PRESCIENT_MILEAGE="scooter.prescient.mileage";
    private void handScooterInfo(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        int power = ( command[6]&0xFF); // 电量百分比
        int mode = command[7]&0xFF; // 速度模式
        int speed = ((command[8]&0xFF)<<8) | (command[9]&0xFF); // 0.1 km/h
        int mileage = ((command[10]&0xFF)<<8) | (command[11]&0xFF); //10 m
        int prescientMileage = ((command[12]&0xFF)<<8) | (command[13]&0xFF); //10m

        callbackScooterInfo(power,mode,speed,mileage,prescientMileage);

        Intent intent = new Intent(ACTION_BLE_SCOOTER_INFO);
        intent.putExtra(EXTRA_SCOOTER_POWER,power);
        intent.putExtra(EXTRA_SCOOTER_SPEED_MODE,mode);
        intent.putExtra(EXTRA_SCOOTER_SPEED,speed);
        intent.putExtra(EXTRA_SCOOTER_MILEAGE,mileage);
        intent.putExtra(EXTRA_SCOOTER_PRESCIENT_MILEAGE,prescientMileage);
        sendLocalBroadcast(intent);
    }
    private void handScooterOldData(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        long timestamp= ((command[6]&0xFF)<<24 )|((command[7]&0xFF)<<16)|((command[8]&0xFF)<<8)| (command[9]&0xFF);
        long openTime= ((command[10]&0xFF)<<24 )|((command[11]&0xFF)<<16)|((command[12]&0xFF)<<8)| (command[13]&0xFF);
        long userId= ((command[14]&0xFF)<<24 )|((command[15]&0xFF)<<16)|((command[16]&0xFF)<<8)| (command[17]&0xFF);



        callbackScooterOldData(timestamp,openTime,userId );

        Intent intent = new Intent(ACTION_BLE_SCOOTER_OLD_DATA);
        intent.putExtra(EXTRA_SCOOTER_OLD_TIMESTAMP,timestamp);
        intent.putExtra(EXTRA_SCOOTER_OLD_USERID,userId);
        intent.putExtra(EXTRA_SCOOTER_OLD_OPENTIME,openTime);
        sendLocalBroadcast(intent);
    }

    private void handScooterClearOldData(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        int status  = command[6];
        callbackScooterClearOldData(status );

        Intent intent = new Intent(ACTION_BLE_SCOOTER_CLEAR_OLD_DATA);
        intent.putExtra(EXTRA_SCOOTER_CLEAR_OLD_STATUS,status);

        sendLocalBroadcast(intent);
    }

    private void handScooterReadCardId(byte[] command){
        Log.i(TAG, "handCommandInfo: 设备信息" );
        // 0 启动读卡，1 读卡成功
        int status  = command[6];

        long high1=(command[7]&0xFF);

        long high= (high1<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);
//        Log.i(TAG,"high:"+high);

        long low1 = command[11] &0xFF;
        long low= (low1<<24)|((command[12]&0xFF)<<16)|((command[13]&0xFF)<<8)| (command[14]&0xFF);
//        Log.i(TAG,"low:"+low);
        long cardId = (high<<32) | (low  );


        //TODO DD
        callbackRFIDReadId(status,String.format("%016X",cardId) );


    }


    private void handScooterExtrDevcie(byte[] command){
        Log.i(TAG, "handScooterExtrDevcie: 设备信息" );
        int controlType = command[6];
        int status = command[7];

        callbackControlExtrDevice(controlType,status);
    }
    private void handScooterSet(byte[] command){
        Log.i(TAG, "handScooterSet:  滑板车设置操作" );
        int status = command[6];


        callbackScooterSet( status);
    }

    public void callbackScooterSet( int status) {

        Intent intent = new Intent(ACTION_BLE_SCOOTER_SET);

        intent.putExtra(EXTRA_SCOOTER_SET_STATUS,status);

        sendLocalBroadcast(intent);
    }

    public void callbackControlExtrDevice(int controlType,int status) {

        Intent intent = new Intent(ACTION_BLE_CONTROL_EXTR_DEVICE);
        intent.putExtra(EXTRA_EXTR_DEVICE_TYPE,controlType);
        intent.putExtra(EXTRA_EXTR_DEVICE_STATUS,status);

        sendLocalBroadcast(intent);
    }

    public void callbackRFIDReadId(int status,String cardId) {

        Intent intent = new Intent(ACTION_BLE_SCOOTER_READ_ID);
        intent.putExtra(EXTRA_RFID_READ_STATUS,status);
        intent.putExtra(EXTRA_RFID_READ_ID,cardId);

        sendLocalBroadcast(intent);
    }

    public void callbackScooterClearOldData(int status) {
    }





    private void handCommandOpen(byte[] command){
        Log.i(TAG, "handCommandOpen: 不拦车" );
        int status = command[6];
        long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);

        Log.i(TAG, "handCommandOpen: timestamp="+timestamp);
        Log.i(TAG, "handCommandOpen: status="+status);
        Intent intent = new Intent(ACTION_BLE_SCOOTER_OPEN);
        intent.putExtra(EXTRA_CARPORT_STATUS, status);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        sendLocalBroadcast(intent);

        if(status==0){
            Log.i(TAG, "handCommandOpen: status= 开始不拦车");
        }else if(status==1){
            Log.i(TAG, "handCommandOpen: status= 不拦车成功");
            sendScooterOpenResponse();
        }else if(status==2){
            Log.i(TAG, "handCommandOpen: status= 不拦车超时");
            sendScooterOpenResponse();
        }


    }

    private void handCommandClose(byte[] command){
        Log.i(TAG, "handCommandClose: 关锁" );
        int status = command[6];
        // 拦车时间戳
        long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);
        Log.i(TAG, "handCommandClose: 时间戳="+ timestamp);

        // 不拦车时间
        long time= ((command[11]&0xFF)<<24 )|((command[12]&0xFF)<<16)|((command[13]&0xFF)<<8)| (command[14]&0xFF);
        Log.i(TAG, "handCommandClose: 不拦车时间="+ time);
        Intent intent = new Intent(ACTION_BLE_SCOOTER_CLOSE);
        intent.putExtra(EXTRA_CARPORT_STATUS, status);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_TIME, time);
        sendLocalBroadcast(intent);
//        if(status==1 || status==2){
//
//            sendScooterCloseResponse();
//        }
    }


    public byte[] sendGetKey(){
        Log.i(TAG, "sendGetKey: 发送获取操作KEY指令");
        String deviceKey ="OmniW4GX";
        byte[] crcOrder= ScooterCommandUtil.getCRCCommunicationKey(deviceKey);

        return writeToDevice(crcOrder);
    }


    public byte[] sendGetOldData( ){
        Log.i(TAG, "sendClearDeviceKey: 发送获取旧数据指令");
        byte[] crcOrder= ScooterCommandUtil.getCRCGetOldData(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    /**
     * 滑板车整体关机
     * @param opt 1-关机，2-开机
     * @return
     */
    public byte[] sendShutDown(byte opt){
        Log.i(TAG, "sendShutDown: 滑板车整车开关机");
        byte[] crcOrder= ScooterCommandUtil.getCRCShutDown(mBLECommunicationKey,opt);
        Log.i(TAG, "sendShutDown: "+PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }

    public byte[] sendShutDown(byte orderType,byte opt){
        Log.i(TAG, "sendClearDeviceKey: 发送关机指令");
        byte[] crcOrder= ScooterCommandUtil.getCRCShutDown(mBLECommunicationKey,orderType,opt);
        return writeToDevice(crcOrder);
    }


    public byte[] sendClearOldData( ){
        Log.i(TAG, "sendClearDeviceKey: 发送清除旧数据指令");

        byte[] crcOrder= ScooterCommandUtil.getCRCClearOldData(mBLECommunicationKey);

        return writeToDevice(crcOrder);
    }



    public byte[] sendGetKey(String deviceKey){
        byte[] crcOrder= ScooterCommandUtil.getCRCCommunicationKey(deviceKey);
        Log.i(TAG,"sendGetKey =>" + Arrays.toString(crcOrder));
        return writeToDevice(crcOrder);
    }

    public byte[] sendGetKey(byte[] deviceKey){
        byte[] crcOrder= ScooterCommandUtil.getCRCCommunicationKey(deviceKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendSystemConfig(int pack,byte[] data){
        byte[] crcOrder= ScooterCommandUtil.getCRCTransmissionData(pack,data);
        Log.i(TAG, "sendSystemConfig: data[]="+PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }

    /**
     * 彩灯控制
     * @param openStatus 控制彩灯开关 0无效, 1关灯, 2开灯
     * @param color 彩灯颜色, 4个字节
     * @param speed 彩灯速度
     * @param brightness 彩灯亮度
     * @param effect 彩灯效果
     * @return 发送数据
     */
    public byte[] sendColorLight(int openStatus, int color, int speed, int brightness, int effect) {
        byte[] crcOrder= ScooterCommandUtil.getCRCColorLight(openStatus, color, speed, brightness, effect, mBLECommunicationKey);
        Log.d(TAG, "彩灯控制 加密后: " + PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }


    public byte[] sendGetFirmwareInfo(){
        Log.i(TAG, "sendGetFirmwareInfo:获取固件信息");
        byte[] crcOrder= ScooterCommandUtil.getCRCFirmwareInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }



    public byte[] sendGetFirmwareInfoDetail(int npack,byte deviceType){
        Log.i(TAG, "sendGetFirmwareInfo:获取固件信息");
        byte[] crcOrder= ScooterCommandUtil.getCRCFirmwareInfoDetail(mBLECommunicationKey,npack,deviceType);
        return writeToDevice(crcOrder);
    }

    public byte[] sendScooterSet(byte light,byte speed,byte you,byte tLight){
        byte[] crcOrder= ScooterCommandUtil.getCRCScooterSet(mBLECommunicationKey,light,speed,you,tLight);
        return writeToDevice(crcOrder);
    }


    public byte[] sendGetPopUp( ){
        Log.i(TAG, "sendGetPopUp:电池开");
        byte[] crcOrder=ScooterCommandUtil.getCRCScooterPopUp(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendExtraDevice(byte type ){
        Log.i(TAG, "sendGetPopUp:电池开");
        byte[] crcOrder=ScooterCommandUtil.getCRCScooterExtraDevice(mBLECommunicationKey,type);
        return writeToDevice(crcOrder);
    }

    public byte[] sendReadCardId( ){
        Log.i(TAG, "sendReadCardId:读取RFID 卡号");
        byte[] crcOrder=ScooterCommandUtil.getCRCReadId(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    public byte[] sendScooterOpen(){
        Log.i(TAG, "sendScooterOpen:发送滑板车开锁指令");
        long timestamp = System.currentTimeMillis()/1000;

        return sendScooterOpen(1,(byte)1);
    }

    /**
     *
     * @param uid   用户ID
     * @param mode  不拦车模式  1：手动模式,3:自动模式
     * @return
     */
    public byte[] sendScooterOpen(int uid, byte mode){
        Log.i(TAG, "sendScooterOpen:发送滑板车开锁指令");
        long timestamp = System.currentTimeMillis()/1000;
        return sendScooterOpen(uid,mode,timestamp);
    }

    /**
     *
     * @param uid   用户ID
     * @param mode  不拦车模式  1：手动模式,3:自动模式
     * @param timestamp  不拦车 时间戳(秒)，
     * @return
     */
    public byte[] sendScooterOpen(int uid, byte mode, long timestamp){

        byte[] crcOrder= ScooterCommandUtil.getCRCScooterOpen(mBLECommunicationKey,mode,uid,timestamp);
        return writeToDevice(crcOrder);
    }



    public byte[] sendDeviceInfo(){
        Log.i(TAG, "sendDeviceInfo:发送 滑板车位锁信息指令");
        byte[] crcOrder= ScooterCommandUtil.getCRCDeviceInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    /**
     * 发送获取滑板车信息指令，包括滑板车电量，滑板车模式，当前车速，单次骑行里程，剩余骑行里程
     * @return
     */
    public byte[] sendScooterInfo(){
        Log.i(TAG, "sendScooterInfo:发送 滑板车位锁信息指令");
        byte[] crcOrder= ScooterCommandUtil.getCRCScooterInfo(mBLECommunicationKey);
        Log.i(TAG, "sendScooterInfo: data[]="+PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }

    /**
     * 蓝牙关锁指令
     * @return
     */
    public byte[] sendScooterClose(){
        Log.i(TAG, "sendScooterClose:发送 关车指令");

        byte[] crcOrder= ScooterCommandUtil.getCRCScooterClose(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    /**
     * 滑板车蓝牙关锁 ，回应指令
     * @return
     */
    public byte[] sendScooterCloseResponse(){
        Log.i(TAG, "sendScooterCloseResponse:发送 关锁指令 回应");
        byte[] crcOrder= ScooterCommandUtil.getCRCScooterCloseResponse(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }



    /**
     * 滑板车蓝牙开锁 ，回应指令
     * @return
     */
    public byte[] sendScooterOpenResponse(){
        Log.i(TAG, "sendScooterOpenResponse:发送 不拦车指令回应");
        byte[] crcOrder= ScooterCommandUtil.getCRCScooterOpenResponse(mBLECommunicationKey);
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



    public class LocalBinder extends Binder{
        public ScooterService getService(){
            return ScooterService.this;
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
