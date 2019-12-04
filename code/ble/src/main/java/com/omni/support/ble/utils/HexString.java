/*
 * Copyright (c) 2019. XAG All Rights Reserved
 */

package com.omni.support.ble.utils;

import android.text.TextUtils;

import java.util.Locale;

/**
 * 十六进制字符串工具类
 */
public class HexString {
    public static String valueOf(byte[] buffer) {
        return valueOf(buffer, " ");
    }

    public static String valueOf(byte[] buffer, String splitStr) {
        if (buffer == null) return "";
        return valueOf(buffer, 0, buffer.length, splitStr);
    }

    public static String valueOf(byte[] buffer, int offset, int length, String splitStr) {
        if (buffer == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append("0");
                sb.append(hex);
            } else {
                sb.append(hex);
            }
            if (i < buffer.length - 1)
                sb.append(splitStr);
        }
        return sb.toString();
    }

    public static String valueOf(int value, boolean with0x) {
        String str = Integer.toHexString(value);
        return with0x ? "0x" + str : str;
    }

    public static String valueOf(int value) {
        return valueOf(value, true);
    }

    public static String valueOf(long value, boolean with0x) {
        String str = Long.toHexString(value);
        return with0x ? "0x" + str : str;
    }

    public static String valueOf(long value) {
        return valueOf(value, true);
    }

    /**
     * 把BytesToHexString()得到的字符串还原成字节数组
     *
     * @param src      待转成byte[]的字符串
     * @param splitStr 分隔符
     * @return 还原的字节数组
     */
    public static byte[] toBytes(String src, String splitStr) {
        if (TextUtils.isEmpty(splitStr)) {
            return toBytes(src);
        }

        String[] arrays = src.split(splitStr);
        byte[]   bytes  = new byte[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            bytes[i] = (byte) Integer.parseInt(arrays[i], 16);
        }
        return bytes;
    }

    /**
     * 将16进制字符串转换成byte[]
     *
     * @param hexString 需要转换的16进制字符串，形如"0B113A07"
     * @return 返回转化之后的byte[]
     */
    public static byte[] toBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase();

        int    length   = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] by       = new byte[length];

        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            by[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }

        return by;
    }

    /**
     * 将16进制字符串转换成byte[]
     *
     * @param hexString 需要转换的16进制字符串
     * @return 返回转化之后的byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase(Locale.getDefault());

        int    length   = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] by       = new byte[length];

        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            by[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }

        return by;
    }

    public static String paddingValueOf(long value, int byteLength, boolean with0x) {
        String str = valueOf(value, false);
        if (str.length() < byteLength * 2) {
            int          add = (byteLength * 2 - str.length());
            StringBuffer sb  = new StringBuffer();

            if (with0x)
                sb.append("0x");

            for (int i = 0; i < add; i++) {
                sb.append("0");
            }

            sb.append(str);
            return sb.toString();
        } else if (str.length() % 2 == 1) {
            return with0x ? "0x0" + str : "0" + str;
        } else {
            return with0x ? "0x" + str : str;
        }
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
