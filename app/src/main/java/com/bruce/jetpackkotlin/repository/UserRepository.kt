package com.bruce.jetpackkotlin.repository

import androidx.lifecycle.LiveData
import com.bruce.jetpackkotlin.AppExecutors
import com.bruce.jetpackkotlin.api.GithubService
import com.bruce.jetpackkotlin.db.UserDao
import com.bruce.jetpackkotlin.vo.Resource
import com.bruce.jetpackkotlin.vo.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/

@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
) {

    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) = userDao.insert(item)

            override fun createCall() = githubService.getUser(login)

            override fun shouldFetch(data: User?) = data == null

            override fun loadFormDb(): LiveData<User> = userDao.findByLogin(login)

        }.asLiveData()
    }

}