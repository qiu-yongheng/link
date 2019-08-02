package com.omni.ble.library.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.UUID;

public class UService extends OBL2Service {
    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    }

    @Override
    public UUID getNotifyUUID() {
        return UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    }

    @Override
    public UUID getWriteUUID() {
        return UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new UService.LocalBinder();

    public class LocalBinder extends Binder {
        public UService getService(){
            return UService.this;
        }
    }
}
