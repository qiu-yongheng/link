package com.omni.support;

import android.app.Application;
import com.omni.support.ble.BleModuleHelper;

/**
 * @author 邱永恒
 * @time 2019/8/7 17:57
 * @desc
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BleModuleHelper.INSTANCE.init(this);
    }
}
