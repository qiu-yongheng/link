package com.omni.support.ble;

import com.omni.support.ble.protocol.base.abde.ABDEPack;
import com.omni.support.ble.utils.HexString;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    byte[] keyOrg = new byte[]{0x61, 0x66, 0x6B, 0x33, 0x74, 0x79, 0x73, 0x77, 0x34, 0x70, 0x73, 0x6B, 0x32, 0x36, 0x68, 0x6A};
    //出货BLOOM1
    byte[] inputKey = new byte[]{0x1, 0x2, 0x3, 0x4};

    @Test
    public void addition_isCorrect() {
//        byte[] keyMap = getKeyMap(inputKey, keyOrg);
//        ABDEPack abdePack = new ABDEPack();
//        byte[] keyMap = abdePack.getKeyMap(inputKey, keyOrg);
//        System.out.println(HexString.valueOf(keyMap));

    }

    public byte[] getKeyMap(byte[] inputKey, byte[] keyOrg) {
        byte[] keyMap = new byte[256];
        int index = 0;
        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < inputKey.length; i++) {
                for (int j = 0; j < keyOrg.length; j++) {
                    keyMap[index] = (byte) ((inputKey[i] + 0x30 + k) ^ keyOrg[j]);
                    index++;
                }
            }
        }
        return keyMap;
    }
}