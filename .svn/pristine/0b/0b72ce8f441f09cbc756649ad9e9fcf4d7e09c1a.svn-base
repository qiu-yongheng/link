package com.omni.ble.library.model;

import android.bluetooth.BluetoothDevice;

/**
 * Description: 蓝牙设备的实体类，包括标准的蓝牙device 和搜索到该设备时的信号值<br />
 * Created by CxiaoX on 2016/3/19 10:37.
 */
public class BLEDevice {
    /** 扫描到的设备 */
    private BluetoothDevice device;
    /** 扫描到的设备的信号值 */
    private int rssi;

    private byte[] scanRecord=null;

    /**
     * 构造函数
     * @param device  扫描到的设备
     * @param rssi    扫描到的设备的信号值
     */
    public BLEDevice(BluetoothDevice device, int rssi){
        this.device=device;
        this.rssi= rssi;
    }
    /** 得到扫描到的设备 */
    public BluetoothDevice getDevice() {
        return device;
    }
    /** 设置扫描到的设备 */
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
    /** 得到扫描到的设备的信号值 */
    public int getRssi() {
        return rssi;
    }
    /** 设置扫描到的设备的信号值 */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    public byte[] getScanRecord() {
        return scanRecord;
    }

    public int getPowerByScanRecord(){
        int result = 0;
        if(scanRecord!=null){

            result = ((scanRecord[14]&0xFF)<<8)|(scanRecord[15]&0xFF);
        }
        return result;
    }

    public int getLockStatus(){
        int result = 0;
        if(scanRecord!=null){

            result = scanRecord[13];
        }
        return result;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BLEDevice bleDevice = (BLEDevice) o;

        return !(device != null ? !device.equals(bleDevice.device) : bleDevice.device != null);

    }

    @Override
    public int hashCode() {
        return device != null ? device.hashCode() : 0;
    }



}
