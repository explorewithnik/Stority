package com.app.homeSpace.homeSpace.repo


import androidx.lifecycle.LiveData
import com.app.homeSpace.helper.AppExecutors
import com.app.homeSpace.homeSpace.data.HomeSpaceDao
import com.app.homeSpace.homeSpace.data.HomeSpaceTable
import com.app.homeSpace.homeSpace.data.HomeSpaceTableResponse
import com.app.homeSpace.remoteUtils.ApiResponse
import com.app.homeSpace.remoteUtils.NetworkBoundResource
import com.app.homeSpace.remoteUtils.Resource
import com.app.homeSpace.remoteUtils.WebService

import javax.inject.Inject


class HomeSpaceRepository @Inject constructor(
    private val executor: AppExecutors,
    private val webService: WebService,
    private val dao: HomeSpaceDao
) {

    fun fetchHomeSpaceDataList(shouldFetch: Boolean): LiveData<Resource<List<HomeSpaceTable>>> {

        return object :
            NetworkBoundResource<List<HomeSpaceTable>, HomeSpaceTableResponse>(executor) {

            override fun shouldFetch(data: List<HomeSpaceTable>?) = shouldFetch

            override fun uploadTag() = null

            override fun createCall(): LiveData<ApiResponse<HomeSpaceTableResponse>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun saveCallResult(item: HomeSpaceTableResponse) {
            }

            override fun loadFromDb() = dao.fetchAllHomeSpaceData()

        }.asLiveData()
    }

    fun fetchHomeSpaceData(id: String, shouldFetch: Boolean): LiveData<Resource<HomeSpaceTable>>? {

        return object : NetworkBoundResource<HomeSpaceTable, HomeSpaceTableResponse>(executor) {

            override fun shouldFetch(data: HomeSpaceTable?) = shouldFetch

            override fun uploadTag() = null

            override fun createCall(): LiveData<ApiResponse<HomeSpaceTableResponse>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun saveCallResult(item: HomeSpaceTableResponse) {
            }

            override fun loadFromDb() = dao.fetchHomeSpaceData(id = id)

        }.asLiveData()
    }

    fun insertCategoryData(data: HomeSpaceTable?) =
        executor.diskIO().execute { dao.insertHomeSpaceData(data = data) }

    fun updateCategory(data: HomeSpaceTable?) =
        executor.diskIO().execute { dao.updateHomeSpaceData(data = data) }

    fun deleteHomeSpaceAllData() = executor.diskIO().execute { dao.deleteHomeSpaceAllData() }

    fun deleteHomeSpaceData(data: HomeSpaceTable?) =
        executor.diskIO().execute { dao.deleteHomeSpaceData(id = data?.id) }

    fun deleteHomeSpaceDataList(list: List<HomeSpaceTable?>) {
        list.forEach {
            executor.diskIO().execute {
                dao.deleteHomeSpaceData(id = it?.id)
            }
        }
    }
}



