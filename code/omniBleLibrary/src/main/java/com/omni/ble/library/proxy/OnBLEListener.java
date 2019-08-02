package com.omni.ble.library.proxy;

/**
 * @author 邱永恒
 * @time 2019/6/27 14:59
 * @desc
 */
public interface OnBLEListener {
    void onLockOpen(String number, int status, long unlockTime);
    void onLockClose(int status, int runtime, long timestamp);
    void onBLEWriteNotify();
    void onGetKey();
    void onGetLockStatus(int power, boolean hasOld, long timestamp, boolean isUnlock);
    void onGetOldData(int uid, int runtime, long timestamp);
    void onGetDataTimeout();

    void onDisconnect();

    void stopScan();

    void scanTimeout();

    void onGetFWInfoSuccess(String firmwareData);

    void onGetFWInfoPack(int nPack, int totalPack);

    void onFwUpgradeTransmission(int currentPack, int deviceType);

    void onLockInfoModify(int pack, int deviceType);

    void serviceNoFind();
}
