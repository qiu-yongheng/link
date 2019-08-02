package com.omni.ble.library.model;

import com.omni.ble.library.BuildConfig;

/**
 * <br />
 * created by CxiaoX at 2017/12/13 18:37.
 */

public class CommandType {


    public static final byte DEVICE_INFO = (byte) 0xFA;
    /**
     * 数据传输指令
     */
    public static final byte DEVICE_DATA = (byte) 0xFB;

    /**
     * 获取传输数据 包数 指令
     * 即APP 问设备要传输的第几包数据(APP 获取 设备 的 固件信息)
     * 或者 设备问APP 要传输的第几包数据 (设备 获取 APP发送的系统配置信息)
     */
    public static final byte GET_FIRMWARE_DATA = (byte) 0xFC;

    //TODO 由原来的CC 改为C7
//    public static final byte LOG_DATA = BuildConfig.SUPPORT_CARPORT ? (byte) 0xCC : (byte) 0xC7;
    public static  final byte LOG_DATA = (byte)0xC7;
//    public static  final byte LOG_DATA = (byte)0xCC;

    public static final byte COMMUNICATION_KEY = 0x01;
    public static final byte ERROR = 0x10;

    public static final byte CONTROL_DOWN = 0x05;
    public static final byte SCOOTER_SET = 0x61;
    public static final byte SCOOTER_COLOR_LIGHT = (byte) 0x82;

    public static final byte CONTROL_UP = 0x15;
    public static final byte PAIR_REMOTE = 0x06;
    public static final byte DEVICE_MODEL = 0x07;
    /**
     * 设备自己的MAC地址。
     */
    public static final byte DEVICE_LOCAL_MAC = 0x03;
    public static final byte DEVICE_MAC_HAND_PAIR = 0x04; // 设备用于配对的MAC
    public static final byte MODIFY_DEVICE_KEY = 0x33; // 设备用于配对的MAC
    public static final byte OLD_DATA = 0x51; // 旧数据
    public static final byte SCOOTER_POWER_CONTROL = (byte) 0x91; // 滑板车整体关机
    public static final byte SCOOTER_XM_POWER_CONTROL = (byte) 0x90; // 滑板车整体关机
    public static final byte CLEAR_DATA = 0x52; // 清除旧数据

    public static final byte CARPORT_DEVICE_INFO = 0x31;

    public static final byte MODEL_AUTO = 2;
    public static final byte MODEL_MANUAL = 1;


    public static final byte SEND_CONTROL = 0x01;
    public static final byte SEND_RESPONSE = 0x02;


    public static final byte KEY_LOCK_COMMUNICATION_KEY = 0x01;
    public static final byte KEY_LOCK_ERROR = 0x10;
    public static final byte KEY_LOCK_OPEN = 0x21;
    public static final byte KEY_LOCK_CLOSE = 0x22;
    public static final byte KEY_LOCK_INFO = 0x31;
    public static final byte KEY_LOCK_LEFT = 0x06;
    public static final byte KEY_LOCK_BACK = 0x07;
    public static final byte KEY_LOCK_SET_TIMESTAMP = 0x02;
    public static final byte KEY_LOCK_ALERT = 0x03;
    public static final byte KEY_LOCK_MODIFY_DEVICE_KEY = 0x33;
    public static final byte KEY_LOCK_CONFIGS = 0x13;
    public static final byte KEY_LOCK_OPEN_RECORD = 0x51;
    public static final byte KEY_LOCK_CLEAR_OPEN_RECORD = 0x52;
    public static final byte KEY_LOCK_INPUT_PWD = 0x53;
    public static final byte KEY_LOCK_PWD_CONFIGS = 0x55;


    public static final byte BOX_LOCK_OPEN = 0x05;
    public static final byte MIFI_LOCK_CLOSE = 0x15;
    public static final byte BOX_LOCK_LOW_OPEN = 0x35;
    public static final byte BOX_LOCK_DEVICE_INFO = 0x31;


    public static final byte SCOOTER_INFO = 0x60;
    public static final byte SCOOTER_OLD_DATA = 0x51;
    public static final byte SCOOTER_CLOSE = 0x15;
    public static final byte SCOOTER_OPEN = 0x05;
    public static final byte SCOOTER_READ_CARD_ID = (byte) 0x85;
    /**
     * 滑板车 电池盖打开 指令
     */
    public static final byte SCOOTER_POPUP = (byte) 0x81;
//    public static  final byte CONTROL_UP = 0x02;
//    public static  final byte CONTROL_DOWN = 0x01;

}
