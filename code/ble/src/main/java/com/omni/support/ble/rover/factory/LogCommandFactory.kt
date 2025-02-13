package com.omni.support.ble.rover.factory

import com.omni.support.ble.core.ICommand
import com.omni.support.ble.protocol.Command
import com.omni.support.ble.protocol.base.LogCommand
import com.omni.support.ble.rover.annotations.CommandID
import com.omni.support.ble.rover.annotations.NoResponse
import com.omni.support.ble.utils.TypeUtils
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * @author 邱永恒
 *
 * @time 2019/8/7 17:02
 *
 * @desc
 *
 */
class LogCommandFactory : ICommandFactory {
    override fun getCommand(method: Method, returnType: Type, data: ByteArray?): ICommand<*> {
        val command = LogCommand<Any>(returnType).apply {
            setData(data ?: ByteArray(0))
        }

        parseMethodAnnotations(method, command)
        return command
    }

    private fun parseMethodAnnotations(method: Method, command: Command<*>) {
        val sb = StringBuilder()

        val annotations = method.annotations
        TypeUtils.checkNotNull(annotations, "方法必须要有注解")

        for (annotation in annotations) {
            when (annotation) {
                is CommandID -> {
                    command.setCmd(annotation.value)
                    command.setResponseCmd(annotation.responseCmd)
                    sb.append("CommandID: ").append(annotation.value).append(", ")
                }
                is NoResponse -> {
                    command.setNoResponse(true)
                    sb.append("NoResponse: ").append("true")
                }
            }
        }

//        Cat.d(sb.toString())
    }
}