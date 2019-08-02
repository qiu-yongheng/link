package com.omni.ble.library.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.omni.ble.library.utils.OGBL1Command;
import com.omni.ble.library.utils.OGBL2Command;
import com.omni.lib.utils.CRCUtil;
import com.omni.lib.utils.PrintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 单车纯蓝牙锁(新板) 操作的service
 * Created by lenovo on 2018/4/19.
 */

public class OBL2Service extends BaseService {
    private static final String TAG="OBL2Service";


    public static final String ACTION_OGBL2_OPEN="com.omni.ble.library.ACTION_BIKE_LOCK_OPEN";
    public static final String ACTION_OGBL2_CLOSE="com.omni.ble.library.ACTION_BIKE_LOCK_CLOSE";
    public static final String ACTION_OBL2_INFO="com.omni.ble.library.ACTION_BIKE_LOCK_INFO";
    /**
     * 固件信息，正在获取固件信息的过程
     */
    public static final String ACTION_BLE_BIKE_LOCK_FW_INFO_ING="com.omni.ble.library.ACTION_BLE_BIKE_LOCK_FW_INFO_ING";
    /**
     * 固件信息，固件信息的完整值
     */
    public final static String ACTION_BLE_BIKE_LOCK_FW_INFO ="com.omni.ble.library.ACTION_BLE_BIKE_LOCK_FW_INFO";


