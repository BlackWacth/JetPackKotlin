package com.bruce.jetpackkotlin.db

import androidx.room.TypeConverter
import timber.log.Timber

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/
object GithubTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").mapNotNull {
                try {
                    it.toInt()
                } catch (e: Exception) {
                    Timber.e(e, "Cannot convert $it to number")
                    null
                }
            }
        }
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

}