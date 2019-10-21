package com.bruce.jetpackkotlin.vo

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/
data class Resourece<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): Resourece<T> = Resourece(Status.SUCCESS, data, null)

        fun <T> error(msg: String, data: T?): Resourece<T> = Resourece(Status.ERROR, data, msg)

        fun <T> loading(data: T?): Resourece<T> = Resourece(Status.LOADING, data, null)
    }
}