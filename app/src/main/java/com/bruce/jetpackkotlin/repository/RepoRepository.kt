package com.bruce.jetpackkotlin.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bruce.jetpackkotlin.AppExecutors
import com.bruce.jetpackkotlin.api.ApiResponse
import com.bruce.jetpackkotlin.api.ApiSuccessResponse
import com.bruce.jetpackkotlin.api.GithubService
import com.bruce.jetpackkotlin.api.RepoSearchResponse
import com.bruce.jetpackkotlin.db.GithubDb
import com.bruce.jetpackkotlin.db.RepoDao
import com.bruce.jetpackkotlin.util.AbsentLiveData
import com.bruce.jetpackkotlin.util.RateLimiter
import com.bruce.jetpackkotlin.vo.Contributor
import com.bruce.jetpackkotlin.vo.Repo
import com.bruce.jetpackkotlin.vo.RepoSearchResult
import com.bruce.jetpackkotlin.vo.Resource
import java.util.concurrent.TimeUnit
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
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GithubDb,
    private val repoDao: RepoDao,
    private val githubService: GithubService
) {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

            override fun createCall(): LiveData<ApiResponse<List<Repo>>> {
                return githubService.getRepos(owner)
            }

            override fun shouldFetch(data: List<Repo>?) =
                data.isNullOrEmpty() || repoListRateLimit.shouldFetch(owner)

            override fun loadFormDb(): LiveData<List<Repo>> {
                return repoDao.loadRepositories(owner)
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>(appExecutors) {
            override fun saveCallResult(item: Repo) {
                repoDao.insert(item)
            }

            override fun createCall(): LiveData<ApiResponse<Repo>> {
                return githubService.getRepo(owner, name)
            }

            override fun shouldFetch(data: Repo?) = data == null

            override fun loadFormDb(): LiveData<Repo> {
                return repoDao.load(owner, name)
            }

        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            override fun saveCallResult(item: List<Contributor>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.runInTransaction {
                    repoDao.createRepoIfNotExists(
                        Repo(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
                        )
                    )
                    repoDao.insertContributors(item)
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<Contributor>>> {
                return githubService.getContributors(owner, name)
            }

            override fun shouldFetch(data: List<Contributor>?) = data.isNullOrEmpty()

            override fun loadFormDb(): LiveData<List<Contributor>> =
                repoDao.loadContributors(owner, name)

        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            githubService = githubService,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(query: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            override fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(
                    query = query,
                    repoIds = repoIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                }
            }

            override fun createCall() = githubService.searchRepos(query)

            override fun shouldFetch(data: List<Repo>?) = data == null

            override fun loadFormDb(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(searchData.repoIds)
                    }
                }
            }

            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>): RepoSearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }
}