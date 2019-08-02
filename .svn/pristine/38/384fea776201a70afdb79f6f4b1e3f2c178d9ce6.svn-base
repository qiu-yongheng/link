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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.ble.library.model.GattAttributes;
import com.omni.ble.library.order.BLEOrderManager;
import com.omni.ble.library.utils.CommandUtil;
import com.omni.lib.utils.CRCUtil;
import com.omni.lib.utils.PrintUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙公共服务的 父类 <br/>
 * 适合解析如下格式蓝牙通信:
 * 数据头(2字节)，数据长度(1字节)，随机数(1)，通信密钥(1)，命令字(1)，数据，CRC8(1)。<br/>
 * Created by cxiaox on 2018/3/9.
 */

public abstract class BaseService extends Service {
    private final static String TAG = BaseService.class.getSimpleName();

    /**
     * 用handler 控制的下次判断 蓝牙连接状态的 间隔
     */
    protected final static int HANDLER_STATE_CONNECT_DELAYED_TIME = 2000;

    public final static String ACTION_BLE_TRANSMISSION_PACK = "com.omni.ble.library.carport.ACTION_BLE_TRANSMISSION_PACK";
    public static final String EXTRA_TRANSMISSION_PACK_PACK = "transmission_pack";
    public static final String EXTRA_TRANSMISSION_PACK_TYPE = "transmission_type";


    /**
     * 蓝牙指令管理类
     */
    protected BLEOrderManager orderManager;

    // 蓝牙操作对象
    protected BluetoothManager mBLEManager;
    protected BluetoothAdapter mBLEAdapter;
    protected BluetoothGatt mBLEGatt = null;

    // 5.0 以后扫描使用
    private BluetoothLeScanner bluetoothLeScanner;
    private MyScanCb myscb;

    /**
     * 用来标识是否自动连接
     */
    protected boolean autoConnect = false;

    /**
     * 当前连接蓝牙状态
     */
    public int mConnectionState = 0; //蓝牙连接的状态

    /**
     * 断开连接
     */
    public static final int STATE_DISCONNECTED = 0;
    /**
     * 正在连接
     */
    public static final int STATE_CONNECTING = 1;
    /**
     * 找到蓝牙连接
     */
    public static final int STATE_CONNECTED = 2;
    /**
     * 找到服务
     */
    public static final int STATE_FIND_SERVICE = 3;
    public static final int STATE_BIND_CONNECTED = 4;


    public static final String EXTRA_TIMESTAMP = "timestamp";
    public static final String EXTRA_VOLTAGE = "voltage";
    public static final String EXTRA_VERSION = "version";


    /**
     * 连接蓝牙设备，断开时自动重连。
     *
     * @param address 要连接的蓝牙设置地址
     * @return 连接成功返回true，否则返回false
     */
    public boolean autoConnect(final String address) throws IllegalArgumentException {
        Log.i(TAG, "autoConnect: 自动连接 蓝牙");
        autoConnect = true;

        return connect(address);
    }

    /**
     * @param address 蓝牙设备的MAC地址
     * @return 连接成功返回true，否则返回false
     * @throws IllegalArgumentException
     */
    public boolean connect(final String address) throws IllegalArgumentException {
        if (mBLEAdapter == null) {
            Log.w(TAG, "connect: BluetoothAdapter not initialized  ");
            return false;
        }
        if (address == null) {
            Log.w(TAG, "connect:   unspecified address.");
            return false;
        }
        //第一次连接设备
        final BluetoothDevice device = mBLEAdapter.getRemoteDevice(address.toUpperCase());


        if (device == null) {
            Log.w(TAG, "connect: Device not found.  Unable to connect.");
            return false;
        }

        //We want to directly connect to the device, so we are setting the autoConnect
        //parameter to false.
        mBLEGatt = device.connectGatt(this, false, mGattCallback);
        //初始化指令读写管理器
        orderManager = new BLEOrderManager(mBLEGatt);

        Log.i(TAG, "748 connect: Trying to create a new connection");
        mConnectionState = STATE_CONNECTING;

        //5s后检测是否连接并配对上设备
        Message message = new Message();
        message.what = HANDLER_STATE_CONNECT;
        message.arg1 = STATE_CONNECTING;
        handler.sendMessageDelayed(message, HANDLER_STATE_CONNECT_DELAYED_TIME);
        return true;

    }


