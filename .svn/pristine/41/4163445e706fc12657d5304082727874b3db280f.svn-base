package com.omni.ble.library.utils;

import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.lib.utils.PrintUtil;

import java.util.Random;

/**
 * Created by lenovo on 2018/3/5.
 */

public class KeyLockCommand {

    private static  final  String TAG="KeyLockCommand";

    private static byte[] getCommand(byte ckey,byte commandType,byte[] data){
        byte[] head=new byte[]{(byte)0xA3,(byte)0xA4};
        byte len = (byte)data.length; // 数据的长度
        byte rand = (byte) (new Random().nextInt(255) & 0xff);
        byte[] command=addBytes(head,new byte[]{len,rand,ckey,commandType});
        return addBytes(command,data);

    }

    private static byte[] addBytes(byte[] a,byte[] b){
        byte[] ans=new byte[a.length+b.length];
        System.arraycopy(a,0,ans,0,a.length);
        System.arraycopy(b,0,ans,a.length,b.length);
        return ans;
    }

    private static byte[] addBytes(byte[] a,byte b){
        byte[] ans=new byte[a.length+1];
        System.arraycopy(a,0,ans,0,a.length);
        ans[a.length]=b;
        return ans;
    }

    /**
     *
     * @param a
     * @param b  int类型的指令，将转换成4字节的byte
     * @return
     */
    private static byte[] addBytes(byte[] a,int b){
        byte[] bBytes=new byte[]{(byte)((b>>24)&0xFF),(byte)((b>>16)&0xFF),(byte)((b>>8)&0xff),(byte)(b&0xFF)};
        return addBytes(a,bBytes);
    }
    private static byte[] addBytes(byte[] a,long b){
        return addBytes(a,(int)b);
    }

    public static byte[] getCRCCommunicationKey(String deviceKey ){
        byte[] data = new byte[8] ;
        for(int i=0;i<deviceKey.length();i++){
            data[i]= (byte) deviceKey.charAt(i);
        }
        byte[] command =getCommand((byte)0, CommandType.KEY_LOCK_COMMUNICATION_KEY,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCOpen(byte ckey, int uid,long timestamp ){
        byte[] data=new byte[]{0x01};
        data=addBytes(data,uid);
        data=addBytes(data,timestamp);
        data=addBytes(data,new byte[]{0x0});
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_OPEN,data);
        Log.i(TAG, "getCRCOpen: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCSetTimestamp(byte ckey, long timestamp ){
        byte[] data=new byte[0];
        data=addBytes(data,timestamp);

        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_SET_TIMESTAMP,data);
        Log.i(TAG, "getCRCSetTimestamp: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCAlertOpen(byte ckey, boolean isOpen ){

        byte status = 0;
        if(isOpen) {
            status = 1;
        }
        byte[] data=new byte[]{status};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_ALERT,data);
        Log.i(TAG, "getCRCSetTimestamp: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCModifyDeviceKey(byte ckey,  String deviceKey ){


        byte[] data=new byte[]{0x01};
        data=addBytes(data,deviceKey.getBytes());
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_MODIFY_DEVICE_KEY,data);
        Log.i(TAG, "getCRCSetTimestamp: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCClearDeviceKey(byte cKey ){

        byte[] data = new byte[]{0x02} ;

        byte[] command =getCommand(cKey,CommandType.KEY_LOCK_MODIFY_DEVICE_KEY,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCShutDown(byte cKey ){

        byte[] data = new byte[]{0x00,0x01} ;
        //0x13
        byte[] command =getCommand(cKey,CommandType.KEY_LOCK_CONFIGS,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCOpenRecord(byte cKey ){

        byte[] data = new byte[]{ 0x01} ;
        byte[] command =getCommand(cKey,CommandType.KEY_LOCK_OPEN_RECORD,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCCleartOpenRecord(byte cKey ){

        byte[] data = new byte[]{ 0x01} ;
        byte[] command =getCommand(cKey,CommandType.KEY_LOCK_CLEAR_OPEN_RECORD,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCInputPWD(byte cKey ,byte index,long password){

        byte[] data = new byte[]{index} ;
        data=addBytes(data,password );
        byte[] command =getCommand(cKey,CommandType.KEY_LOCK_INPUT_PWD,data);
        Log.i(TAG, "getCRCInputPWD: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseOpen(byte ckey ){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_OPEN,data);
        Log.i(TAG, "getCRCResponseOpen: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseLeft(byte ckey ){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_LEFT,data);
        Log.i(TAG, "getCRCResponseLeft: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseOpenRecord(byte ckey ,byte index){
        byte[] data=new byte[]{0x02,index};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_OPEN_RECORD,data);
        Log.i(TAG, "getCRCResponseOpenRecord: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseOpenRecord(byte ckey ,int index){
        byte[] data=new byte[]{0x02,(byte)((index>>8)&0xFF),(byte)(index&0xFF)};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_OPEN_RECORD,data);
        Log.i(TAG, "getCRCResponseOpenRecord: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseBack(byte ckey ){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_BACK,data);
        Log.i(TAG, "getCRCResponseBack: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCResponseClose(byte ckey ){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_CLOSE,data);
        Log.i(TAG, "getCRCResponseClose: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCPwdConfigs(byte ckey,byte maxLen,long validTime ){
        byte[] data=new byte[]{maxLen};
        data= addBytes(data,validTime);
        byte[] command =getCommand(ckey, CommandType.KEY_LOCK_PWD_CONFIGS,data);
        Log.i(TAG, "getCRCPwdConfigs: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCKeyLockInfo(byte ckey  ){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.KEY_LOCK_INFO,data);
        Log.i(TAG, "getCRCKeyLockInfo: "+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }

    private static byte[] getXorCRCCommand(byte[] command){
//        Log.i(TAG, "getXorCRCCommand: 原始"+PrintUtil.getHexString(command));
        byte[] xorCommand=encode(command);
//        Log.i(TAG, "getXorCRCCommand: 加0x32异或后："+PrintUtil.getHexString(xorCommand));
        byte[] crcOrder= CRCByte(xorCommand);
//        Log.i(TAG, "getXorCRCCommand: 加CRC后："+PrintUtil.getHexString(crcOrder));
        return  crcOrder;
    }

    private static byte[] encode(byte[] command){
        byte[] xorComm = new byte[command.length];
        xorComm[0]=command[0];
        xorComm[1]=command[1];
        xorComm[2]=command[2];
        xorComm[3] =(byte) (command[3] +0x32);
        for(int i=4;i<command.length;i++){
            xorComm[i]= (byte) (command[i] ^ command[3]);
        }
        return xorComm;
    }

    private static byte[] CRCByte(byte[] ori){
        byte[] ret = new byte[ori.length+1];
        int crc8 = com.omni.lib.utils.CRCUtil.calcCRC8(ori);
        for(int i=0;i<ori.length;i++) ret[i]=ori[i];
        ret[ori.length]= (byte) (crc8&0xFF);
        return ret;
    }
}
