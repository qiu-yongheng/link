package com.omni.ble.library.order;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.LinkedList;

/**
 * 控制蓝牙读写的管理类，在BLEService中得到Gatt的方法初始化,可以在发现服务的地方初始化<br />
 * created by CxiaoX at 2016/3/19 10:55.
 */
public class BLEOrderManager {
    private static final String TAG="BLEOrderManager";
    /** 指令列表 */
    private LinkedList<BLEOrder> orderQueue;
    /** 蓝牙Gatt,用于读写特征 */
    private BluetoothGatt mBLEGatt;

    /**
     * 构造函数
     * @param mBLEGatt  蓝牙的Gatt
     */
    public BLEOrderManager(BluetoothGatt mBLEGatt){
        this.mBLEGatt = mBLEGatt;
        orderQueue=new LinkedList<>();
    }

    public  void addOrder(BLEOrder order){
        orderQueue.addLast(order);
//        Log.i(TAG, "addOrder: 添加了一条指令到指令管理器，目前有size =" + orderQueue.size());

        if(orderQueue.size()==1){
            //新增加一条指令，如果只有这一条就执行
            executeOrder();
        }
    }

    public void addReadOrder(BluetoothGattCharacteristic bluetoothGattCharacteristic){
        BLEOrder readOrder = new BLEOrder(BLEOrder.TYPE_READ,bluetoothGattCharacteristic);
        addOrder(readOrder);
    }
    public void addReadRssiOrder( ){
        BLEOrder readOrder = new BLEOrder(BLEOrder.TYPE_READ_RSSI);
        addOrder(readOrder);
    }

    public void addWriteOrder(BluetoothGattCharacteristic bgc,byte[] values){
        BLEOrder writeOrder=new BLEOrder(BLEOrder.TYPE_WRITE,bgc,values);
        addOrder(writeOrder);
    }

    public void addWriteHeartOrder(BluetoothGattCharacteristic bgc,byte[] values){
        BLEOrder writeOrder=new BLEOrder(BLEOrder.TYPE_WRITE_HEART,bgc,values);
        addOrder(writeOrder);
    }

    /**
     * 删除第一条指令，并执行下一条指令
     */
    public void removeFirst(){
        if(!orderQueue.isEmpty()) {
            //删除一条指令
            orderQueue.removeFirst();
            //执行剩下的指令
            executeOrder();
        }
    }

    /**
     * 获取到第一条指令，并执行下一条指令
     */
    public BLEOrder getFirst(){
        if(!orderQueue.isEmpty()) {
            //删除一条指令
           return  orderQueue.getFirst();
        }
        return null;
    }


    /**
     * 根据指令类型执行指令
     */
    public void executeOrder(){ //0121
        if(!orderQueue.isEmpty()){
            BLEOrder order = orderQueue.getFirst();
            if(order.getType()==BLEOrder.TYPE_READ){
                mBLEGatt.readCharacteristic(order.getBLEgc());
            }else if(order.getType()==BLEOrder.TYPE_WRITE ){
                BluetoothGattCharacteristic mBGC = order.getBLEgc();
                mBGC.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                mBGC.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBGC.setValue(order.getValues());
                mBLEGatt.writeCharacteristic(mBGC);
            }else if( order.getType()==BLEOrder.TYPE_WRITE_HEART){
                BluetoothGattCharacteristic mBGC = order.getBLEgc();
                mBGC.setValue(order.getValues());
                mBLEGatt.writeCharacteristic(mBGC);
            }
            else if(order.getType()==BLEOrder.TYPE_READ_RSSI ){
                mBLEGatt.readRemoteRssi();
            }
        }
    }
}
