/*
 * Copyright (c) 2018. XAG All Rights Reserved
 */

package com.omni.support.ble.core

interface IResp<T> {

//    fun isSuccessful(): Boolean

//    fun getPacks(): List<IPack>

    fun getResult(): T?
}