    public static  final String EXTRA_FIRMWARE_DATA="firmware_data";
    public static  final String EXTRA_FIRMWARE_NPACK="firmware_npack";

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("6E400001-E6AC-A7E7-B1B3-E699BAE8D000");
    }

    @Override
    public UUID getWriteUUID() {
        return UUID.fromString("6E400002-E6AC-A7E7-B1B3-E699BAE8D000");
    }

    @Override
    public UUID getNotifyUUID() {
        return UUID.fromString("6E400003-E6AC-A7E7-B1B3-E699BAE8D000");
    }

    @Override
    public void onCharacteristicChangedCallback(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {


        String deviceAddress = gatt.getDevice().getAddress();
        final byte[] values=characteristic.getValue();

        Log.i(TAG, "onCharacteristicChangedCallback: "+ PrintUtil.toHexString(values));

        if(isGetInfo && values.length==20){
            // 读取锁信息

            if(com.omni.ble.library.utils.CRCUtil.CheckFirstCRC16(values)){
                byte[] command = new byte[values.length-2];
                System.arraycopy(values,2,command,0,values.length-2);
                onHandFirmwareDataCommand(command);
            }
            return ;
        }



        if(values.length==2 && values[0]==32&& values[1]==0) return ;
        byte[] command = new byte[values.length-2];
        for(int i=0;i<command.length;i++) command[i] = values[i];
        int calcCRC = CRCUtil.calcCRC16(command);
        int valueCRC =((values[values.length-2]&0xFF)<<8) | (values[values.length-1]&0xFF);



        if(calcCRC== valueCRC){
            Log.i(TAG, "onCharacteristicChanged: CRC success");
            byte head = (byte) (values[1]-0x32);
            command[1]=head;
            for(int i=2;i<values.length-2;i++){
                command[i] = (byte) (values[i] ^ head);
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
            case OGBL1Command.ORDER_GET_KEY:
                // get key
                handGetKeyCommand(mac,command);
                break;
            case OGBL1Command.ORDER_UN_LOCK:
                handOpenCommand(command);
                break;
            case OGBL1Command.ORDER_LOCK_CLOSE:
                // lock
                handLockCloseCommand(command);
                break;
            case OGBL1Command.ORDER_INFO:
                handLockInfoCommand(command);
                break;
            case (byte)0xFA:
                handFirmwareData(command);
                break;
            case (byte)0xFF:
                handSetRes(command);
                break;
            case (byte)0xF1:
                // 设备问app 请求升级固件数据(一包一包请求)
                handUpgradeFirmwarePack(command);
                break;
        }

    }

    private void handSetRes(byte[] command){
        int resCmd=command[5] & 0xFF;
        byte[] crcOrder= OGBL2Command.getCRCUpRespCommand(   mBLECommunicationKey,(byte)0xFF,(byte)resCmd);
        writeToDevice(crcOrder);
    }

    private void handUpgradeFirmwarePack(byte[] command) {
        int high=command[5] & 0xFF;
        int low=command[6] & 0xFF;
        int nPack = (high <<8) | low;
        int dType = command[7] & 0xFF;
        callbackTransmissionPack(nPack,dType);
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
//
//
            int len =dataBytes.size();
            byte[] allFirmwareData = new byte[len];
            for(int i=0;i<len;i++) {
                allFirmwareData[i]=dataBytes.get(i);
            }
//


            int calcCRC16 = CRCUtil.calcCRC16(allFirmwareData);
            Log.i(TAG, "onHandFirmwareDataCommand: 计算的CRC16="+ String.format("0x%02X",calcCRC16));

            // 将byte[] 数组转化成String
//            String firmwareData = new String(allFirmwareData);
//            Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);




            if(calcCRC16 == firmwareInfoCRC16){
                len =dataBytes.size();
                for(int i=dataBytes.size()-1;i>=0;i--){
                    byte item = dataBytes.get(i);
                    if(item!=(byte)0x00) {
                        len=i+1;
                        break;
                    }
                }
                allFirmwareData = new byte[len];
                for(int i=0;i<len;i++) {
                    allFirmwareData[i]=dataBytes.get(i);
                }
                String firmwareData = new String(allFirmwareData);
                Intent intent = new Intent(ACTION_BLE_BIKE_LOCK_FW_INFO);
                intent.putExtra(EXTRA_FIRMWARE_DATA,firmwareData);
                sendLocalBroadcast(intent);
                //
//                String firmwareData = new String(allFirmwareData);
                Log.i(TAG, "onHandFirmwareDataCommand: 获取到的固件信息="+firmwareData);
            }else{
                len =dataBytes.size();
                for(int i=dataBytes.size()-1;i>=0;i--){
                    byte item = dataBytes.get(i);
                    if(item!=(byte)0x00) {
                        len=i+1;
                        break;
                    }
                }
                allFirmwareData = new byte[len];
                for(int i=0;i<len;i++) {
                    allFirmwareData[i]=dataBytes.get(i);
                }
                String firmwareData = new String(allFirmwareData);
                Intent intent = new Intent(ACTION_BLE_BIKE_LOCK_FW_INFO);
                intent.putExtra(EXTRA_FIRMWARE_DATA,firmwareData);
                sendLocalBroadcast(intent);
                Log.i(TAG, "onHandFirmwareDataCommand:获取到的CRC 和计算CRC 不一样");
            }
        }else{
            // 将获取固件信息的过程 回调出去。
            Intent intent = new Intent(ACTION_BLE_BIKE_LOCK_FW_INFO_ING);

            // 当前返回的是第几包数据
            intent.putExtra(EXTRA_FIRMWARE_NPACK,nPack);
            sendLocalBroadcast(intent);


            // 非最后一包数据
            //TODO 回调出去
//            sendGetFirmwareInfoDetail(nPack+1,(byte)0x8A);
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
        byte[] crcOrder= OGBL2Command.getCRCFirmwareInfoDetail(mBLECommunicationKey,npack,deviceType );
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
    public static final String EXTRA_LOCK_INCO_TIMESTAMP="info_timestamp";

    public void handLockInfoCommand(byte[] command){
        //0-开锁，1-关锁
        int lockStatus= command[5];
        int power = command[6];
        // 1没有，0有
        int old = command[7];
        long timestamp =  ((command[8]&0xFF)<<24) | ((command[9]&0xFF)<<16) |((command[10]&0xFF)<<8) | (command[11] &0xFF);


        Intent intent = new Intent(ACTION_OBL2_INFO);
        intent.putExtra(EXTRA_LOCK_STATUS,lockStatus);
        intent.putExtra(EXTRA_LOCK_POWER,power);
        intent.putExtra(EXTRA_LOCK_OLD,old);
        intent.putExtra(EXTRA_LOCK_INCO_TIMESTAMP,timestamp);
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

        Intent intent = new Intent(ACTION_OGBL2_OPEN);
        intent.putExtra("status",status);
        intent.putExtra("timestamp",timestamp);

        sendLocalBroadcast(intent);



    }

    public void handLockCloseCommand(byte[] command){
        int status = command[5];
        long openTimestamp = ((command[6]&0xFF)<<24) | ((command[7]&0xFF)<<16) |((command[8]&0xFF)<<8) | (command[9] &0xFF);
        long openTime = ((command[10]&0xFF)<<24) | ((command[11]&0xFF)<<16) |((command[12]&0xFF)<<8) | (command[13] &0xFF);

        Intent intent = new Intent(ACTION_OGBL2_CLOSE);
        intent.putExtra("status",status);
        intent.putExtra("openTimestamp",openTimestamp);
        intent.putExtra("openTime",openTime);

        sendLocalBroadcast(intent);

        // 关锁回应
        byte[] closeResp = OGBL2Command.getCRCLockResCommand(mBLECommunicationKey);
        writeToDevice(closeResp);


    }

    public byte[] sendGetKeyCommand( String deviceKey){
        byte[] crcOrder= OGBL2Command.getCRCKeyCommand( deviceKey);
        Log.i(TAG, "sendGetKeyCommand: 发送的指令"+ PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);

    }

    public byte[] sendOpenCommand(int uid,long timestamp){
        byte[] crcOrder= OGBL2Command.getCRCOpenCommand(uid,mBLECommunicationKey,timestamp);
        return writeToDevice(crcOrder);
    }

    public  byte[] sendShutDown(){
        byte[] command = OGBL2Command.getCRCShutDown(mBLECommunicationKey);
        return writeToDevice(command);
    }

    public byte[] sendResOpenCommand(){
        byte[] crcOrder= OGBL2Command.getCRCOpenResCommand( mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendGetLockInfoCommand( ){
        byte[] crcOrder= OGBL2Command.getCRCInfoCommand( mBLECommunicationKey);
        return writeToDevice(crcOrder);

    }


    /**
     * 发送升级固件数据，按照每包的内容发送
     * @param nPack  当前发送的包数
     * @param data  这个升级固件的数据
     * @return
     */
    public byte[] sendUpgradeFwDetail(int nPack,byte[] data){
        byte[] crcOrder= OGBL2Command.getCRCUpDetailCommand(nPack,data);
        return writeToDevice(crcOrder);
    }




    /**
     * 发送固件升级指令
     * @param nPack 升级文件总包数
     * @param upCRC16 升级文件的总CRC16值
     * @param dType  设备类型(U型锁为D1)
     * @param updateKey 升级KEY
     * @return
     */
    public byte[] sendUpdateFirmwareCommand(int nPack, int upCRC16, byte dType, String updateKey){
        byte[] crcOrder = OGBL2Command.getCRCUpCommand(mBLECommunicationKey,nPack,upCRC16,dType,updateKey);
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
        byte[] crcOrder= OGBL2Command.getCRCFirmwareInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new  LocalBinder();

    public class LocalBinder extends Binder {
        public OBL2Service getService(){
            return OBL2Service.this;
        }
    }
}
