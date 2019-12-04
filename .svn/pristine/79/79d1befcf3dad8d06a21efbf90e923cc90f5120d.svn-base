package com.omni.support.ble.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author 邱永恒
 *
 * @time 2019/9/5 18:39
 *
 * @desc
 *
 */
object AESUtils {
    /**
     * 加密
     */
    fun encrypt(src: ByteArray, key: ByteArray): ByteArray {
        return try {
            val keySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            cipher.doFinal(src)
        } catch (ex: Exception) {
            ByteArray(0)
        }
    }

    /**
     * 解密
     */
    fun decrypt(src: ByteArray, key: ByteArray): ByteArray {
        return try {
            val keySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            cipher.doFinal(src)
        } catch (ex: Exception) {
            ByteArray(0)
        }

    }
}