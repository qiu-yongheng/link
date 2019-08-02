package com.omni.ble.library.utils;

import com.omni.ble.library.model.CommandType;

public class MifiCommand extends BoxLockCommand {

    public static byte[] getCrcMifiClsoe(byte ckey, int uid,long timestamp ){
        byte[] data=new byte[]{0x01};
        data=addBytes(data,uid);
        data=addBytes(data,timestamp);
        byte[] command =getCommand(ckey, CommandType.MIFI_LOCK_CLOSE,data);
        return  getXorCRCCommand(command);
    }
}
