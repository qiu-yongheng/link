package com.omni.support.utils.permission;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.omni.support.utils.R;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2018/3/14  17:28
 * @desc 权限帮助类
 */
public class PermissionHelper {
    /**
     * 存储权限和定位权限(在MainActivity申请)
     *
     * @param listener
     */
    public static void requestStorageAndLocationAndPhone(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.STORAGE, PermissionConstants.PHONE, PermissionConstants.LOCATION);
    }

    /**
     * 存储权限和拍照权限(在PointTagActivity申请)
     *
     * @param listener
     */
    public static void requestStorageAndCamera(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.STORAGE, PermissionConstants.CAMERA);
    }

    /**
     * 存储权限
     *
     * @param listener 同意监听
     */
    public static void requestStorage(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.STORAGE);
    }

    /**
     * 打电话权限
     *
     * @param listener 同意监听
     */
    public static void requestPhone(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.PHONE);
    }

    /**
     * 打电话权限
     *
     * @param grantedListener 同意监听
     * @param deniedListener  拒绝监听
     */
    public static void requestPhone(final OnPermissionGrantedListener grantedListener,
                                    final OnPermissionDeniedListener deniedListener) {
        request(grantedListener, deniedListener, PermissionConstants.PHONE);
    }

    /**
     * 发短信权限
     *
     * @param listener 同意监听
     */
    public static void requestSms(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.SMS);
    }

    /**
     * 定位权限
     *
     * @param listener 同意监听
     */
    public static void requestLocation(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.LOCATION);
    }

    /**
     * 拍照权限
     *
     * @param listener
     */
    public static void requestCamera(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.CAMERA);
    }

    /**
     * 录音权限
     *
     * @param listener
     */
    public static void requestAudio(final OnPermissionGrantedListener listener) {
        request(listener, PermissionConstants.MICROPHONE);
    }

    /**
     * 请求权限
     *
     * @param grantedListener 同意监听
     * @param permissions     需要申请的权限
     */
    public static void request(final OnPermissionGrantedListener grantedListener,
                               final @PermissionConstants.Permission String... permissions) {
        request(grantedListener, null, permissions);
    }

    public static void request(final OnPermissionGrantedListener grantedListener,
                               final OnPermissionDeniedListener deniedListener,
                               final @PermissionConstants.Permission String... permissions) {

        PermissionUtils.permission(permissions)
                .rationale(PermissionHelper::showRationaleDialog)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        if (grantedListener != null) {
                            grantedListener.onPermissionGranted();
                        }
                        LogUtils.d(permissionsGranted);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            showOpenAppSettingDialog();
                        } else {
                            if (deniedListener != null) {
                                deniedListener.onPermissionDenied();
                            }
                        }

                        LogUtils.d(permissionsDeniedForever, permissionsDenied);
                    }
                })
                .request();
    }

    public interface OnPermissionGrantedListener {
        /**
         * 权限同意
         */
        void onPermissionGranted();
    }

    public interface OnPermissionDeniedListener {
        /**
         * 权限拒绝
         */
        void onPermissionDenied();
    }

    /**
     * 当权限被拒绝时, 弹窗
     *
     * @param shouldRequest
     */
    private static void showRationaleDialog(final PermissionUtils.OnRationaleListener.ShouldRequest shouldRequest) {
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null) {
            return;
        }
        new AlertDialog.Builder(topActivity)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(topActivity.getString(R.string.utils_hint_denied_permission))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> shouldRequest.again(true))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> shouldRequest.again(false))
                .setCancelable(false)
                .create()
                .show();

    }

    /**
     * 当权限被永久拒绝时, 提示到设置界面授权
     */
    private static void showOpenAppSettingDialog() {
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity == null) return;
        new AlertDialog.Builder(topActivity)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(topActivity.getString(R.string.utils_hint_launch_setting))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // 打开设置界面
                    PermissionUtils.launchAppDetailsSettings();
                    topActivity.finish();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    topActivity.finish();
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
