package com.bruce.jetpackkotlin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bruce.jetpackkotlin.vo.Contributor
import com.bruce.jetpackkotlin.vo.Repo
import com.bruce.jetpackkotlin.vo.RepoSearchResult
import com.bruce.jetpackkotlin.vo.User

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GithubDb: RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao

}