package com.bruce.jetpackkotlin.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bruce.jetpackkotlin.db.GithubTypeConverters

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/21
 * description：
 *
 **/
@Entity
@TypeConverters(GithubTypeConverters::class)
data class RepoSearchResult (

    @PrimaryKey
    val query: String,

    val repoIds: List<Int>,

    val totalCount: Int,

    val next: Int?
)