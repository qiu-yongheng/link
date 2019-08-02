package com.omni.ble.library.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.omni.ble.library.utils.BikeLockCommand;
import com.omni.ble.library.utils.CRCUtil;
import com.omni.lib.utils.PrintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 3in1 马蹄锁 操作的service
 * Created by solo on 2019/05/11.
 */

public class BikeLockService extends BaseService {
    private static final String TAG="BikeLockService";


    /**
     * ACTION 3in1 马蹄锁 开锁广播(0x21)
     */
    public static final String ACTION_BIKE_LOCK_OPEN ="com.omni.ble.library.ACTION_BIKE_LOCK_OPEN";
    /**
     * ACTION 3in1 马蹄锁 关锁广播(0x22)
     */
    public static final String ACTION_BIKE_LOCK_CLOSE ="com.omni.ble.library.ACTION_BIKE_LOCK_CLOSE";

    /**
     * 3in1马蹄锁 锁状态信息(0x31)
     */
    public static final String ACTION_BIKE_LOCK_INFO ="com.omni.ble.library.ACTION_BIKE_LOCK_INFO";
    /**
     * 固件信息，正在获取固件信息的过程
     */
    public static final String ACTION_BLE_BIKE_LOCK_FW_INFO_ING="com.omni.ble.library.ACTION_BLE_BIKE_LOCK_FW_INFO_ING";
    /**
     * 固件信息，固件信息的完整值
     */
    public final static String ACTION_BLE_BIKE_LOCK_FW_INFO ="com.omni.ble.library.ACTION_BLE_BIKE_LOCK_FW_INFO";
    /**
     * 清除旧数据
     */
    public final static String ACTION_BIKE_LOCK_CLEAR_OLD_DATA ="com.omni.ble.library.ACTION_BIKE_LOCK_CLEAR_OLD_DATA";
    /**
     * 获取旧数据
     */
    public final static String ACTION_BIKE_LOCK_OLD_DATA ="com.omni.ble.library.ACTION_BIKE_LOCK_OLD_DATA";

    public static final String EXTRA_LOCK_OLD_TIMESTAMP="old_timestamp";
    public static final String EXTRA_LOCK_OLD_TIME="old_time";
    public static final String EXTRA_LOCK_OLD_UID="old_uid";


