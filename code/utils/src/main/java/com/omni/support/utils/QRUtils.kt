package com.omni.support.utils

import android.util.Log

import java.util.regex.Pattern

/**
 * @author 邱永恒
 * @time 2019/7/10 11:45
 * @desc 二维码解析
 */
object QRUtils {
    private const val TAG = "QRUtils"

    fun parseQRContent(content: String): String {
        var code = content
        when {
            isContainsBase64(content) -> code = code.substring(code.lastIndexOf("/") + 1)
            content.contains("https://hulaj.eu?c=") -> code = code.substring(code.lastIndexOf("=") + 1)
            content.contains("https://hulaj.eu?c") -> code = code.substring(code.lastIndexOf("c") + 1)
            else -> {
                code = code.substring(code.lastIndexOf("=") + 1)
                code = code.substring(code.lastIndexOf("/") + 1)
                code = code.substring(code.lastIndexOf("?") + 1)
            }
        }

        Log.d(TAG, "二维码内容: $content, code: $code")
        return code
    }

    /**
     * 判断字符串是否经过base64编码
     */
    private fun isBase64(str: String): Boolean {
        val base64Pattern =
            "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"
        return Pattern.matches(base64Pattern, str)
    }

    /**
     * 提取base64格式的code
     */
    private fun isContainsBase64(str: String): Boolean {
        return str.contains("==")
    }
}
