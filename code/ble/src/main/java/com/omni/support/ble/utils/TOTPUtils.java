package com.omni.support.ble.utils;

import java.math.BigInteger;

/**
 * Time-based One-Time Password的简写，表示基于时间戳算法的一次性密码
 * Created by lenovo on 2018/3/10.
 */

public class TOTPUtils {
    private static final String PUBLIC_KEY="N6xA^^FIa1xi@!FCmpasYfVD0wtSNlwP";
    private static final int[] DIGITS_POWER
            // 0 1  2   3    4     5      6       7        8
            = {1,10,100,1000,10000,100000,1000000,10000000,100000000 };

    /**
    * @param validSecond 有效秒数 <br />
    * 如果有效时间是1小时(3600s),则在当天15:00:00-15:59:59 这段时间内是会生成同一个密码<br />
    * 如果有效时间是5分钟(300s), 则在当天15:00:00-15:4:59 这段时间内是会生成同一个密码<br />
    * @param codeDigits 获取密码位数
	* @return 开锁密码
	*/
    public static String getOpenKey(long timestamp,int validSecond,int codeDigits){
        long T = timestamp/validSecond;
        String result = getOpenKey(T,codeDigits);
        return result;
    }


    /**
     * 按照时间戳生成密码
     * @param timestamp 时间戳
     * @param codeDigits 密码位数
     * @return
     */
    public static String getOpenKey(long timestamp,int codeDigits){
        String result = null;
        byte[] pubKeyBytes=PUBLIC_KEY.getBytes();

        // 整数转四个字节的数组
        String hexStrTimestamp = Long.toHexString(timestamp);
        byte[] times= hexStr2Bytes(hexStrTimestamp);
        // 时间戳与公钥进行异或运算
        for(int i=0;i<pubKeyBytes.length;i++){
            for(int j=0;j<times.length;j++){
                pubKeyBytes[i]=(byte) (pubKeyBytes[i] ^ times[j]);

            }
        }
        // 一个小于16的偏移量。
        int offset = pubKeyBytes[pubKeyBytes.length - 1] & 0xf;
        // 根据偏移量获取到一个密码值。
        int binary =
                ((pubKeyBytes[offset] & 0x7f) << 24) |
                        ((pubKeyBytes[offset + 1] & 0xff) << 16) |
                        ((pubKeyBytes[offset + 2] & 0xff) << 8) |
                        (pubKeyBytes[offset + 3] & 0xff);
        //取指定个数密码
        int otp = binary%DIGITS_POWER[codeDigits];
        //指定个数密码，前面为0的补0。
        result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }
        return result;
    }

    private static byte[] hexStr2Bytes(String hex){
        // Adding one byte to get the right conversion
        // values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();
        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length ; i++){
            ret[i] = bArray[i+1];
            System.out.println(String.format("%X", ret[i]));
        }
        return ret;
    }

    private static String toHexStr(byte[] data){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for(int i=0;i<data.length;i++){
            sb.append(String.format("%02X,", data[i]));
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
}
