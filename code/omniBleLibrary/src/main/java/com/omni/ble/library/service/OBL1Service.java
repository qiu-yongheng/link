package com.omni.ble.library.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.omni.ble.library.utils.OGBL1Command;
import com.omni.lib.utils.CRCUtil;
import com.omni.lib.utils.PrintUtil;

import java.util.UUID;

/**
 * 单车纯蓝牙锁 操作的service
 * Created by lenovo on 2018/3/26.
 */

public class OBL1Service extends BaseService {
    private static final String TAG="OBL1Service";


    public static final String ACTION_OGBL1_OPEN="com.omni.ble.library.ACTION_OGBL1_OPEN";
    public static final String ACTION_OGBL1_CLOSE="com.omni.ble.library.ACTION_OGBL1_CLOSE";

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7");
    }

    @Override
    public UUID getWriteUUID() {
        return UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");
    }

    @Override
    public UUID getNotifyUUID() {
        return UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");
    }

    @Override
    public void onCharacteristicChangedCallback(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {


        String deviceAddress = gatt.getDevice().getAddress();
        final byte[] values=characteristic.getValue();

        Log.i(TAG, "onCharacteristicChangedCallback: "+ PrintUtil.toHexString(values));

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
        switch (command[7]){
            case 0x11:
                // get key
                handGetKeyCommand(mac,command);
                break;
            case 0x21:
                handOpenCommand(command);
            case 0x22:
                // lock
                handLockCloseCommand(command);
                break;
        }

    }

    public void handGetKeyCommand(String mac,byte[] command){
       mBLECommunicationKey= command[9];
        Intent intent = new Intent(ACTION_BLE_OPT_GET_KEY_WITH_MAC);
        intent.putExtra("mac",mac);
        intent.putExtra("ckey",mBLECommunicationKey);
        sendLocalBroadcast(intent);

    }

    public void handOpenCommand(byte[] command){
        int status= command[9];

        Intent intent = new Intent(ACTION_OGBL1_OPEN);
        intent.putExtra("status",status);

        sendLocalBroadcast(intent);

    }

    public void handLockCloseCommand(byte[] command){
        int status = command[9];

        Intent intent = new Intent(ACTION_OGBL1_CLOSE);
        intent.putExtra("status",status);

        sendLocalBroadcast(intent);

    }

    public byte[] sendGetKeyCommand(int uid,String deviceKey){
        byte[] crcOrder= OGBL1Command.getCRCKeyCommand(uid,deviceKey);
        return writeToDevice(crcOrder);

    }

    public byte[] sendOpenCommand(int uid){
        byte[] crcOrder= OGBL1Command.getCRCOpenCommand(uid,mBLECommunicationKey);
        return writeToDevice(crcOrder);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new  LocalBinder();

    public class LocalBinder extends Binder {
        public OBL1Service getService(){
            return OBL1Service.this;
        }
    }
}
