package com.app.stority.remoteUtils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.app.stority.helper.AppExecutors

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(
    val appExecutors: AppExecutors
) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        val dbSource = loadFromDb()
        result.addSource(dbSource) { resultType ->
            result.removeSource(dbSource)
            if (shouldFetch(resultType)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { rT ->
                    result.value = Resource.success(rT)
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
        //TODO comment Logger.d(Thread.currentThread(), "Call: $apiResponse")
        result.addSource(dbSource) { resultType ->
            setValue(Resource.loading(resultType))
        }

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
//            Logger.e(Thread.currentThread(), "Response ${Gson().toJson(response)}")//todo remove gson

            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.diskIO().execute {
                        processResponse(response)?.let { saveCallResult(it) }
                        appExecutors.mainThread().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData, response.code))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    @WorkerThread
    private fun processResponse(response: ApiSuccessResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    @MainThread
    protected abstract fun uploadTag(): String?
}