package com.bruce.jetpackkotlin.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/

@Entity(
    indices = [
        Index("id"),
        Index("owner_login")
    ],
    primaryKeys = ["name", "owner_login"]
)
data class Repo(

    val id: Int,
    @SerializedName("name")
    val name: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    val owner: Owner,

    @SerializedName("stargazers_count")
    val stars: Int
) {
    data class Owner(
        @SerializedName("login")
        val login: String,
        @SerializedName("url")
        val url: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }
}
