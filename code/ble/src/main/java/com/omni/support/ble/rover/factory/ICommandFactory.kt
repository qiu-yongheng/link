package com.omni.support.ble.rover.factory

import com.omni.support.ble.core.ICommand
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * @author 邱永恒
 *
 * @time 2018/12/29  09:47
 *
 * @desc
 *
 */

interface ICommandFactory {
    fun getCommand(method: Method, returnType: Type, data: ByteArray?): ICommand<*>
}