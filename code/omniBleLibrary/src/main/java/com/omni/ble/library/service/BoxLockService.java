package com.omni.ble.library.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.ble.library.order.BLEOrderManager;
import com.omni.ble.library.utils.BoxLockCommand;
import com.omni.lib.utils.PrintUtil;

import java.util.List;
import java.util.UUID;

/**
 * 柜锁
 * Created by cxiaox on 2018/3/9.
 */

public class BoxLockService extends BaseService {
    private static final String TAG="BoxLockService";

    /**
     * 柜锁 开锁广播
     */
    public final static String ACTION_BLE_BOX_LOCK_OPEN ="com.omni.ble.library.ACTION_BLE_BOX_LOCK_OPEN";
    public final static String ACTION_BLE_BOX_LOCK_INFO ="com.omni.ble.library.ACTION_BLE_BOX_LOCK_INFO";
    public final static String ACTION_BLE_BOX_LOCK_LOW_STATUS ="com.omni.ble.library.ACTION_BLE_BOX_LOCK_LOW_STATUS";


    @Override
    public void onServicesDiscoveredCallback(BluetoothGatt gatt, int status) {
        super.onServicesDiscoveredCallback(gatt, status);

        List<BluetoothGattService> list= gatt.getServices();
        BluetoothGattService bleGattService=null;
        for(BluetoothGattService  BLEGS: list){
            if(BLEGS.getUuid().equals(getServiceUUID())){
                mConnectionState = STATE_FIND_SERVICE;
                bleGattService= gatt.getService(getServiceUUID());

                //控制功能
                mBLEGCWrite = bleGattService.getCharacteristic(getWriteUUID());
                //参数配置
                mBLEGCNotify = bleGattService.getCharacteristic(getNotifyUUID());
            }else if(BLEGS.getUuid().equals(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"))){
                mConnectionState = STATE_FIND_SERVICE;
                bleGattService= gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));

                //控制功能
                mBLEGCWrite = bleGattService.getCharacteristic(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"));
                //参数配置
                mBLEGCNotify = bleGattService.getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
            }
        }


        if(bleGattService!=null) {
            Log.i(TAG, "onServicesDiscovered: 发现蓝牙服务");

            mConnectionState = STATE_FIND_SERVICE;


            sendFindServiceHandler();

            orderManager=new BLEOrderManager(gatt);

            setCharacteristicNotification(mBLEGCNotify);
        }else{
            Log.i(TAG, "onServicesDiscovered: 没有发现服务");
        }
    }

    @Override
    public void onHandNotifyCommand(String mac, byte[] command) {
        super.onHandNotifyCommand(mac, command);
        switch (command[5]){
            case CommandType.BOX_LOCK_OPEN:
                Log.i(TAG, "onHandNotifyCommand: 柜锁接收到开锁指令的回应");
                // 发送开柜锁的 广播
                int status = command[6];
                long timestamp= ((command[7]&0xFF)<<24 )|((command[8]&0xFF)<<16)|((command[9]&0xFF)<<8)| (command[10]&0xFF);
                Intent intent = new Intent(ACTION_BLE_BOX_LOCK_OPEN);
                intent.putExtra("status", status);
                intent.putExtra(EXTRA_TIMESTAMP, timestamp);
                sendLocalBroadcast(ACTION_BLE_BOX_LOCK_OPEN);

                break;
            case CommandType.BOX_LOCK_DEVICE_INFO:
                Log.i(TAG, "onHandNotifyCommand: 获取柜锁 信息");
                handBoxLockInfo(command);
                break;
            case CommandType.BOX_LOCK_LOW_OPEN:
                handBoxLockLowOpen(command);
                break;
        }
        
    }

    public static final String EXTRA_LOW_OPEN_STATUS="low_open_status";

    private void handBoxLockInfo( byte[] command){

        Log.i(TAG, "handBoxLockInfo: lock info="+ PrintUtil.toHexString(command));
        int voltage = ((command[6]&0xFF)<<8) | (command[7]&0xFF);
        // 低电量开锁模式开关
        int lowOpenStatus = command[9]&0xFF;
        String version = String.format("%s.%s.%s",command[10],command[11],command[12]);

        Intent intent = new Intent(ACTION_BLE_BOX_LOCK_INFO);
        intent.putExtra(EXTRA_VOLTAGE,voltage);
        intent.putExtra(EXTRA_VERSION,version);
        intent.putExtra(EXTRA_LOW_OPEN_STATUS,lowOpenStatus);
        sendLocalBroadcast(intent);
    }


    public static  final String EXTRA_LOW_POWER_STATUS="power_status";
    private void handBoxLockLowOpen( byte[] command){

        int lowStatus = command[6]&0xFF;

        Intent intent = new Intent(ACTION_BLE_BOX_LOCK_LOW_STATUS);
        intent.putExtra(EXTRA_LOW_POWER_STATUS,lowStatus);

        sendLocalBroadcast(intent);
    }


    public byte[] sendGetKey(String deviceKey){
        Log.i(TAG, "sendGetKey:发送获取操作KEY指令");
        byte[] crcOrder= BoxLockCommand.getCRCCommunicationKey(deviceKey);

        return writeToDevice(crcOrder);
    }

    public byte[] sendBoxLockOpen(int uid,long timestamp){
        Log.i(TAG, "sendMifiLockOpen:发送 开柜锁 指令");
        byte[] crcOrder=BoxLockCommand.getCRCOpen(mBLECommunicationKey,uid,timestamp);
        return writeToDevice(crcOrder);
    }


    public byte[] sendBoxLockLowOpen(byte status){
        Log.i(TAG, "sendMifiLockOpen:发送 开柜锁 指令");
        byte[] crcOrder=BoxLockCommand.getCRCLowOpen(mBLECommunicationKey,status);
        return writeToDevice(crcOrder);
    }

    public byte[] sendBoxLockInfo(){
        byte[] crcOrder= BoxLockCommand.getCRCDeviceInfo(mBLECommunicationKey);
        return writeToDevice(crcOrder);
    }



    @Override
    public UUID getServiceUUID() {
        return getUUID("6e400001-e6ac-a7e7-b1b3-e699bae80060");
    }

    @Override
    public UUID getWriteUUID() {
        return getUUID("6e400002-e6ac-a7e7-b1b3-e699bae80060");
    }

    @Override
    public UUID getNotifyUUID() {
        return getUUID("6e400003-e6ac-a7e7-b1b3-e699bae80060");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new  LocalBinder();

    public class LocalBinder extends Binder {
        public BoxLockService getService(){
            return BoxLockService.this;
        }
    }

}
