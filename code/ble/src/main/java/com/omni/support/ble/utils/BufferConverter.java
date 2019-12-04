/*
 * Copyright (c) 2017. XAIRCRAFT UAV Solutions.
 * All rights reserved.
 */

package com.omni.support.ble.utils;

/**
 * @author 邱永恒
 * @time 2018/5/15  14:12
 * @desc
 */
public class BufferConverter {
    private int mPosition;
    private byte[] mBuffer;

    public BufferConverter(int capacity) {
        this.mBuffer = new byte[capacity];
        this.mPosition = 0;
    }

    public BufferConverter(byte[] buffer) {
        this.mBuffer = buffer;
        this.mPosition = 0;
    }

    public BufferConverter putS8(int value) {
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putU8(int value) {
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putS16(int value) {
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putU16(int value) {
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putS32(int value) {
        mBuffer[mPosition++] = (byte) (value >> 24);
        mBuffer[mPosition++] = (byte) (value >> 16);
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putU32(long value) {
        mBuffer[mPosition++] = (byte) (value >> 24);
        mBuffer[mPosition++] = (byte) (value >> 16);
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putU64(long value) {
        mBuffer[mPosition++] = (byte) (value >> 56);
        mBuffer[mPosition++] = (byte) (value >> 48);
        mBuffer[mPosition++] = (byte) (value >> 40);
        mBuffer[mPosition++] = (byte) (value >> 32);
        mBuffer[mPosition++] = (byte) (value >> 24);
        mBuffer[mPosition++] = (byte) (value >> 16);
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putS64(long value) {
        mBuffer[mPosition++] = (byte) (value >> 56);
        mBuffer[mPosition++] = (byte) (value >> 48);
        mBuffer[mPosition++] = (byte) (value >> 40);
        mBuffer[mPosition++] = (byte) (value >> 32);
        mBuffer[mPosition++] = (byte) (value >> 24);
        mBuffer[mPosition++] = (byte) (value >> 16);
        mBuffer[mPosition++] = (byte) (value >> 8);
        mBuffer[mPosition++] = (byte) value;
        return this;
    }

    public BufferConverter putDouble(double value) {
        long bits = Double.doubleToLongBits(value);
        putU64(bits);
        return this;
    }

    public BufferConverter putFloat(float value) {
        int bits = Float.floatToIntBits(value);
        putU32(bits);
        return this;
    }

    public BufferConverter putBytes(byte[] bytes) {
        if (bytes == null)
            return this;

        for (byte aByte : bytes) {
            mBuffer[mPosition++] = aByte;
        }
        return this;
    }

    public BufferConverter putString(String value) {
        putBytes(value.getBytes());
        return this;
    }

    public byte getS8() {
        return (byte) (mBuffer[mPosition++] & 0xff);
    }

    public int getU8() {
        return (short) ((short) mBuffer[mPosition++] & 0xff);
    }

    public short getS16() {
        short value;
        value = (short) (((short) mBuffer[mPosition++] & 0xff) << 8);
        value |= ((short) mBuffer[mPosition++] & 0x00ff);
        return value;
    }

    public int getU16() {
        int value;
        value = ((int) mBuffer[mPosition++] & 0xff) << 8;
        value |= ((int) mBuffer[mPosition++] & 0xff);
        return value;
    }

    public int getS32() {
        int value;

        value = ((int) mBuffer[mPosition++] & 0xff) << 24;
        value |= ((int) mBuffer[mPosition++] & 0xff) << 16;
        value |= ((int) mBuffer[mPosition++] & 0xff) << 8;
        value |= (int) mBuffer[mPosition++] & 0xff;

        return value;
    }

    public long getU32() {
        long value;

        value = ((long) mBuffer[mPosition++] & 0xff) << 24;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 16;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 8;
        value |= (long) mBuffer[mPosition++] & 0xff;

        return value;
    }

    public long getS64() {
        long value;

        value = ((long) mBuffer[mPosition++] & 0xff) << 56;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 48;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 40;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 32;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 24;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 16;
        value |= ((long) mBuffer[mPosition++] & 0xff) << 8;
        value |= (long) mBuffer[mPosition++] & 0xff;

        return value;
    }

    public double getDouble() {
        return Double.longBitsToDouble(getS64());
    }

    public float getFloat() {
        return Float.intBitsToFloat(getS32());
    }

    public byte[] getBytes(int length) {
        byte[] temp = new byte[length];
        System.arraycopy(mBuffer, mPosition, temp, 0, length);
        mPosition += length;
        return temp;
    }

    /**
     * 设置位置
     *
     * @param newPosition 新位置
     */
    public void position(int newPosition) {
        mPosition = newPosition;
    }

    /**
     * 获取位置
     *
     * @return
     */
    public int position() {
        return mPosition;
    }

    /**
     * 从当前位置偏移offset个字节
     *
     * @param offset
     */
    public BufferConverter offset(int offset) {
        mPosition += offset;
        return this;
    }

    /**
     * 返回Converter里的buffer
     *
     * @return
     */
    public byte[] buffer() {
        return mBuffer;
    }

    public static byte[] copy(byte[] source, int offset, int length) {
        byte[] buffer = new byte[length];
        System.arraycopy(source, offset, buffer, 0, length);
        return buffer;
    }
}
