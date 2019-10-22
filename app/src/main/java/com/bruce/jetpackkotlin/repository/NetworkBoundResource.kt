package com.bruce.jetpackkotlin.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bruce.jetpackkotlin.AppExecutors
import com.bruce.jetpackkotlin.api.ApiEmptyResponse
import com.bruce.jetpackkotlin.api.ApiErrorResponse
import com.bruce.jetpackkotlin.api.ApiResponse
import com.bruce.jetpackkotlin.api.ApiSuccessResponse
import com.bruce.jetpackkotlin.vo.Resource

/**
 *
 * Copyright (c) 2019, 四川绿源集科技有限公司 All rights reserved.
 * author：HuaZhongWei
 * date：2019/10/22
 * description：
 *
 **/
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors){
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val dbSource = loadFormDb()

        result.addSource(dbSource) {data ->
            result.removeSource(dbSource)
            if(shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else{
                result.addSource(dbSource) {newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()

        result.addSource(dbSource) {newData ->
            setValue(Resource.loading(newData))
        }

        result.addSource(apiResponse) {response ->
            result.removeSource(apiResponse)
            result.removeSource((dbSource))

            when(response) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO().execute{
                        saveCallResult(processResponse(response))
                        appExecutors.mainThread().execute {
                            result.addSource(loadFormDb()) {newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }

                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        result.addSource(loadFormDb()) {newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }

                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(loadFormDb()) {newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }

        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>


    protected open fun onFetchFailed() {

    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFormDb(): LiveData<ResultType>
}