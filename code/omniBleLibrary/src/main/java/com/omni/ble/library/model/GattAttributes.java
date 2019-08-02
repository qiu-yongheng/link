package com.omni.ble.library.model;

import java.util.UUID;

/**
 * Description:ble uuid <br />
 */
public class GattAttributes {

    // 马蹄锁
    public final  static UUID UUID_SERVICE=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb7");
    public final  static UUID UUID_CHARACTERISTIC_WRITE=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cba");
    public final  static UUID UUID_CHARACTERISTIC_READ=UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8");

    // 纯蓝牙马蹄锁
    public final static UUID UUID_BLUETOOTH_SERVICE = UUID.fromString("6E400001-E6AC-A7E7-B1B3-E699BAE8D000");
    public final static UUID UUID_BLUETOOTH_CHARACTERISTIC_WRITE = UUID.fromString("6E400002-E6AC-A7E7-B1B3-E699BAE8D000");
    public final static UUID UUID_BLUETOOTH_CHARACTERISTIC_READ = UUID.fromString("6E400003-E6AC-A7E7-B1B3-E699BAE8D000");

    // 车位锁相关=============S
    public final  static UUID UUID_CARPORT_SERVICE =UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");

    public final  static UUID UUID_CARPORT_CHARACTERISTIC_WRITE =UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public final  static UUID UUID_CARPORT_CHARACTERISTIC_NOTIFY =UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    /** for notify */
    public final static UUID UUID_NOTIFICATION_DESCRIPTOR=UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
    // 车位锁相关=============E

    // 滑板车 IOT 相关=============S
    public final  static UUID UUID_SCOOTER_SERVICE =UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /** for notify */
    public final  static UUID UUID_SCOOTER_CHARACTERISTIC_WRITE =UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public final  static UUID UUID_SCOOTER_CHARACTERISTIC_NOTIFY =UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // 滑板车 IOT相关=============E


    public final  static UUID UUID_REMOTE_SERVICE=UUID.fromString("6F400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public final  static UUID UUID_REMOTE_CHARACTERISTIC_WRITE=UUID.fromString("6F400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public final  static UUID UUID_REMOTE_CHARACTERISTIC_NOTIFY=UUID.fromString("6F400003-B5A3-F393-E0A9-E50E24DCCA9E");
}
