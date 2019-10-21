package com.bruce.jetpackkotlin.vo

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["repoName", "repoOwner", "login"],
    foreignKeys = [
        ForeignKey(
            entity = Repo::class,
            parentColumns = ["name", "owner_login"],
            childColumns = ["repoName", "repoOwner"],
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ]
)
data class Contributor(
    @SerializedName("login")
    val login: String,

    @SerializedName("contributions")
    val contributions: Int,

    @SerializedName("avatar_url")
    val avatarUrl: String?
) {

    lateinit var repoName: String

    lateinit var repoOwner: String
}