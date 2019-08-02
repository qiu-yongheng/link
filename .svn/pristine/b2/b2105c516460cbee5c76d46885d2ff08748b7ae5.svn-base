package com.omni.ble.library.utils;


import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.lib.utils.PrintUtil;

import java.util.Random;

/**
 * <br />
 * created by CxiaoX at 2017/4/22 18:02.
 */

public class CommandUtil extends BaseCommand {

    private static final String TAG = "CommandUtil";

    private static byte[] getCommand(byte ckey, byte commandType, byte[] data) {
        byte[] head = new byte[]{(byte) 0xA3, (byte) 0xA4};
        byte len = (byte) data.length; // 数据的长度
//        byte rand = (byte) (new Random().nextInt(255) & 0xff);
        byte rand = (byte) (0x45);
        byte[] command = addBytes(head, new byte[]{len, rand, ckey, commandType});
        return addBytes(command, data);

    }

    public static byte[] getCRCCarportDown(byte ckey, int uid, long timestamp) {
        byte[] data = new byte[]{0x01};
        data = addBytes(data, uid);
        data = addBytes(data, timestamp);
        data = addBytes(data, new byte[]{0x0});
        byte[] command = getCommand(ckey, CommandType.CONTROL_DOWN, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCCarportDown(byte ckey, byte mode, int uid, long timestamp) {
        byte[] data = new byte[]{mode};
        data = addBytes(data, uid);
        data = addBytes(data, timestamp);
        data = addBytes(data, new byte[]{0x0});
        byte[] command = getCommand(ckey, CommandType.CONTROL_DOWN, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCDeviceInfo(byte ckey) {
        byte[] data = new byte[]{0x01};
        byte[] command = getCommand(ckey, CommandType.CARPORT_DEVICE_INFO, data);
        return getXorCRCCommand(command);
    }


    public static byte[] getCRCPairRemote(byte ckey, byte[] mac) {
        byte[] data = new byte[]{0x01};
        data = addBytes(data, mac);
        byte[] command = getCommand(ckey, CommandType.PAIR_REMOTE, data);

        Log.i(TAG, "getCRCPairRemote: 车位锁配对遥控器 byte[]=" + PrintUtil.toHexString(command));

        return getXorCRCCommand(command);
    }


    public static byte[] getCRCCarportDownResponse(byte ckey) {
        byte[] data = new byte[]{0x02};
        byte[] command = getCommand(ckey, CommandType.CONTROL_DOWN, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCCarportUp(byte ckey) {
        byte[] data = new byte[]{0x01};
        byte[] command = getCommand(ckey, CommandType.CONTROL_UP, data);
        return getXorCRCCommand(command);
    }


    public static byte[] getCRCCarportUpResponse(byte ckey) {
        byte[] data = new byte[]{0x02};
        byte[] command = getCommand(ckey, CommandType.CONTROL_UP, data);
        return getXorCRCCommand(command);
    }

    private static String getCommForHex(byte[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            sb.append(String.format("%02X,", values[i]));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public static byte[] getCRCCommunicationKey(String deviceKey) {
        byte[] data = new byte[8];
        for (int i = 0; i < deviceKey.length(); i++) {
            data[i] = (byte) deviceKey.charAt(i);
        }
        byte[] command = getCommand((byte) 0, CommandType.COMMUNICATION_KEY, data);
        return getXorCRCCommand(command);
    }


    public static byte[] getCRCModifyDeviceKey(byte cKey, String deviceKey) {

        byte[] data = new byte[9];
        data[0] = 1;
        for (int i = 0; i < deviceKey.length(); i++) {
            data[i + 1] = (byte) deviceKey.charAt(i);
        }
        byte[] command = getCommand(cKey, CommandType.MODIFY_DEVICE_KEY, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCClearDeviceKey(byte cKey) {

        byte[] data = new byte[]{0x02};

        byte[] command = getCommand(cKey, CommandType.MODIFY_DEVICE_KEY, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCGetOldData(byte cKey) {

        byte[] data = new byte[]{0x01};

        byte[] command = getCommand(cKey, CommandType.OLD_DATA, data);
        return getXorCRCCommand(command);
    }


    /**
     * @param cKey
     * @param opt  1-关机，2-开机
     * @return
     */
    public static byte[] getCRCShutDown(byte cKey, byte opt) {

        byte[] data = new byte[]{opt};

        byte[] command = getCommand(cKey, CommandType.SCOOTER_POWER_CONTROL, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCShutDown(byte cKey, byte orderType, byte opt) {

        byte[] data = new byte[]{opt};

        byte[] command = getCommand(cKey, orderType, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCClearOldData(byte cKey) {

        byte[] data = new byte[]{0x01};

        byte[] command = getCommand(cKey, CommandType.CLEAR_DATA, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCGetPairInfo(byte cKey) {

        byte[] data = new byte[]{0x01};

        byte[] command = getCommand(cKey, CommandType.DEVICE_MAC_HAND_PAIR, data);
        return getXorCRCCommand(command);
    }

    /**
     * 获取车锁锁 本地MAC 地址
     *
     * @param ckey
     * @return
     */
    public static byte[] getCRCGetLocalMac(byte ckey) {
        byte[] data = new byte[]{0x01};
        byte[] command = getCommand(ckey, CommandType.DEVICE_LOCAL_MAC, data);
        return getXorCRCCommand(command);
    }

    /**
     * 获取已经配对的MAC地址
     *
     * @param ckey
     * @return
     */
    public static byte[] getCRCHadMacPair(byte ckey) {
        byte[] data = new byte[]{0x01};
        byte[] command = getCommand(ckey, CommandType.DEVICE_MAC_HAND_PAIR, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCModelCommand(byte ckey, byte model) {
        byte[] data = new byte[]{0x01, model};
        byte[] command = getCommand(ckey, CommandType.DEVICE_MODEL, data);
        return getXorCRCCommand(command);
    }

    public static byte[] getCRCModelResponse(byte ckey) {
        byte[] data = new byte[]{0x02};
        byte[] command = getCommand(ckey, CommandType.DEVICE_MODEL, data);
        return getXorCRCCommand(command);
    }


    private static final byte ORDER_KEY = 0x11;
    private static final byte ORDER_UN_LOCK = 0x21;
    private static final byte ORDER_LOCK = 0x22;
    private static final byte ORDER_LOCK_STATUS = 0x31;
    private static final byte ORDER_FW_INFO = (byte) 0xFA;
    private static final byte ORDER_OLE_DATA = 0x51;
    private static final byte ORDER_CLEAR_OLE_DATA = 0x52;
    private static final byte ORDER_POPUP = (byte) 0x81;


    public static byte[] getCRCLockCommand(byte bleKey) {
        byte[] command = getLockCommand(bleKey);
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    public static byte[] getCRCOpenResCommand(byte bleKey) {
        byte[] command = getCommand(bleKey, ORDER_UN_LOCK, (byte) 0x00);
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    public static byte[] getCRCOpenCommand(int uid, byte bleKey, long timestamp, byte openType) {
        byte[] command = getOpenCommand(uid, bleKey, timestamp, openType);
        Log.i(TAG, "getCRCOpenCommand: cmm=" + getCommForHex(command));
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        Log.i(TAG, "getCRCOpenCommand: CRC cmm=" + getCommForHex(crcOrder));
        return crcOrder;
    }

    public static byte[] getCRCLockStatusCommand(byte bleKey) {
        byte[] command = getLockStatusCommand(bleKey);
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    /**
     * 获取固件信息
     *
     * @param uid
     * @param bleKey
     * @return 返回固件信息包数
     */
    public static byte[] getFwInfoCommand(int uid, byte bleKey) {
        byte[] command = getCommand(bleKey, ORDER_FW_INFO, (byte) 0x00);
        Log.d(TAG, "getFwInfoCommand send: " + getCommForHex(command));
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    /**
     * 获取固件信息
     *
     * @param bleKey
     * @param nPack
     * @param deviceType
     * @return
     */
    public static byte[] getFwInfoPackCommand(byte bleKey, int nPack, byte deviceType) {
        byte[] command = getFirmwareInfoDetailCommand(bleKey, nPack, deviceType);
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    private static byte[] getFirmwareInfoDetailCommand(byte bleKey, int nPack, byte dType) {
        byte randKey = (byte) (new Random().nextInt(255) & 0xff);

        byte[] command = new byte[8];// 不包含CRC校验的长度
        command[0] = (byte) 0xFE;
        command[1] = (byte) (randKey); //随机数 x1

        command[2] = bleKey;  // key
        command[3] = (byte) 0xFB;  // 命令代码
        command[4] = 0x03;  // 长度
        command[5] = (byte) ((nPack >> 8) & 0xFF);
        command[6] = (byte) (nPack & 0xFF);
        command[7] = dType;
        return command;
    }

    public static byte[] getCRCOldDataCommand(byte bleKey) {
        byte[] command = geOldDataCommand(bleKey);
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    public static byte[] getCRCClearDataCommand(byte bleKey) {
        // 原
        byte[] command = geClearDataCommand(bleKey);
        // 加密
        byte[] xorCommand = decode(command);
        // CRC
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    public static byte[] geClearDataCommand(byte bleKey) {
        return getCommand(bleKey, ORDER_CLEAR_OLE_DATA, (byte) 0x00);
    }

    public static byte[] geOldDataCommand(byte bleKey) {
        return getCommand(bleKey, ORDER_OLE_DATA, (byte) 0x00);
    }

    public static byte[] getLockStatusCommand(byte bleKey) {
        return getCommand(bleKey, ORDER_LOCK_STATUS, (byte) 0x00);
    }

    private static byte[] getLockCommand(byte bleKey) {
        return getCommand(bleKey, ORDER_LOCK, (byte) 0x00);
    }

    public static byte[] getCRCKeyCommand2(String bleKey) {
        byte[] command = getKeyCommand2(bleKey);
        Log.d(TAG, "获取key: " + PrintUtil.toHexString(command));
        byte[] xorCommand = decode(command);
        byte[] crcOrder = CRCByte(xorCommand);
        return crcOrder;
    }

    private static byte[] decode(byte[] command) {
        byte[] xorComm = new byte[command.length];
        xorComm[0] = command[0];
        xorComm[1] = (byte) (command[1] + 0x32);
        for (int i = 2; i < command.length; i++) {
            xorComm[i] = (byte) (command[i] ^ command[1]);
        }
        return xorComm;
    }

    private static byte[] CRCByte(byte[] ori) {
        byte[] ret = new byte[ori.length + 2];
        int crc = CRCUtil.calcCRC(ori);
        for (int i = 0; i < ori.length; i++) ret[i] = ori[i];
        ret[ori.length] = (byte) ((crc >> 8) & 0xFF);
        ret[ori.length + 1] = (byte) (crc & 0xFF);
        return ret;
    }

    private static byte[] getKeyCommand2(String bleKey) {
        byte randKey = (byte) (new Random().nextInt(255) & 0xff);

        byte[] command = new byte[13];// 不包含CRC校验的长度
        command[0] = (byte) 0xFE;
        command[1] = (byte) (randKey); //随机数 x1
        command[2] = 0;  // key
        command[3] = ORDER_KEY;  // 命令代码

        command[4] = 0x08;  // 长度
        // yOTmK50z

        byte[] bytes = bleKey.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            command[5 + i] = bytes[i];
        }

        // 0x79 0x4F 0x54 0x6D 0x4B 0x35 0x30 0x7A
//        command[5] = 'y';  //
//        command[6] = 'O';  //
//        command[7] = 'T';  //
//        command[8] = 'm';  //
//        command[9] = 'K';  //
//        command[10] = '5'; //
//        command[11] = '0'; //
//        command[12] = 'z'; //

//        command[5]= 'd';
//        command[6]= 'B';
//        command[7]= 'O';
//        command[8]= '3';
//        command[9]= 'd';
//        command[10]= 'w';
//        command[11]= 'P';
//        command[12]= 'Q';
//        byte randKey = (byte) (new Random().nextInt(255) & 0xff);
//        byte uidB1=(byte) (( 0>>24)&0xFF);
//        byte uidB2=(byte) (( 0>>16)&0xFF);
//        byte uidB3=(byte) ( ( 0>>8)&0xFF);
//        byte uidB4=(byte) ( 0 &0xFF);
//        byte[] command=new byte[17];// 不包含CRC校验的长度
//        command[0]= (byte) 0xFE;
//        command[1]=(byte) (randKey); //随机数 x1
//        command[2]= uidB1; // 用户id
//        command[3]= uidB2;
//        command[4]= uidB3;
//        command[5]= uidB4;
//        command[6]= 0;  // key
//        command[7]= ORDER_KEY;  // 命令代码
//        command[8]= 0x08;  // 长度
//        // yOTmK50z
//        // 0x79 0x4F 0x54 0x6D 0x4B 0x35 0x30 0x7A
//        command[9]=  'y';  //
//        command[10]= 'O';  //
//        command[11]= 'T';  //
//        command[12]= 'm';  //
//        command[13]= 'K';  //
//        command[14]= '5';  //
//        command[15]= '0';  //
//        command[16]= 'z';  //
        return command;
    }

    private static byte[] getCommand(byte bleKey, byte order, byte len) {
        byte randKey = (byte) (new Random().nextInt(255) & 0xff);

        byte[] command = new byte[5];// 不包含CRC校验的长度
        command[0] = (byte) 0xFE;
        command[1] = (byte) (randKey); //随机数 x1
        command[2] = bleKey;  // key
        command[3] = order;  // 命令代码
        command[4] = len;  // 长度
        return command;
    }

    private static byte[] getFWInfoCommand(int uid, byte key, byte order, byte len) {
        byte randKey = (byte) (new Random().nextInt(255) & 0xff);
        byte uidB1 = (byte) ((uid >> 24) & 0xFF);
        byte uidB2 = (byte) ((uid >> 16) & 0xFF);
        byte uidB3 = (byte) ((uid >> 8) & 0xFF);
        byte uidB4 = (byte) (uid & 0xFF);

        byte[] command = new byte[9];// 不包含CRC校验的长度
        command[0] = (byte) 0xFE;
        command[1] = randKey; //随机数 x1
        command[2] = uidB1;
        command[3] = uidB2;
        command[4] = uidB3;
        command[5] = uidB4;
        command[6] = key;  // key
        command[7] = order;  // 命令代码
        command[8] = len;  // 长度
        return command;
    }

    public static byte[] getOpenCommand(int uid, byte bleKey, long timestamp, byte openType) {
        byte randKey = (byte) (new Random().nextInt(255) & 0xff);
        byte uidB1 = (byte) ((uid >> 24) & 0xFF);
        byte uidB2 = (byte) ((uid >> 16) & 0xFF);
        byte uidB3 = (byte) ((uid >> 8) & 0xFF);
        byte uidB4 = (byte) (uid & 0xFF);

        byte time1 = (byte) ((timestamp >> 24) & 0xFF);
        byte time2 = (byte) ((timestamp >> 16) & 0xFF);
        byte time3 = (byte) ((timestamp >> 8) & 0xFF);
        byte time4 = (byte) (timestamp & 0xFF);
        byte[] command = new byte[14];// 不包含CRC校验的长度
        command[0] = (byte) 0xFE;
        command[1] = (byte) (randKey); //随机数 x1

        command[2] = bleKey;  // key
        command[3] = ORDER_UN_LOCK;  // 命令代码
        command[4] = 0x09;  // 长度
        command[5] = uidB1; // 用户id
        command[6] = uidB2;
        command[7] = uidB3;
        command[8] = uidB4;
        command[9] = time1; // 用户id
        command[10] = time2;
        command[11] = time3;
        command[12] = time4;
        command[13] = openType;

        return command;
    }

}
