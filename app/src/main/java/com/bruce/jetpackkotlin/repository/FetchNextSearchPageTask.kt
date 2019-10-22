package com.bruce.jetpackkotlin.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bruce.jetpackkotlin.api.*
import com.bruce.jetpackkotlin.db.GithubDb
import com.bruce.jetpackkotlin.vo.RepoSearchResult
import com.bruce.jetpackkotlin.vo.Resource
import java.io.IOException

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService,
    private val db: GithubDb
) : Runnable {

    private val _liveData = MutableLiveData<Resource<Boolean>>()

    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val current = db.repoDao().findSearchResult(query)

        if (current == null) {
            _liveData.postValue(null)
            return
        }

        val nextPage = current.next
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }

        val newValue = try {
            val reponse = githubService.searchRepos(query, nextPage).execute()
            val apiResponse = ApiResponse.create(reponse)
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.repoIds)

                    ids.addAll(apiResponse.body.items.map { it.id })

                    val merged =
                        RepoSearchResult(query, ids, apiResponse.body.total, apiResponse.nextPage)

                    db.runInTransaction {
                        db.repoDao().insert(merged)
                        db.repoDao().insertRepos(apiResponse.body.items)
                    }
                    Resource.success(apiResponse.nextPage != null)
                }

                is ApiEmptyResponse -> Resource.success(false)
                is ApiErrorResponse -> Resource.error(apiResponse.errorMessage, true)
            }
        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}