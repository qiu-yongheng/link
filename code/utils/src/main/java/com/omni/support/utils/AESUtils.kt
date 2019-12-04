package com.omni.support.utils

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.EncryptUtils

/**
 * @author 邱永恒
 *
 * @time 2019/9/4 15:21
 *
 * @desc AES加解密工具类
 *
 */
object AESUtils {
    private const val TAG = "AESUtils"

    /**
     * 加密
     */
    fun encrypt(content: String, iv: String, key: String): String {
        if (TextUtils.isEmpty(content)) {
            return ""
        } else {
            val aeS2Base64 = EncryptUtils.encryptAES2Base64(
                content.toByteArray(), key.toByteArray(),
                "AES/CBC/PKCS5Padding", iv.toByteArray()
            ) ?: return ""

            Log.d(TAG, String(aeS2Base64))
            return String(aeS2Base64)
        }
    }

    /**
     * 解密
     */
    fun decrypt(content: String?, iv: String, aesKey: String): String {
        if (content == null || TextUtils.isEmpty(content)) {
            return ""
        } else {
            val bytes = EncryptUtils.decryptBase64AES(
                content.toByteArray(),
                aesKey.toByteArray(),
                "AES/CBC/PKCS5Padding",
                iv.toByteArray()
            ) ?: return ""

            Log.d(TAG, String(bytes))
            return String(bytes)
        }
    }
}