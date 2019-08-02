package com.omni.ble.library.utils;





import android.util.Log;

import com.omni.ble.library.model.CommandType;
import com.omni.lib.utils.PrintUtil;

import java.io.PrintWriter;
import java.util.Random;

/**
 * <br />
 * created by CxiaoX at 2017/4/22 18:02.
 */

public class ScooterCommandUtil extends  BaseCommand{

    private static  final  String TAG=ScooterCommandUtil.class.getSimpleName();

    /**
     * 得到未经CRC检验的指令
     * @param ckey  通信KEY
     * @param commandType 指令类型
     * @param data 指令内容
     * @return byte数组
     */
    private static byte[] getCommand(byte ckey,byte commandType,byte[] data){
        byte[] head=new byte[]{(byte)0xA3,(byte)0xA4};
        byte len = (byte)data.length; // 数据的长度
        byte rand = (byte) (new Random().nextInt(255) & 0xff);
        byte[] command=addBytes(head,new byte[]{len,rand,ckey,commandType});
        return addBytes(command,data);

    }

    public static byte[] getCRCScooterOpen(byte ckey, int uid,long timestamp ){
        byte[] data=new byte[]{0x01};
        data=addBytes(data,uid);
        data=addBytes(data,timestamp);
        data=addBytes(data,new byte[]{0x0});
        byte[] command =getCommand(ckey, CommandType.CONTROL_DOWN,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCScooterOpen(byte ckey, byte mode,int uid,long timestamp ){
        byte[] data=new byte[]{mode};
        data=addBytes(data,uid);
        data=addBytes(data,timestamp);
        data=addBytes(data,new byte[]{0x0});
        byte[] command =getCommand(ckey,CommandType.CONTROL_DOWN,data);
        return  getXorCRCCommand(command);
    }


    public static byte[] getCRCScooterSet(byte ckey, byte light,byte speed,byte you,byte tLight){
        byte[] data=new byte[]{light,speed,you,tLight};

        byte[] command =getCommand(ckey,CommandType.SCOOTER_SET,data);
        return  getXorCRCCommand(command);
    }

    /**
     * 获取设备锁信息
     * @param ckey
     * @return
     */
    public static byte[] getCRCDeviceInfo(byte ckey  ){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.CARPORT_DEVICE_INFO,data);
        Log.i(TAG, "getCRCDeviceInfo: 原始指令="+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCScooterInfo(byte ckey  ){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.SCOOTER_INFO,data);
        Log.i(TAG, "getCRCScooterInfo: 原始指令="+ PrintUtil.toHexString(command));
        return  getXorCRCCommand(command);
    }


    public static byte[] getCRCShutDown(byte cKey,byte opt ){

        byte[] data = new byte[]{opt} ;

        byte[] command =getCommand(cKey,CommandType.SCOOTER_POWER_CONTROL,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCShutDown(byte cKey,byte orderType,byte opt ){

        byte[] data = new byte[]{opt} ;

        byte[] command =getCommand(cKey,orderType,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCScooterOpenResponse(byte ckey){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey,CommandType.CONTROL_DOWN,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCScooterClose(byte ckey ){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.SCOOTER_CLOSE,data);
        return  getXorCRCCommand(command);
    }


    public static byte[] getCRCScooterCloseResponse(byte ckey){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey,CommandType.CONTROL_UP,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCScooterPopUp(byte ckey){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.SCOOTER_POPUP,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCScooterExtraDevice(byte ckey,byte optType){
        byte[] data=new byte[]{optType};
        byte[] command =getCommand(ckey,CommandType.SCOOTER_POPUP,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCReadId(byte ckey){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.SCOOTER_READ_CARD_ID,data);
        return  getXorCRCCommand(command);
    }

    private static String getCommForHex(byte[] values){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i<values.length;i++){
            sb.append( String.format("%02X,",values[i]));
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    public static byte[] getCRCCommunicationKey(String deviceKey ){
        byte[] data = new byte[8] ;
        for(int i=0;i<deviceKey.length();i++){
            data[i]= (byte) deviceKey.charAt(i);
        }
        byte[] command =getCommand((byte)0,CommandType.COMMUNICATION_KEY,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCCommunicationKey(byte[] deviceKey ){
        byte[] data = new byte[8] ;
        for(int i=0;i<deviceKey.length;i++){
            data[i]= (byte) deviceKey[i];
        }
        byte[] command =getCommand((byte)0,CommandType.COMMUNICATION_KEY,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCGetOldData(byte cKey ){

        byte[] data = new byte[]{0x01} ;

        byte[] command =getCommand(cKey,CommandType.OLD_DATA,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCClearOldData(byte cKey ){

        byte[] data = new byte[]{0x01} ;

        byte[] command =getCommand(cKey,CommandType.CLEAR_DATA,data);
        return  getXorCRCCommand(command);
    }




}
