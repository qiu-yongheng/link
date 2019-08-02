package com.omni.ble.library.order;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * 蓝牙指令，包括读特征，写特征，读信号量三大类<br />
 * created by CxiaoX at 2016/3/19 10:48.
 */
public class BLEOrder {
    /**  指令类型为读 */
    public static final int TYPE_READ=1;
    /**  指令类型为写 */
    public static final int TYPE_WRITE=2;
    /**  指令类型为读信号值 */
    public static final int TYPE_READ_RSSI=3;
    public static final int TYPE_WRITE_HEART=4;

    /** 表示当前读写指令类型 */
    private int type;
    /**  蓝牙设备中的特征 */
    private BluetoothGattCharacteristic BLEgc;
    /** 需要写的内容 */
    private byte[] values;

    /**
     * 读指令 构造方法，主要是读取信息值
     * @param type  读类型  {@link #TYPE_READ_RSSI}
     */
    public BLEOrder(int type ){
        this.type=type;
    }

    /**
     * 读指令 构造方法
     * @param type  读类型 {@link #TYPE_READ}
     * @param bgc   需要被读的蓝牙设备 特征
     */
    public BLEOrder(int type,BluetoothGattCharacteristic bgc){
        this.type=type;
        this.BLEgc = bgc;
    }

    /**
     * 写指令 构造方法
     * @param type  写类型 {@link #TYPE_WRITE}
     * @param bgc   需要被写的蓝牙设备 特征
     * @param values 需要写入的值
     */
    public BLEOrder(int type,BluetoothGattCharacteristic bgc,byte[] values){
        this.type=type;
        this.BLEgc = bgc;
        this.values=values;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BluetoothGattCharacteristic getBLEgc() {
        return BLEgc;
    }

    public void setBLEgc(BluetoothGattCharacteristic BLEgc) {
        this.BLEgc = BLEgc;
    }

    public byte[] getValues() {
        return values;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }
}
