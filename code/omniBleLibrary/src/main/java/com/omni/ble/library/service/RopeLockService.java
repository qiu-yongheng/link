package com.omni.ble.library.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.omni.ble.library.order.BLEOrderManager;
import com.omni.lib.utils.PrintUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 钢缆锁，个人蓝牙锁 服务
 */
public class RopeLockService extends Service {
    private final static String TAG=RopeLockService.class.getSimpleName();

    private UUID uuidService=UUID.fromString("6e400001-e6ac-a7e7-b1b3-e699bae80062");
    private UUID uuidWrite=UUID.fromString("6e400002-e6ac-a7e7-b1b3-e699bae80062");
    private UUID uuidRead=UUID.fromString("6e400003-e6ac-a7e7-b1b3-e699bae80062");

    //sample KEY
    byte[] keyOrg=new byte[]{0x61,0x66,0x6B,0x33,0x74,0x79,0x73,0x77,0x34,0x70,0x73,0x6B,0x32,0x36,0x68,0x6A};
    byte[] inputKey =new byte[] {0x1,0x2,0x3,0x4};


    /**
     * 连接了蓝牙设备，注册了通知的广播
     */
    public final static String ACTION_BLE_WRITE_NOTIFY="com.omni.ble.library.ACTION_BLE_WRITE_NOTIFY";
    /**
     * 扫描到BLE设备
     */
    public final static String ACTION_BLE_SCAN_DEVICE="com.omni.ble.library.ACTION_BLE_SCAN_DEVICE";
    /**
     * 指令时间内，没有扫描到想到扫描的设备的广播通知
     */
    public final static String ACTION_BLE_SCAN_DEVICE_NOT="com.omni.ble.library.ACTION_BLE_SCAN_DEVICE_NOT";

    /** 断开蓝牙设备的广播  */
    public static final String ACTION_BLE_DISCONNECTED="com.omni.ble.library.ACTION_BLE_DISCONNECTED";


    /** 钢缆锁本地KEY 保存状态广播  */
    public static final String ACTION_LOCK_MAP_KEY="com.omni.ble.library.ACTION_LOCK_MAP_KEY";
    public static final String ACTION_LOCK_NOTIFY_DATA="com.omni.ble.library.ACTION_LOCK_NOTIFY_DATA";


    public static  final String EXTRA_MAP_KEY_RAND_NUM="randNumber";
    public static  final String EXTRA_MAP_KEY_TYPE="map_key_type";
    public static  final String EXTRA_LOCK_NOTIFY_DATA ="notify_data";

    public static final int MAP_KEY_TYPE_LOCAL_KEY_EXIST=1;
    public static final int MAP_KEY_TYPE_LOCAL_KEY_NOT_EXIST=2;

    /**
     * 蓝牙指令管理类
     */
    protected BLEOrderManager orderManager;

    // 蓝牙操作对象
    protected BluetoothManager mBLEManager;
    protected BluetoothAdapter mBLEAdapter;
    protected BluetoothGatt mBLEGatt=null;

    /**  用handler 控制的下次判断 蓝牙连接状态的 间隔 */
    protected final static int HANDLER_STATE_CONNECT_DELAYED_TIME=2000;
    protected final static int HANDLER_DISCOVER_SERVICES=30;
    /** 当前连接蓝牙状态 */
    public  int mConnectionState=0; //蓝牙连接的状态

    protected final static int HANDLER_STATE_CONNECT=3;
    /** 断开连接*/
    public static final int STATE_DISCONNECTED =0;
    /** 正在连接 */
    public static final int STATE_CONNECTING =1;
    /** 找到蓝牙连接*/
    public static final int STATE_CONNECTED =2;
    /** 找到服务*/
    public static final int STATE_FIND_SERVICE =3;
    public static final int STATE_BIND_CONNECTED =4;