    public static final String EXTRA_LOCK_CLEAR_STATUS="clear_status";
    public static  final String EXTRA_FIRMWARE_DATA="firmware_data";
    public static  final String EXTRA_FIRMWARE_NPACK="firmware_npack";
    public static  final String EXTRA_FIRMWARE_TOTAL_PACK="firmware_total_pack";

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB7");
    }

    @Override
    public UUID getWriteUUID() {
        return UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CBA");
    }

    @Override
    public UUID getNotifyUUID() {
        return UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB8");
    }

    @Override
    public void onCharacteristicChangedCallback(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {


        String deviceAddress = gatt.getDevice().getAddress();
        final byte[] values=characteristic.getValue();

        Log.i(TAG, "onCharacteristicChangedCallback: "+ PrintUtil.toHexString(values));

        if(isGetInfo && values.length==20){
            // 读取锁信息

            if(CRCUtil.CheckFirstCRC16(values)){
                byte[] command = new byte[values.length-2];
                System.arraycopy(values,2,command,0,values.length-2);
                onHandFirmwareDataCommand(command);
            }
            return ;
        }



        if(values.length==2 && values[0]==32&& values[1]==0) return ;



        int start=0;
        int copyLen = 0;
        for (int i = 0; i < values.length; i++) {
            if ((values[i] & 0xFF) == 0xFE) {
                start = i;
                int randNum = (values[i + 1] - 0x32) & 0xFF;
                int len = ((values[i + 4]) & 0xFF) ^ randNum;
                copyLen = len + 7; //16+
                break;

            }
        }

        if(copyLen==0)  return ;
        byte[] real=new byte[copyLen];
        System.arraycopy(values,start,real,0,copyLen);

        Log.i(TAG, "onCharacteristicChangedCallback: real="+ PrintUtil.toHexString(real));
        byte[] command = new byte[real.length-2];
        for(int i=0;i<command.length;i++) command[i] = real[i];

        int calcCRC = CRCUtil.calcCRC16(command);

        int valueCRC =((real[real.length-2]&0xFF)<<8) | (real[real.length-1]&0xFF);



        if(calcCRC== valueCRC){
            Log.i(TAG, "onCharacteristicChanged: CRC success");
            byte head = (byte) (real[1]-0x32);
            command[0]= real[0];
            command[1]=head;
            for(int i=2;i<real.length-2;i++){
                command[i] = (byte) (real[i] ^ head);
            }
            onHandNotifyCommand(deviceAddress,command);
        }else{
            Log.i(TAG, "onCharacteristicChanged: CRC fail");
        }
    }

    @Override
    public void onHandNotifyCommand(String mac, byte[] command) {
        Log.i(TAG, "onHandNotifyCommand data[]= "+ PrintUtil.toHexString(command));
        switch (command[3]){
            case BikeLockCommand.ORDER_GET_KEY:
                // get key
                handGetKeyCommand(mac,command);
                break;
            case BikeLockCommand.ORDER_UN_LOCK:
                handOpenCommand(command);
                break;
            case BikeLockCommand.ORDER_LOCK_CLOSE:
                // lock
                handLockCloseCommand(command);
                break;
            case BikeLockCommand.ORDER_INFO:
                handLockInfoCommand(command);
                break;
            case BikeLockCommand.ORDER_OLD_DATA:
                handOldDataCommand(command);
                break;
            case BikeLockCommand.ORDER_CLEAR_OLD:
                handClearOldDataCommand(command);
                break;
            case (byte)0xFA:
                handFirmwareData(command);
                break;
        }

    }

    private List<Byte> dataBytes= new ArrayList<>();
    int infoCurSavePack = -1;
    public void onHandFirmwareDataCommand(  byte[] command) {
        int nPack = ((command[0]&0xFF)<<8) | (command[1]&0xFF);
        Log.i(TAG, "onHandFirmwareDataCommand: 第几包数据="+nPack);
        Log.i(TAG, "onHandFirmwareDataCommand:  固件信息="+ PrintUtil.toHexString(command));
        if(infoCurSavePack!=nPack) {
            for (int i = 2; i < command.length; i++) {
                dataBytes.add(command[i]);
            }
            infoCurSavePack = nPack;
        }

        if(nPack==totalPack-1){
            // 最后一包数据
            // 将接收到的数据转换成String
            isGetInfo = false;
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

            // 将byte[] 数组转化成String
            String firmwareData = new String(allFirmwareData);
            Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);

            Intent intent = new Intent(ACTION_BLE_BIKE_LOCK_FW_INFO);
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
            Intent intent = new Intent(ACTION_BLE_BIKE_LOCK_FW_INFO_ING);
            // 当前返回的是第几包数据
            intent.putExtra(EXTRA_FIRMWARE_NPACK,nPack);
            intent.putExtra(EXTRA_FIRMWARE_TOTAL_PACK,totalPack);
            sendLocalBroadcast(intent);
        }

    }


    int firmwareInfoCRC16=0;
    /**
     * 处理固件总包数信息
     * @param command
     */
    private void handFirmwareData(byte[] command){
        int len = command[4];
        // 系统信息
        totalPack = ((command[5]&0xff)<<8) | (command[6]&0xff);
        firmwareInfoCRC16 = ((command[7]&0xff)<<8) | (command[8]&0xff);
        byte deviceType = command[9];
        Log.i(TAG, "handFirmwareData: totalPack="+totalPack);
        Log.i(TAG, "handFirmwareData: firmwareInfoCRC16 ="+ String.format("0x%02X",firmwareInfoCRC16));
        Log.i(TAG, "handFirmwareData: deviceType="+deviceType);
        // 获取第一包 固件信息
        sendGetFirmwareInfoDetail( 0,deviceType);


    }

    private boolean isGetInfo = false;
    /**
     * 获取固件信息，一包一包获取
     * @param npack
     * @param deviceType
     * @return
     */
    public byte[] sendGetFirmwareInfoDetail(int npack,byte deviceType){
        isGetInfo = true;
        byte[] crcOrder= BikeLockCommand.getCRCFirmwareInfoDetail(mBLECommunicationKey,npack,deviceType );
        return writeToDevice(crcOrder);
    }



    /**
     * 开关锁状态，0-开锁状态，1-关锁状态
     */
    public static final String EXTRA_LOCK_STATUS="status";
    public static final String EXTRA_LOCK_POWER="power";
    /**
     * 是否有旧数据，1没有，0有旧数据
     */
    public static final String EXTRA_LOCK_OLD="old";
    public static final String EXTRA_LOCK_INFO_TIMESTAMP ="info_timestamp";
    /**
     * 电动车电池电量
     */
    public static final String EXTRA_BIKE_POWER ="bike_power";

    /**
     * 马蹄锁状态信息(0x31)
     * @param command  解密后的指令
     */
    public void handLockInfoCommand(byte[] command){
        //0-开锁，1-关锁
        int lockStatus= command[5];
        int power = command[6]; // 电池电量
        // 1没有，0有
        int old = command[7];
        long timestamp =  ((command[8]&0xFF)<<24) | ((command[9]&0xFF)<<16) |((command[10]&0xFF)<<8) | (command[11] &0xFF);


        int bikePower = 0;// 电动车电池电量
        if(command[4]==8){
            // 长度是8的话，就是在最后面加了 电动车电池电量百分比
            bikePower = command[12];

        }
        Intent intent = new Intent(ACTION_BIKE_LOCK_INFO);
        intent.putExtra(EXTRA_BIKE_POWER,bikePower);
        intent.putExtra(EXTRA_LOCK_STATUS,lockStatus);
        intent.putExtra(EXTRA_LOCK_POWER,power);
        intent.putExtra(EXTRA_LOCK_OLD,old);
        intent.putExtra(EXTRA_LOCK_INFO_TIMESTAMP,timestamp);
        sendLocalBroadcast(intent);
    }
    private void handClearOldDataCommand(byte[] command) {
        int status= command[5];

        Intent intent = new Intent(ACTION_BIKE_LOCK_CLEAR_OLD_DATA);
        intent.putExtra(EXTRA_LOCK_CLEAR_STATUS,status);
        sendLocalBroadcast(intent);
    }

    private void handOldDataCommand(byte[] command) {
        long timestamp = ((command[5]&0xFF)<<24) | ((command[6]&0xFF)<<16) |((command[7]&0xFF)<<8) | (command[8] &0xFF);
        long openTime = ((command[9]&0xFF)<<24) | ((command[10]&0xFF)<<16) |((command[11]&0xFF)<<8) | (command[12] &0xFF);
        long uid = ((command[13]&0xFF)<<24) | ((command[14]&0xFF)<<16) |((command[15]&0xFF)<<8) | (command[16] &0xFF);

        Intent intent = new Intent(ACTION_BIKE_LOCK_OLD_DATA);
        intent.putExtra(EXTRA_LOCK_OLD_TIME, openTime);
        intent.putExtra(EXTRA_LOCK_OLD_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_LOCK_OLD_UID, uid);
        sendLocalBroadcast(intent);
    }
    public void handGetKeyCommand(String mac, byte[] command){
       mBLECommunicationKey= command[5];
        Intent intent = new Intent(ACTION_BLE_OPT_GET_KEY_WITH_MAC);
        intent.putExtra("mac",mac);
        intent.putExtra("ckey",mBLECommunicationKey);
        sendLocalBroadcast(intent);

    }

    public void handOpenCommand(byte[] command){

        // 回复 0x21 指令
        sendResOpenCommand();

        int status= command[5];
        long timestamp = ((command[6]&0xFF)<<24) | ((command[7]&0xFF)<<16) |((command[8]&0xFF)<<8) | (command[9] &0xFF);

        Intent intent = new Intent(ACTION_BIKE_LOCK_OPEN);
        intent.putExtra("status",status);
        intent.putExtra("timestamp",timestamp);
        sendLocalBroadcast(intent);



    }

    public void handLockCloseCommand(byte[] command){
        int status = command[5];
        long openTimestamp = ((command[6]&0xFF)<<24) | ((command[7]&0xFF)<<16) |((command[8]&0xFF)<<8) | (command[9] &0xFF);
        long openTime = ((command[10]&0xFF)<<24) | ((command[11]&0xFF)<<16) |((command[12]&0xFF)<<8) | (command[13] &0xFF);

        Intent intent = new Intent(ACTION_BIKE_LOCK_CLOSE);
        intent.putExtra("status",status);
        intent.putExtra("openTimestamp",openTimestamp);
        intent.putExtra("openTime",openTime);

        sendLocalBroadcast(intent);

        // 关锁回应
        byte[] closeResp = BikeLockCommand.getCRCLockResCommand(mBLECommunicationKey);
        writeToDevice(closeResp);


    }

    public byte[] sendGetKeyCommand( String deviceKey){
        byte[] crcOrder= BikeLockCommand.getCRCKeyCommand( deviceKey);
        Log.i(TAG, "sendGetKeyCommand: 发送的指令"+ PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);

    }

    public byte[] sendOpenCommand(int uid,long timestamp){
        byte[] crcOrder= BikeLockCommand.getCRCOpenCommand(uid,mBLECommunicationKey,timestamp);
        return writeToDevice(crcOrder);
    }

    public  byte[] sendShutDown(){
        byte[] command = BikeLockCommand.getCRCShutDown(mBLECommunicationKey);
        return writeToDevice(command);
    }

    public byte[] sendGetOldDataCommand( ){
        byte[] crcOrder= BikeLockCommand.getCRCOldDataCommand(mBLECommunicationKey);
        return writeToDevice(crcOrder);

    }
    public byte[] sendGetClearOldDataCommand( ){
        byte[] crcOrder= BikeLockCommand.getCRCClearOldDataCommand(mBLECommunicationKey);
        return writeToDevice(crcOrder);

    }

    public byte[] sendResOpenCommand(){
        byte[] crcOrder= BikeLockCommand.getCRCOpenResCommand( mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendGetLockInfoCommand( ){
        byte[] crcOrder= BikeLockCommand.getCRCInfoCommand( mBLECommunicationKey);
        return writeToDevice(crcOrder);

    }


    // 获取固件信息

    /**
     * 获取马蹄锁 固件信息，
     * 锁会返回 固件信息的总包数。
     * @return
     */
    public byte[]  sendGetFirmwareInfo(){
        dataBytes.clear();

        byte[] crcOrder= BikeLockCommand.getCRCFirmwareInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new  LocalBinder();

    public class LocalBinder extends Binder {
        public BikeLockService getService(){
            return BikeLockService.this;
        }
    }
}
