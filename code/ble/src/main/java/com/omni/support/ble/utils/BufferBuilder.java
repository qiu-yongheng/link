package com.omni.support.ble.utils;

import java.util.ArrayList;
import java.util.List;

public class BufferBuilder {
    private List<Byte> mBuffer;

    public BufferBuilder() {
        this.mBuffer = new ArrayList<>();
    }

    public BufferBuilder putS8(int value) {
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putU8(int value) {
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putS16(int value) {
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putU16(int value) {
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putS32(int value) {
        mBuffer.add((byte) (value >> 24));
        mBuffer.add((byte) (value >> 16));
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putU32(long value) {
        mBuffer.add((byte) (value >> 24));
        mBuffer.add((byte) (value >> 16));
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putU64(long value) {
        mBuffer.add((byte) (value >> 56));
        mBuffer.add((byte) (value >> 48));
        mBuffer.add((byte) (value >> 40));
        mBuffer.add((byte) (value >> 32));
        mBuffer.add((byte) (value >> 24));
        mBuffer.add((byte) (value >> 16));
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putS64(long value) {
        mBuffer.add((byte) (value >> 56));
        mBuffer.add((byte) (value >> 48));
        mBuffer.add((byte) (value >> 40));
        mBuffer.add((byte) (value >> 32));
        mBuffer.add((byte) (value >> 24));
        mBuffer.add((byte) (value >> 16));
        mBuffer.add((byte) (value >> 8));
        mBuffer.add((byte) value);
        return this;
    }

    public BufferBuilder putDouble(double value) {
        long bits = Double.doubleToLongBits(value);
        putU64(bits);
        return this;
    }

    public BufferBuilder putFloat(float value) {
        int bits = Float.floatToIntBits(value);
        putU32(bits);
        return this;
    }

    public BufferBuilder putBytes(byte[] bytes) {
        if (bytes == null)
            return this;

        for (int i = 0; i < bytes.length; i++) {
            mBuffer.add(bytes[i]);
        }
        return this;
    }

    public BufferBuilder putString(String value) {
        putBytes(value.getBytes());
        return this;
    }


    /**
     * 从当前位置偏移offset个字节
     *
     * @param offset
     */
    public BufferBuilder offset(int offset) {
        for (int i = 0; i < offset; i++) {
            mBuffer.add((byte) 0);
        }
        return this;
    }

    /**
     * 返回Converter里的buffer
     *
     * @return
     */
    public byte[] buffer() {
        byte[] bytes = new byte[mBuffer.size()];
        for (int i = 0; i < mBuffer.size(); i++) {
            bytes[i] = mBuffer.get(i);
        }
        return bytes;
    }

    public int size() {
        return mBuffer.size();
    }
}
