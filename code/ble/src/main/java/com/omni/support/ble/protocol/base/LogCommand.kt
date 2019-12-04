package com.omni.support.ble.protocol.base

import com.omni.support.ble.protocol.Command
import java.lang.reflect.Type

/**
 * @author 邱永恒
 *
 * @time 2019/8/28 11:49
 *
 * @desc
 *
 */
class LogCommand<RESULT>(private val returnType: Type) : Command<RESULT>(returnType)