    /**
     * 用于 写数据
     */
    protected BluetoothGattCharacteristic mBLEGCWrite;
    /**
     * 用于 获取锁发送过来的通知
     */
    protected BluetoothGattCharacteristic mBLEGCNotify;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case HANDLER_DISCOVER_SERVICES:
                    discoverServices(mBLEGatt);
                    break;

                case HANDLER_STATE_CONNECT:

                    break;
            }
        }
    };


    public boolean connect(final String address,byte[] inputKey,byte[] keyOrg){
        this.inputKey = inputKey;
        this.keyOrg= keyOrg;
        return connect(address);
    }


    /**
     *
     * @param address 蓝牙设备的MAC地址
     * @return 连接成功返回true，否则返回false
     * @throws IllegalArgumentException
     */
    public boolean connect(final String address) throws  IllegalArgumentException{
        if(mBLEAdapter==null  ){
            Log.w(TAG, "connect: BluetoothAdapter not initialized  ");
            return false;
        }
        if( address==null){
            Log.w(TAG, "connect:   unspecified address.");
            return false;
        }
        //第一次连接设备
        final BluetoothDevice device = mBLEAdapter.getRemoteDevice(address.toUpperCase());


        if(device==null){
            Log.w(TAG, "connect: Device not found.  Unable to connect.");
            return false;
        }

        //We want to directly connect to the device, so we are setting the autoConnect
        //parameter to false.
        mBLEGatt = device.connectGatt(this,false,mGattCallback);
        //初始化指令读写管理器
        orderManager = new BLEOrderManager(mBLEGatt);

        Log.i(TAG, "748 connect: Trying to create a new connection");
        mConnectionState = STATE_CONNECTING;

        //5s后检测是否连接并配对上设备
        Message message = new Message();
        message.what =HANDLER_STATE_CONNECT;
        message.arg1=STATE_CONNECTING;
        handler.sendMessageDelayed(message, HANDLER_STATE_CONNECT_DELAYED_TIME);
        return true;

    }

    public void disconnect(){
        if(mBLEAdapter==null || mBLEGatt==null){
            Log.w(TAG, "disconnect: BluetoothAdapter not initialized");
            return;
        }
        mConnectionState = STATE_DISCONNECTED;
        mBLEGatt.disconnect();
    }

    protected void discoverServices(BluetoothGatt gatt){
        if(gatt!=null) gatt.discoverServices();
    }


    /**
     * 用于注册蓝牙通知
     * @param characteristic
     */
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic ){
        if(mBLEAdapter==null||mBLEGatt==null){
            Log.w(TAG, "setCharacteristicNotification: BluetoothAdapter not initialized");
            return ;
        }
        BluetoothGattDescriptor descriptor=characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBLEGatt.writeDescriptor(descriptor);
    }


    private  int  checkStatus = 0; // 0 no check ,1 check ing ,2 check ok

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {

            if(newState== BluetoothProfile.STATE_CONNECTED){
                Log.i(TAG, "onConnectionStateChange: thread="+Thread.currentThread().getName());
                gatt.discoverServices();
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                checkStatus=0;
                mConnectionState=STATE_DISCONNECTED;
                sendLocalBroadcast(ACTION_BLE_DISCONNECTED);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> list= gatt.getServices();
            BluetoothGattService bleGattService=null;
            for(BluetoothGattService  BLEGS: list){
                Log.i(TAG, "onServicesDiscovered: service="+BLEGS.getUuid().toString());
                if(BLEGS.getUuid().equals(uuidService)){
                    bleGattService= gatt.getService(uuidService);
                    mBLEGatt = gatt;
                    //控制功能
                    mBLEGCWrite = bleGattService.getCharacteristic(uuidWrite);
                    //参数配置
                    mBLEGCNotify = bleGattService.getCharacteristic(uuidRead);
                    setCharacteristicNotification(mBLEGCNotify);
                }
            }
        }

        int calcSum=0;
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            Log.i(TAG, "onCharacteristicChanged: "+ PrintUtil.toHexString(data));

//            Message msg = new Message();
//            msg.what=3;
//            Bundle bundle = new Bundle();
//            bundle.putByteArray("data",data);
//            msg.setData(bundle);
//            hand.sendMessage(msg);
            Intent intent=new Intent(ACTION_LOCK_NOTIFY_DATA);
            intent.putExtra(EXTRA_LOCK_NOTIFY_DATA,data);
            sendLocalBroadcast(intent);


            int head1 = data[0]&0xFF;
            int head2 = data[1]&0xFF;
            int command = data[3]&0xFF;
//            if(head1==0xAB && head2==0xDE && data[2]==0x04 && command==0x0FB){
//                // 升级指令返回FB
//                int status = data[4]&0xFF;
//                Log.i(TAG, "onCharacteristicChanged:升级指令状态="+status);
//                return ;
//            }
//
//            if(head1==0xAB && head2==0xDE && data[2]==0x04 && command==0x0FC){
//                // 升级指令返回FB
//                int pack = ((data[4]&0xFF)<<8) | (data[5]&0xFF);
//                onBLECarportTransmission(pack);
////                Log.i(TAG, "onCharacteristicChanged: 升级文件包数="+pack);
//                return ;
//            }

            int status = data[4];
            int randNum = data[11];

            if(status ==0x08){
                // 0x08=1000(B)
//                hasMapKey=true;
                //todo 发送有本地KEY广播
                intent=new Intent(ACTION_LOCK_MAP_KEY);
                intent.putExtra(EXTRA_MAP_KEY_RAND_NUM,randNum);
                intent.putExtra(EXTRA_MAP_KEY_TYPE,MAP_KEY_TYPE_LOCAL_KEY_EXIST);
                sendLocalBroadcast(intent);

                writeGetKeyWithMapKey( data[11]);
                // auto check
                // have mapKey
                //  use mapkey encode  0x01 command
                // if use input key is 0x1 0x2 0x3 0x04;
//                if(checkStatus==0) {
//                    byte[] keyMap = getKeyMap(inputKey, keyOrg);
//                    byte[] command01 = getCommand01(randNum,inputKey,keyMap);
//                    writeToDevice(command01);
//                    checkStatus = 1;
//                }


            }else if((status & 0x08)==0){
//                hasMapKey=false;
                //todo 发送 没有本地KEY广播
                intent=new Intent(ACTION_LOCK_MAP_KEY);
                intent.putExtra(EXTRA_MAP_KEY_RAND_NUM,randNum);
                intent.putExtra(EXTRA_MAP_KEY_TYPE,MAP_KEY_TYPE_LOCAL_KEY_NOT_EXIST);
                sendLocalBroadcast(intent);

                writeGetKeyWithKeyorg(data[11]);
                // auto check
                // no mapKey
                // use keyOrg encode  0x01 command
                // if use input key is [0x01,0x02,0x03,0x04]
                // when check ok , lock save the input key for next check
//                if(checkStatus==0) {
//                    byte[] command01 = getCommand01WithKeyOrg(randNum,inputKey,keyOrg);
//                    writeToDevice(command01);
//                    checkStatus = 1;
//                }

            }else if(status==0x0A ){
                //0x0A=1010(B)
                //check ok
                // key right
                checkStatus =2;
//                hand.sendEmptyMessage(2);

            }else if(status ==0x09){
                //0x9=1001(B)
                // check ok
                checkStatus =2;
//                hand.sendEmptyMessage(2);
            }

            if(checkStatus==2){
                calcSum++;
                if(calcSum>=5) {
                    // send 0x07   for   keep  connect (each 5 s)
                    writeHeart2Device(inputKey, keyOrg);
//                    byte[] keyMap = getKeyMap(inputKey, keyOrg);
//                    writeToDevice(getCommand07(keyMap));
                    calcSum=0;
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,final BluetoothGattCharacteristic characteristic, int status) {
            // 写成功
//            Log.i(TAG, "onCharacteristicWrite:  write ok");
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //写完移动数据的通知
            gatt.setCharacteristicNotification(mBLEGCNotify,true);

            // 已经注册了通过。
            sendLocalBroadcast(ACTION_BLE_WRITE_NOTIFY);


        }
    };

    public static final String EXTRA_SCAN_DEVICE="com.omni.ble.library.EXTRA_SCAN_DEVICE";
    public static final String EXTRA_SCAN_RECORD="com.omni.ble.library.EXTRA_SCAN_RECORD";
    public static final String EXTRA_DEVICE_MAC="com.omni.ble.library.EXTRA_DEVICE_MAC";
    private boolean isScanDevice = false;
    private boolean  mScanning=false;
    private String filterMac="";
    /**
     * 开始扫描设备
     * @param deviceAddress 指令扫描设备的MAC地址
     * @param scanPeriod 扫描的时长，单位 毫秒数
     */
    public void startScanBLEDevice(final String deviceAddress, int scanPeriod){
        filterMac=deviceAddress;
        isScanDevice=false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning=false;
                //TODO 2018-12-03  发现使用新的扫描方式，有时无法扫描到设备-故使用旧方法

                mBLEAdapter.stopLeScan(mLescanCallback);
                if(!isScanDevice){
                    // 指定时间内没有扫描的想扫的设备，发送广播通知

                    Intent intent = new Intent(ACTION_BLE_SCAN_DEVICE_NOT);
                    intent.putExtra(EXTRA_DEVICE_MAC,deviceAddress);
                    sendLocalBroadcast(intent);
                }

            }
        },scanPeriod);
        mScanning=true;
        //TODO 2018-12-03  发现使用新的扫描方式，有时无法扫描到设备-故使用旧方法

        mBLEAdapter.startLeScan(mLescanCallback);

    }

    /**
     * 停止蓝牙扫描
     */
    public void stopScanBLEDevice(){
        if(mScanning){
            mScanning=false;
            mBLEAdapter.stopLeScan(mLescanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mLescanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            Log.i(TAG, "onScanResult:旧 thread name="+Thread.currentThread().getName());
            // 回调在异步线程中
            String macAddress=device.getAddress();
            if(mBLEAdapter!=null && (  filterMac.equals(macAddress) )){
                isScanDevice = true;
                Log.i(TAG, "onScanResult: mac="+device.getAddress());
                Intent intent = new Intent(ACTION_BLE_SCAN_DEVICE);
                intent.putExtra(EXTRA_SCAN_DEVICE,device);
                intent.putExtra(EXTRA_SCAN_RECORD,scanRecord);
                sendLocalBroadcast(intent);

                stopScanBLEDevice();
            }
        }
    };

    public boolean isConnectedDevice(String mac){
        List<BluetoothDevice> deviceList = mBLEManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
        Log.i(TAG, "isConnectedDevice: deviceList = "+deviceList.size());
        for(BluetoothDevice bluetoothDevice: deviceList){
            Method isConnectedMethod = null;
            try {
                isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected",(Class[])null);
                isConnectedMethod.setAccessible(true);
                boolean isConnected= (boolean) isConnectedMethod.invoke(bluetoothDevice,(Object[]) null);
                Log.i(TAG, "isConnectedDevice: 放射 返回获取连接状态");
                Log.i(TAG, "isConnectedDevice: isConnected="+isConnected);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            if(mac.equals(bluetoothDevice.getAddress())){
                return  true;
            }
        }
        Log.i(TAG, "isConnectedDevice: isConnected =false");
        return  false;
    }

    private static byte[] getKeyMap(byte[] inputKey,byte[] keyOrg){
        byte[] keyMap = new byte[256];
        int index = 0;
        for(int k=0;k<4;k++){
            for (int i = 0; i < inputKey.length; i++) {
                for (int j = 0; j < keyOrg.length; j++) {
                    keyMap[index] = (byte) ((inputKey[i] + 0x30+k ) ^ keyOrg[j]);
                    index++;
                }
            }
        }
        return keyMap;
    }

    private byte[] getCommand07(byte[] encodeArray){
        byte[] command01=new byte[]{(byte)0xAB,(byte)0xDE,0x04,0x20,0x07,0x0};
        byte randNumber = (byte)(new Random().nextInt() &0xFF);
        command01[3]=randNumber;
        return encryption(command01,encodeArray);
    }



    private static byte[] encryption(byte[] plain ,byte[] keyMap){
        int index = (plain[3] &0xFF);
        byte[] xorplain=new byte[plain.length];
        for(int i=0;i<plain.length;i++){
            if(i>=5){
                if(index>= keyMap.length) index=0;
                xorplain[i]=(byte)((plain[i]^ keyMap[index++])&0xFF);
            }else{
                xorplain[i]=plain[i];
            }
        }
        return checkSum(xorplain);
    }

    private static byte[] checkSum(byte[] data){
        int sum=0;
        byte[] result = new byte[data.length+1];
        for(int i=0;i<data.length;i++) {
            result[i]=data[i];
            if(i>=3){
                // 从随机数开锁校验和
                sum+=data[i];
            }
        }
        result[data.length]=(byte)(sum&0xFF);
        return result;
    }

    private byte[] writeHeart2Device(byte[] inputKey,byte[] keyOrg){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        return writeToDevice(getCommand07(keyMap));
    }

    /**
     * send data to device
     * @param randNum
     * @return
     */
    private byte[] writeGetKeyWithMapKey(byte randNum){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command01 = getCommand01(randNum,inputKey,keyMap);
        return writeToDevice(command01);
    }

    private byte[] writeGetKeyWithKeyorg(byte randNum){

        byte[] command01 = getCommand01WithKeyOrg(randNum,inputKey,keyOrg);
        return writeToDevice(command01);
    }

    public byte[] writeUnlock(){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command01 = getUnlockCommand(keyMap);
        return writeToDevice(command01);
    }

    public byte[] writeUnlock(long timestamp){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command = getUnlockByTimestampCommand(timestamp,keyMap);
        return writeToDevice(command);
    }

    public byte[] writeShutdown( ){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command = getShutDown(keyMap);
        return writeToDevice(command);
    }

    public byte[] writeDeviceConfig(byte openMode ){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command = getControlCommand(openMode ,keyMap);
        return writeToDevice(command);
    }


    public static final byte MODE_OPEN_APP=0x20;
    public static final byte MODE_OPEN_APP_MANUAL=0x10;

    private byte[] getControlCommand(byte controlCode,byte[] encodeArray){
        // 0x80 =1000 0000(B)
        byte[] command=new byte[]{(byte)0xAB,(byte)0xDE,0x04,0x20,0x05,(byte)controlCode};
        byte randNumber = (byte)(new Random().nextInt() &0xFF);
        command[3]=randNumber;
        return encryption(command,encodeArray);
    }

    public byte[] writeClearKey( ){
        byte[] keyMap = getKeyMap(inputKey, keyOrg);
        byte[] command = getClearKeyCommand(inputKey,keyMap);
        return writeToDevice(command);
    }
    private byte[] getClearKeyCommand(byte[] inputKey, byte[] encodeArray){
        byte[] command01=new byte[]{(byte)0xAB,(byte)0xDE,0x07,0x20,0x02,0x01,2,3,4};
        byte randNumber = (byte)(new Random().nextInt() &0xFF);
        command01[3]=randNumber;

        command01[5]=inputKey[0];
        command01[6]=inputKey[1];
        command01[7]=inputKey[2];
        command01[8]=inputKey[3];

        return encryption(command01,encodeArray);
    }

    private byte[] getShutDown(byte[] encodeArray){
        //
        byte[] command01=new byte[]{(byte)0xAB,(byte)0xDE,0x04,0x20,0x06,0x30};
        byte randNumber = (byte)(new Random().nextInt() &0xFF);
        command01[3]=randNumber;
        return encryption(command01,encodeArray);
    }


    private byte[] getUnlockByTimestampCommand(long timestamp,byte[] encodeArray){
        byte t1 = (byte)((timestamp>>24) &0xFF);
        byte t2 = (byte)((timestamp >>16)&0xFF);
        byte t3 = (byte)((timestamp>>8) &0xFF);
        byte t4 = (byte)(timestamp &0xFF);

        byte[] command=new byte[]{(byte)0xAB,(byte)0xDE,0x07,0x20,0x09,t1,t2,t3,t4};
        return encryption(command,encodeArray);
    }

    private byte[] getUnlockCommand(byte[] encodeArray){
        // 0x80 =1000 0000(B)
        byte[] command05=new byte[]{(byte)0xAB,(byte)0xDE,0x04,0x20,0x05,(byte)0x80};
        byte randNumber = (byte)(new Random().nextInt() &0xFF);
        command05[3]=randNumber;
        return encryption(command05,encodeArray);
    }



    private byte[] getCommand01WithKeyOrg(byte randNum,byte[] inputKey,byte[] keyOrg){
        // if use input key is 0x1,0x2,0x3,0x4
        byte[] command01=new byte[]{(byte)0xAB,(byte)0xDE,0x07,0x20,0x01,0x01,0x02,0x03,0x04};

        command01[3]=randNum;

        command01[5]=inputKey[0];
        command01[6]=inputKey[1];
        command01[7]=inputKey[2];
        command01[8]=inputKey[3];

        return encryptionWithOrgkey(command01,keyOrg);
    }

    private static byte[] encryptionWithOrgkey(byte[] plain ,byte[] orgkey){
        int index = 0;
        byte[] xorplain=new byte[plain.length];
        for(int i=0;i<plain.length;i++){
            if(i>=5){
                xorplain[i]=(byte)((plain[i]^ orgkey[index++])&0xFF);
            }else{
                xorplain[i]= plain[i];
            }
        }
        return checkSum(xorplain);
    }

    private byte[] getCommand01(byte randNum,byte[] inputKey,byte[] encodeArray){
        // if use input key is 0x1,0x2,0x3,0x4

        //  {0x1,0x1D,0x32,0x7C}
        byte[] command01=new byte[]{(byte)0xAB,(byte)0xDE,0x07,0x20,0x01,0x01,0x1D,0x32,0x7C};
        byte randNumber = randNum;
        command01[3]=randNumber;

        command01[5]=inputKey[0];
        command01[6]=inputKey[1];
        command01[7]=inputKey[2];
        command01[8]=inputKey[3];
        return encryption(command01,encodeArray);
    }


    protected   byte[] writeToDevice(byte[] data){
        if(mBLEAdapter==null ){
            Log.w(TAG, "writeToDevice: BluetoothAdapter not initialized");
            return null;
        }
        if( mBLEGatt==null){
            Log.w(TAG, "writeToDevice: mBLEGatt not initialized");
            return null;
        }

//        Log.i(TAG, "writeToDevice: encode   command="+toHexString(data));


        mBLEGCWrite.setValue(data);
        mBLEGCWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBLEGatt.writeCharacteristic(mBLEGCWrite);


        return data;
    }

    /**
     * 发送 本地广播
     * @param action 字符串格式的广播action
     */
    protected void sendLocalBroadcast(final String action ){
        final Intent intent = new Intent(action);
        sendLocalBroadcast(intent);
    }
    /**
     * 发送 本地广播
     * @param intent intent 形式的广播action
     */
    protected void sendLocalBroadcast(final Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(init()) Log.i(TAG, "onCreate: 初始成功");
    }
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new RopeLockService.LocalBinder();
    public class LocalBinder extends Binder {
        public RopeLockService getService(){
            return RopeLockService.this;
        }
    }
}
