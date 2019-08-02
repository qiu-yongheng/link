package com.omni.ble.library.utils;





import com.omni.ble.library.model.CommandType;

import java.util.Random;

/**
 * <br />
 * created by CxiaoX at 2017/4/22 18:02.
 */

public class BoxLockCommand extends BaseCommand{

    private static  final  String TAG="BoxLockCommand";

    protected static byte[] getCommand(byte ckey,byte commandType,byte[] data){
        byte[] head=new byte[]{(byte)0xA3,(byte)0xA4};
        byte len = (byte)data.length; // 数据的长度
        byte rand = (byte) (new Random().nextInt(255) & 0xff);
        byte[] command=addBytes(head,new byte[]{len,rand,ckey,commandType});
        return addBytes(command,data);

    }




    public static byte[] getCRCOpen(byte ckey, int uid,long timestamp ){
        byte[] data=new byte[]{0x01};
        data=addBytes(data,uid);
        data=addBytes(data,timestamp);
        data=addBytes(data,new byte[]{0x0});
        byte[] command =getCommand(ckey, CommandType.BOX_LOCK_OPEN,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCLowOpen(byte ckey, byte status ){
        byte[] data=new byte[]{0x01,status};

        byte[] command =getCommand(ckey, CommandType.BOX_LOCK_LOW_OPEN,data);
        return  getXorCRCCommand(command);
    }


    public static byte[] getCRCDeviceInfo(byte ckey  ){
        byte[] data=new byte[]{0x01};
        byte[] command =getCommand(ckey,CommandType.BOX_LOCK_DEVICE_INFO,data);
        return  getXorCRCCommand(command);
    }






    public static byte[] getCRCCarportDownResponse(byte ckey){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey,CommandType.CONTROL_DOWN,data);
        return  getXorCRCCommand(command);
    }




    public static byte[] getCRCCarportUpResponse(byte ckey){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey,CommandType.CONTROL_UP,data);
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


    public static byte[] getCRCModifyDeviceKey(byte cKey,String deviceKey ){

        byte[] data = new byte[9] ;
        data[0]=1;
        for(int i=0;i<deviceKey.length();i++){
            data[i+1]= (byte) deviceKey.charAt(i);
        }
        byte[] command =getCommand(cKey,CommandType.MODIFY_DEVICE_KEY,data);
        return  getXorCRCCommand(command);
    }

    public static byte[] getCRCClearDeviceKey(byte cKey ){

        byte[] data = new byte[]{0x02} ;

        byte[] command =getCommand(cKey,CommandType.MODIFY_DEVICE_KEY,data);
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
    public static byte[] getCRCGetPairInfo(byte cKey ){

        byte[] data = new byte[]{0x01} ;

        byte[] command =getCommand(cKey,CommandType.DEVICE_MAC_HAND_PAIR,data);
        return  getXorCRCCommand(command);
    }





    public static byte[] getCRCModelCommand(byte ckey,byte model){
        byte[] data=new byte[]{0x01,model};
        byte[] command =getCommand(ckey,CommandType.DEVICE_MODEL,data);
        return  getXorCRCCommand(command);
    }
    public static byte[] getCRCModelResponse(byte ckey){
        byte[] data=new byte[]{0x02};
        byte[] command =getCommand(ckey,CommandType.DEVICE_MODEL,data);
        return  getXorCRCCommand(command);
    }







}
