package com.omni.support.ble;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.omni.support.ble.protocol.base.abde.ABDEPack;
import com.omni.support.ble.utils.HexString;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    byte[] inputKey = new byte[]{0x1, 0x2, 0x3, 0x4};

    @Test
    public void useAppContext() {
        // Context of the app under test.
        ABDEPack pack = new ABDEPack();
        pack.setCmd(1);
        pack.setPayload(inputKey);
        byte[] buffer = pack.getBuffer();
        System.out.println(HexString.valueOf(buffer));
        System.out.println("");
    }
}
