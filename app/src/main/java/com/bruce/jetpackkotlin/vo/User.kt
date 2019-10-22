package com.bruce.jetpackkotlin.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/

@Entity
data class User(

    @PrimaryKey
    @SerializedName("login")
    val login: String,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("company")
    val company: String?,

    @SerializedName("repos_url")
    val reposUrl: String?,

    @SerializedName("blog")
    val blog: String?
)