package com.omni.support.ble.protocol

import android.util.Log
import com.omni.support.ble.core.ICommand
import com.omni.support.ble.core.BufferDeserializable
import com.omni.support.ble.utils.HexString
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 16:45
 *
 * @desc
 *
 */
open class Command<RESULT>(private val returnType: Type) : ICommand<RESULT> {
    private var cmd: Int = 0
    private var responseCmd: Int = 0
    private var isNoResponse: Boolean = false
    private var data: ByteArray = ByteArray(0)

    override fun getCmd(): Int {
        return cmd
    }

    override fun setCmd(cmd: Int) {
        this.cmd = cmd
    }

    override fun getResponseCmd(): Int {
        return responseCmd
    }

    override fun setResponseCmd(cmd: Int) {
        this.responseCmd = cmd
    }

    override fun setNoResponse(isNoResponse: Boolean) {
        this.isNoResponse = isNoResponse
    }

    override fun isNoResponse(): Boolean {
        return isNoResponse
    }

    override fun getData(): ByteArray {
        return data
    }

    override fun setData(data: ByteArray) {
        this.data = data
    }

    override fun onResult(buffer: ByteArray?): RESULT? {
        if (returnType is Class<*>) {
            val resultClass = returnType

            // 处理Boolean类型结果
            if (resultClass == Boolean::class.java || resultClass == java.lang.Boolean::class.java) {
                return if (buffer == null || buffer.isEmpty()) {
                    false as RESULT
                } else {
                    (buffer[0].toInt() == 0x00) as RESULT
                }
            }

            if (resultClass == String::class.java) {
                return if (buffer == null || buffer.isEmpty()) {
                    "" as RESULT
                } else {
                    String(buffer) as RESULT
                }
            }

            if (resultClass == ByteArray::class.java) {
                return buffer as RESULT
            }

            // 处理Result类型结果
            val genericInterfaces = resultClass.genericInterfaces

            if (genericInterfaces.contains(BufferDeserializable::class.java)) {
                try {
                    val result = resultClass.newInstance() as BufferDeserializable
                    result.setBuffer(buffer ?: ByteArray(0))
                    return result as RESULT
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        } else if (returnType is ParameterizedType) {
            Log.w("debug", returnType.toString())
        }
        return null
    }

    override fun toString(): String {
        return "Command(returnType=$returnType, cmd=${HexString.valueOf(cmd)}, data=${HexString.valueOf(data)})"
    }
}