    private boolean mScanning = false;
    private String filterMac = "";

    /**
     * 开始扫描设备
     *
     * @param deviceAddress 指令扫描设备的MAC地址
     * @param scanPeriod    扫描的时长，单位 毫秒数
     */
    public void startScanBLEDevice(final String deviceAddress, int scanPeriod) {
        filterMac = deviceAddress;
        isScanDevice = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                //TODO 2018-12-03  发现使用新的扫描方式，有时无法扫描到设备-故使用旧方法
//                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//                    if (bluetoothLeScanner != null) {
//                        bluetoothLeScanner.stopScan(myscb);
//                    }else{
//
//                        mBLEAdapter.stopLeScan(mLescanCallback);
//                    }
//                }else{
//                    mBLEAdapter.stopLeScan(mLescanCallback);
//                }
                mBLEAdapter.stopLeScan(mLescanCallback);
                if (!isScanDevice) {
                    // 指定时间内没有扫描的想扫的设备，发送广播通知

                    Intent intent = new Intent(ACTION_BLE_SCAN_DEVICE_NOT);
                    intent.putExtra(EXTRA_DEVICE_MAC, deviceAddress);
                    sendLocalBroadcast(intent);
                }

            }
        }, scanPeriod);
        mScanning = true;
        //TODO 2018-12-03  发现使用新的扫描方式，有时无法扫描到设备-故使用旧方法
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//
//            List<ScanFilter> bleScanFilters = new ArrayList<>();
//            bleScanFilters.add(new ScanFilter.Builder().setDeviceAddress(deviceAddress).build());
//
//            if (bluetoothLeScanner != null) {
//                bluetoothLeScanner.startScan(bleScanFilters, new ScanSettings.Builder().build(), myscb);
//            }else{
//                mBLEAdapter.startLeScan(mLescanCallback);
//            }
//        }else{
//            mBLEAdapter.startLeScan(mLescanCallback);
//        }
        mBLEAdapter.startLeScan(mLescanCallback);

    }

    /**
     * 停止扫描
     */
    public void stopScanBLEDevice() {
        if (mScanning) {
            mScanning = false;
            //TODO 2018-12-03  发现使用新的扫描方式，有时无法扫描到设备-故使用旧方法
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//                if (bluetoothLeScanner != null) {
//                    bluetoothLeScanner.stopScan(myscb);
//                }else{
//                    mBLEAdapter.stopLeScan(mLescanCallback);
//                }
//            }else{
//                mBLEAdapter.stopLeScan(mLescanCallback);
//            }
            mBLEAdapter.stopLeScan(mLescanCallback);
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (init()) Log.i(TAG, "onCreate: 初始成功");
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return true if the initialization is successful.
     */
    public boolean init() {
        if (mBLEManager == null) {
            mBLEManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBLEManager == null) {
                Log.e(TAG, "init: Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBLEAdapter = mBLEManager.getAdapter();
        if (mBLEAdapter == null) {
            Log.e(TAG, "init: Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.i(TAG, "init: mBleAdapter=" + mBLEAdapter.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "initView:  android 版本>=5.0");
            bluetoothLeScanner = mBLEAdapter.getBluetoothLeScanner();
            myscb = new MyScanCb();
        }
        return true;
    }


    /**
     * 获取到所有连接了的设备
     *
     * @return 连接了的设备列表
     */
    public List<BluetoothDevice> getConnectedDevices() {

        return mBLEManager.getConnectedDevices(BluetoothProfile.GATT);
    }


    /**
     * 判断 设备是否通过蓝牙连接了
     *
     * @param mac 要检查的设备的MAC地址
     * @return true APP已经连接了当前设备，false 没有连接当前设备
     */
    public boolean isConnectedDevice(String mac) {

//        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//        Class<BluetoothAdapter> bluetoothAdapterClass= BluetoothAdapter.class;
//        try {
//            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState",(Class[])null);
//            method.setAccessible(true);
//            int state = (int) method.invoke(adapter,(Object[]) null);
//            Log.i(TAG, "isConnectedDevice:  state="+state);
//            if(state == BluetoothAdapter.STATE_CONNECTED){
//                Log.i(TAG, "isConnectedDevice:  BluetoothAdapter.STATE_CONNECTED");
//                List<BluetoothDevice> deviceList = mBLEManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
//                for(BluetoothDevice bluetoothDevice: deviceList){
//                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected",(Class[])null);
//                    isConnectedMethod.setAccessible(true);
//                    boolean isConnected= (boolean) isConnectedMethod.invoke(bluetoothDevice,(Object[]) null);
//                    Log.i(TAG, "isConnectedDevice: 放射 返回获取连接状态");
//                    Log.i(TAG, "isConnectedDevice: isConnected"+isConnected);
//                    return isConnected;
//                }
//
//            }
//
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }


        List<BluetoothDevice> deviceList = mBLEManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
        Log.i(TAG, "isConnectedDevice: deviceList = " + deviceList.size());
        for (BluetoothDevice bluetoothDevice : deviceList) {
            Method isConnectedMethod = null;
            try {
                isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                boolean isConnected = (boolean) isConnectedMethod.invoke(bluetoothDevice, (Object[]) null);
                Log.i(TAG, "isConnectedDevice: 放射 返回获取连接状态");
                Log.i(TAG, "isConnectedDevice: isConnected=" + isConnected);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mac.equals(bluetoothDevice.getAddress())) {
                return true;
            }
        }
        Log.i(TAG, "isConnectedDevice: isConnected =false");
        return false;
    }

    public void disconnect() {
        if (mBLEAdapter == null || mBLEGatt == null) {
            Log.w(TAG, "disconnect: BluetoothAdapter not initialized");
            return;
        }

//        Log.i(TAG, "service onItemClick: 断开连接的 操作线程="+Thread.currentThread().getName());
//        Log.i(TAG, "320 disconnect: ble service 中的断开设备连接的方法");
        autoConnect = false;
        mConnectionState = STATE_DISCONNECTED;
        mBLEGatt.disconnect();
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {

            bleConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bleConnection(gatt, status, newState);
                if (mConnectionState == STATE_BIND_CONNECTED) {
                    //已经配对过设备了，这个是为了防止连接一个设备的时候同时多次发现设备
                    return;
                }

                mConnectionState = STATE_CONNECTED;

                Message msg = new Message();
                msg.what = HANDLER_STATE_CONNECT;
                msg.arg1 = STATE_CONNECTED;
                handler.sendMessageDelayed(msg, HANDLER_STATE_CONNECT_DELAYED_TIME);
                sendLocalBroadcast(ACTION_BLE_CONNECTED);

                //延迟600ms 发送 发现服务的handler ,
                handler.sendEmptyMessageDelayed(HANDLER_DISCOVER_SERVICES, 600);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                bleDisconnection(gatt, status, newState);
                //设备断开连接
                //保存设备断开连接状态

                mConnectionState = STATE_DISCONNECTED;

                //发送断开连接的广播
                sendLocalBroadcast(ACTION_BLE_DISCONNECTED);
                gatt.close();
                if (mBLEGatt == gatt) {
                    mBLEGatt = null;
                }
                Log.i(TAG, "onConnectionStateChange:  断开蓝牙");
                //TODO 自动连接蓝牙设备
                if (autoConnect) {
                    Message msg = new Message();
                    msg.what = HANDLER_STATE_CONNECT;
                    msg.arg1 = STATE_CLOSE_GATT_RECONNECT;
                    handler.sendMessageDelayed(msg, HANDLER_STATE_CONNECT_DELAYED_TIME);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            onServicesDiscoveredCallback(gatt, status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            onCharacteristicChangedCallback(gatt, characteristic);

        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            // 写成功
            orderManager.removeFirst();
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            orderManager.removeFirst();
            //读成功
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //写完移动数据的通知
            gatt.setCharacteristicNotification(mBLEGCNotify, true);
            sendLocalBroadcast(ACTION_BLE_WRITE_NOTIFY);
        }
    };


    public void onServicesDiscoveredCallback(BluetoothGatt gatt, int status) {

        List<BluetoothGattService> list = gatt.getServices();
        BluetoothGattService bleGattService = null;
        for (BluetoothGattService BLEGS : list) {
            if (BLEGS.getUuid().equals(getServiceUUID())) {
                mConnectionState = STATE_FIND_SERVICE;
                bleGattService = gatt.getService(getServiceUUID());

                //控制功能
                mBLEGCWrite = bleGattService.getCharacteristic(getWriteUUID());
                //参数配置
                mBLEGCNotify = bleGattService.getCharacteristic(getNotifyUUID());
            }
        }


        if (bleGattService != null) {
            Log.i(TAG, "onServicesDiscovered: 发现蓝牙服务");

            mConnectionState = STATE_FIND_SERVICE;

            sendFindServiceHandler();


            sendLocalBroadcast(ACTION_BLE_FIND_SERVICE);
            orderManager = new BLEOrderManager(gatt);

            setCharacteristicNotification(mBLEGCNotify);
        } else {
            Log.i(TAG, "onServicesDiscovered: 没有发现服务");
        }

    }

    public byte[] sendLogInfo(byte deviceType) {
        isLogData = true;
        byte[] crcCommand = CommandUtil.getCRCFirmwarLogInfo(mBLECommunicationKey, deviceType);
        return writeToDevice(crcCommand);
    }

    public void sendNoLogInfo() {
        isLogData = false;
    }

    protected void sendFindServiceHandler() {
        Message msg = new Message();
        msg.what = HANDLER_STATE_CONNECT;
        msg.arg1 = STATE_FIND_SERVICE;
        handler.sendMessageDelayed(msg, HANDLER_STATE_CONNECT_DELAYED_TIME);
        sendLocalBroadcast(ACTION_BLE_FIND_SERVICE);
    }

    protected boolean isLogData = false;

    public void onCharacteristicChangedCallback(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String deviceAddress = gatt.getDevice().getAddress();
        // ble notify
        byte[] values = characteristic.getValue();
        Log.i(TAG, "onCharacteristicChanged: 蓝牙设备发送的数据=" + PrintUtil.toHexString(values));

        if (isLogData) {
            callbackLogData(values);
            return;
        }


        if (values.length == 20) {
            byte[] realData = new byte[18];
            System.arraycopy(values, 2, realData, 0, realData.length);
//            Log.i(TAG, "onCharacteristicChanged: 去除头部CRC16值="+ PrintUtil.toHexString(realData));
            int calcCRC16 = CRCUtil.calcCRC16(realData);
            int crc16 = ((values[0] & 0xFF) << 8) | (values[1] & 0xFF);
            if (calcCRC16 == crc16) {
                // crc 16 校验通过
                int nPack = ((realData[0] & 0xFF) << 8) | (realData[1] & 0xFF);
                // 不用解密。
                byte[] firmwareData = new byte[16];
                System.arraycopy(realData, 2, firmwareData, 0, firmwareData.length);
                onHandFirmwareDataCommand(nPack, firmwareData);

            }
        }


        // 获取正确的指令长度指令
        int start = 0;
        int copyLen = 0;
        for (int i = 0; i < values.length; i++) {
            if ((values[i] & 0xFF) == 0xA3 && (values[i + 1] & 0xFF) == 0xA4) {
                start = i;
                int len = values[i + 2];
                copyLen = len + 7; //16+
                break;
            }
        }
        if (copyLen == 0) return;
        byte[] real = new byte[copyLen];
        System.arraycopy(values, start, real, 0, copyLen);
//            Log.i(TAG, "onCharacteristicChanged: 提取接收到的正式数据="+ PrintUtil.toHexString(real));

        // 计算CRC8 值
        byte[] command = new byte[real.length - 1];
        for (int i = 0; i < command.length; i++) command[i] = real[i];
//            Log.i(TAG, "onCharacteristicChanged: 计算CRC的数组="+ getCommForHex(command));
        int crc8 = CRCUtil.calcCRC8(command);
        int vCrc = real[real.length - 1] & 0xFF;
//            Log.i(TAG, "onCharacteristicChanged: 计算的CRC="+crc8);
//            Log.i(TAG, "onCharacteristicChanged: 接收的CRC="+(real[real.length-1]&0xFF));
        // 验证CRC8值。
        if (crc8 == vCrc) {
            // crc8校验成功
            // 解密接收到数据
            byte rand = (byte) (real[3] - 0x32);
            command[3] = rand;
            for (int i = 4; i < command.length; i++) {
                command[i] = (byte) (command[i] ^ rand);
            }
            onHandNotifyCommand(deviceAddress, command);
        } else {
            // CRC8 校验失败
            Log.i(TAG, "onCharacteristicChanged: CRC8 校验失败");
        }

    }


    public void onHandNotifyCommand(String mac, byte[] command) {
//        Log.i(TAG, "onHandNotifyCommand: "+ PrintUtil.toHexString(command));
        switch (command[5]) {
            case CommandType.COMMUNICATION_KEY:
                Log.i(TAG, "onHandNotifyCommand: GET KEY" + PrintUtil.toHexString(command));
                handCommunicationKey(mac, command);
                break;
            case CommandType.ERROR:
                handCommandError(command);
                break;
            case CommandType.DEVICE_DATA:

                handCommandFB(command);
                break;
            case CommandType.GET_FIRMWARE_DATA:
                handTransmissionPack(command);
                break;
        }
    }


    protected void callbackTransmissionPack(int curPack, int deviceType) {


        Intent intent = new Intent(ACTION_BLE_TRANSMISSION_PACK);
        intent.putExtra(EXTRA_TRANSMISSION_PACK_PACK, curPack);
        intent.putExtra(EXTRA_TRANSMISSION_PACK_TYPE, deviceType);
        sendLocalBroadcast(intent);

    }

    private void handTransmissionPack(byte[] command) {
        int curPack = ((command[6] & 0xFF) << 8) | (command[7] & 0xFF); // 总包数
        int deviceType = command[8] & 0xFF;
        callbackTransmissionPack(curPack, deviceType);
//        Log.i(TAG, "handTransmissionPack: curPack ="+curPack);
//        Log.i(TAG, "handTransmissionPack: deviceType ="+deviceType);
    }


    public void handCommandFB(byte[] command) {
        int status = command[6];
        if (status == 0) {
            Log.i(TAG, "handCommandFB: 升级指令密码发送错误");
        } else if (status == 1) {
            Log.i(TAG, "handCommandFB: 升级状态就绪");
        } else if (status == 2) {
            Log.i(TAG, "handCommandFB: 升级数据完成");
        } else if (status == 3) {
            Log.i(TAG, "handCommandFB: 升级数据 接收超时");
        } else if (status == 4) {
            Log.i(TAG, "handCommandFB: 总文件CRC错误");
        }

    }


    public void onHandFirmwareDataCommand(int nPack, byte[] command) {
        Log.i(TAG, "onHandFirmwareDataCommand: nPack=" + nPack);
        Log.i(TAG, "onHandFirmwareDataCommand: " + PrintUtil.toHexString(command));

    }


    private void handCommandError(byte[] command) {
        Log.i(TAG, "handCommandError: 指令失败");
        int status = command[6];
        callbackCommandError(status);
    }


    protected byte mBLECommunicationKey = 0;
    protected int totalPack = 0;

    protected void handCommunicationKey(String mac, byte[] command) {

        Log.e(TAG, "获取key返回数据 =》" + Arrays.toString(command));

        int flag = command[6];
        if (flag == 1) {
            mBLECommunicationKey = command[7];
            callbackCommunicationKey(mac, mBLECommunicationKey);


        } else {
            callbackCommunicationKeyError();
            Log.i(TAG, "handCommunicationKey: 获取通信KEY失败");
        }

    }


    protected void callbackLogData(byte[] logData) {

    }


    /**
     * 默认实现，发送获取到KEY 的广播
     *
     * @param mac              设备MAC地址
     * @param communicationKey 通信KEY
     */
    protected void callbackCommunicationKey(String mac, byte communicationKey) {
        Intent intent = new Intent(ACTION_BLE_OPT_GET_KEY_WITH_MAC);
        intent.putExtra("mac", mac);
        intent.putExtra("ckey", mBLECommunicationKey);
        sendLocalBroadcast(intent);
    }

    /**
     * 默认实现 获取通信KEY指令 错误的广播
     */
    protected void callbackCommunicationKeyError() {


        sendLocalBroadcast(ACTION_BLE_OPT_GET_KEY_ERROR);

    }

    protected void callbackCommandError(int status) {
    }


    protected void bleConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
    }

    protected void bleConnection(final BluetoothGatt gatt, final int status, final int newState) {
    }

    protected void bleDisconnection(final BluetoothGatt gatt, final int status, final int newState) {
    }


    /**
     * 检测当前连接状态，并查看是否重新连接
     */
    protected final static int HANDLER_STATE_CONNECT = 3;
    protected final static int HANDLER_DISCOVER_SERVICES = 30;

    /**
     * 连接状态操作 ，关闭 gatt 重新连接设备
     */
    public static final int STATE_CLOSE_GATT_RECONNECT = 16;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

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
                        } else if (preConnectionState == STATE_CLOSE_GATT_RECONNECT) {

                        }
                    }
                    break;
            }
        }
    };

    protected void discoverServices(BluetoothGatt gatt) {
        if (gatt != null) gatt.discoverServices();
    }

    protected void setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (mBLEAdapter == null || mBLEGatt == null) {
            Log.w(TAG, "setCharacteristicNotification: BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(getDescriptorNotifyUUID());
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBLEGatt.writeDescriptor(descriptor);
    }

    /**
     * 发送 本地广播
     *
     * @param action 字符串格式的广播action
     */
    protected void sendLocalBroadcast(final String action) {

        final Intent intent = new Intent(action);
        sendLocalBroadcast(intent);
    }

    /**
     * 发送 本地广播
     *
     * @param intent intent 形式的广播action
     */
    protected void sendLocalBroadcast(final Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * 用于 写数据
     */
    protected BluetoothGattCharacteristic mBLEGCWrite;
    /**
     * 用于 获取锁发送过来的通知
     */
    protected BluetoothGattCharacteristic mBLEGCNotify;

    /**
     * 连接上蓝牙设备的广播
     */
    public static final String ACTION_BLE_CONNECTED = "com.omni.ble.library.ACTION_BLE_CONNECTED";
    /**
     * 断开蓝牙设备的广播
     */
    public static final String ACTION_BLE_DISCONNECTED = "com.omni.ble.library.ACTION_BLE_DISCONNECTED";

    /**
     * 找到蓝牙设备的服务的广播
     */
    public final static String ACTION_BLE_FIND_SERVICE = "com.omni.ble.library.ACTION_BLE_FIND_SERVICE";
    /**
     * 连接了蓝牙设备，注册了通知的广播
     */
    public final static String ACTION_BLE_WRITE_NOTIFY = "com.omni.ble.library.ACTION_BLE_WRITE_NOTIFY";
    /**
     * 扫描到BLE设备
     */
    public final static String ACTION_BLE_SCAN_DEVICE = "com.omni.ble.library.ACTION_BLE_SCAN_DEVICE";
    /**
     * 指令时间内，没有扫描到想到扫描的设备的广播通知
     */
    public final static String ACTION_BLE_SCAN_DEVICE_NOT = "com.omni.ble.library.ACTION_BLE_SCAN_DEVICE_NOT";

    /**
     * 设备通信广播，获取了通信的操作的KEY
     */
    public final static String ACTION_BLE_OPT_GET_KEY_WITH_MAC = "com.omni.ble.library.ACTION_BLE_OPT_GET_KEY_WITH_MAC";
    public final static String ACTION_BLE_OPT_GET_KEY_ERROR = "com.omni.ble.library.ACTION_BLE_OPT_GET_KEY_ERROR";

    /**
     * 日志广播
     */
    public final static String ACTION_BLE_LOG_DATA = "com.omni.ble.library.ACTION_BLE_LOG_DATA";

    /**
     * 抽象方法，子类自己实现，返回操作蓝牙设备服务的UUID
     *
     * @return 蓝牙服务的UUID
     */
    public abstract UUID getServiceUUID();

    /**
     * 抽象方法，子类自己实现，返回操作蓝牙 写操作的UUID
     *
     * @return 写操作的UUID
     */
    public abstract UUID getWriteUUID();

    /**
     * 抽象方法，子类自己实现，返回操作蓝牙 通知的UUID
     *
     * @return 通知的UUID
     */
    public abstract UUID getNotifyUUID();

    public UUID getDescriptorNotifyUUID() {
        return GattAttributes.UUID_NOTIFICATION_DESCRIPTOR;
    }

    /**
     * 通过uuid的字符串值，获取到UUID 对象
     *
     * @param uuid UUID值
     * @return UUID对象
     */
    public UUID getUUID(String uuid) {
        return UUID.fromString(uuid);
    }


    /**
     * 将数据写入设备
     *
     * @param data 要写入设备的数据
     * @return 数据byte[]
     */
    protected byte[] writeToDevice(byte[] data) {
        if (mBLEAdapter == null) {
            Log.w(TAG, "writeToDevice: BluetoothAdapter not initialized");
            return null;
        }
        if (mBLEGatt == null) {
            Log.w(TAG, "writeToDevice: mBLEGatt not initialized");
            return null;
        }

//        Log.e(TAG, "发送的数据 =》 " + Arrays.toString(data));

        orderManager.addWriteOrder(mBLEGCWrite, data);
        return data;
    }

    public static final String EXTRA_SCAN_DEVICE = "com.omni.ble.library.EXTRA_SCAN_DEVICE";
    public static final String EXTRA_SCAN_RECORD = "com.omni.ble.library.EXTRA_SCAN_RECORD";
    public static final String EXTRA_DEVICE_MAC = "com.omni.ble.library.EXTRA_DEVICE_MAC";
    private boolean isScanDevice = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MyScanCb extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            // 居然也在main 线程中
            // 新的回调在主线程中
//            Log.i(TAG, "onScanResult:新 thread name="+Thread.currentThread().getName());
            Log.i(TAG, "onScanResult: name=" + result.getDevice().getName());
            Log.i(TAG, "onScanResult: mac=" + result.getDevice().getAddress());
//            Log.i(TAG, "onScanResult: byte[] ="+ PrintUtil.toHexString(result.getScanRecord().getBytes()) );

            // 电量指令

            isScanDevice = true;
            // 发送扫描到设备的广播
            Log.i(TAG, "onScanResult:845 发送扫描到蓝牙的广播");
            Intent intent = new Intent(ACTION_BLE_SCAN_DEVICE);
            intent.putExtra(EXTRA_SCAN_DEVICE, result.getDevice());
            intent.putExtra(EXTRA_SCAN_RECORD, result.getScanRecord().getBytes());
            sendLocalBroadcast(intent);

            stopScanBLEDevice();
        }
    }

    private BluetoothAdapter.LeScanCallback mLescanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            Log.i(TAG, "onScanResult:旧 thread name="+Thread.currentThread().getName());
            // 回调在异步线程中
            String macAddress = device.getAddress();
            if (mBLEAdapter != null && (filterMac.equals(macAddress))) {
                isScanDevice = true;
                Log.i(TAG, "onScanResult: mac=" + device.getAddress());
                Intent intent = new Intent(ACTION_BLE_SCAN_DEVICE);
                intent.putExtra(EXTRA_SCAN_DEVICE, device);
                intent.putExtra(EXTRA_SCAN_RECORD, scanRecord);
                sendLocalBroadcast(intent);

                stopScanBLEDevice();
            }
        }
    };


    public byte[] sendUpdateFirmwareCommand(int nPack, int crc, byte deviceType, String updateKey) {
        Log.i(TAG, "sendGetFirmwareInfo:发送升级指令");
        byte[] crcOrder = CommandUtil.getCRCUpdateFirmwareCommand(mBLECommunicationKey, nPack, crc, deviceType, updateKey);
        return writeToDevice(crcOrder);
    }

    public byte[] sendTransmissionData(int pack, byte[] data) {
        byte[] crcOrder = CommandUtil.getCRCTransmissionData(pack, data);
//        Log.i(TAG, "sendTransmissionData: data[]="+PrintUtil.toHexString(crcOrder));
        return writeToDevice(crcOrder);
    }


    public byte[] sendUpdateTransmissionCommand(byte transmissionType, int nPack, int crc, byte deviceType, String updateKey) {
        Log.i(TAG, "sendUpdateTransmissionCommand:发送数据传输指令");
        byte[] crcOrder = CommandUtil.getCRCUpdateTransmissionCommand(transmissionType, mBLECommunicationKey, nPack, crc, deviceType, updateKey);
        return writeToDevice(crcOrder);
    }


}
