package com.bruce.jetpackkotlin.db

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bruce.jetpackkotlin.vo.Contributor
import com.bruce.jetpackkotlin.vo.Repo
import com.bruce.jetpackkotlin.vo.RepoSearchResult
import java.util.*

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
@Dao
abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repo: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepos(repositories: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createRepoIfNotExists(repo: Repo): Long

    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin AND name = :name")
    abstract fun load(ownerLogin: String, name: String): LiveData<Repo>

    @Query(
        """
        SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor
        WHERE repoOwner = :owner AND repoName = :name
        ORDER BY contributions DESC
    """
    )
    abstract fun loadContributors(owner: String, name: String): LiveData<List<Contributor>>

    @Query(
        """
            SELECT * FROM repo
            WHERE owner_login = :owner
            ORDER BY stars DESC
        """
    )
    abstract fun loadRepositories(owner: String): LiveData<List<Repo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: RepoSearchResult)

    @Query("SELECT * FROM reposearchresult WHERE `query` = :query")
    abstract fun search(query: String): LiveData<RepoSearchResult>

    @Query("SELECT * FROM repo WHERE id in (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): LiveData<List<Repo>>

    @Query("SELECT * FROM reposearchresult WHERE `query` = :query")
    abstract fun findSearchResult(query: String): RepoSearchResult?

    fun loadOrdered(repoIds: List<Int>): LiveData<List<Repo>> {
        val order = SparseIntArray()
        repoIds.withIndex()
            .forEach {
                order.put(it.value, it.index)
            }

        return Transformations.map(loadById(repoIds)) {
            Collections.sort(it) {r1, r2 ->
                val pos1 = order.get(r1.id)
                val pos2 = order.get(r2.id)
                pos1 - pos2
            }
            it
        }
    }
}