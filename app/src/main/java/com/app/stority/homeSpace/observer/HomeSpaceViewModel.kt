package com.app.stority.homeSpace.observer

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.app.stority.helper.Logger
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.repo.HomeSpaceRepository
import com.app.stority.remoteUtils.AbsentLiveData
import com.app.stority.remoteUtils.Resource
import javax.inject.Inject


class HomeSpaceViewModel @Inject constructor(

    var repo: HomeSpaceRepository
) : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    var apiCall = MutableLiveData<String>()
    var searchApiCal = MutableLiveData<String>()

    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    var data = HomeSpaceTable()

    var result: LiveData<Resource<List<HomeSpaceTable>>> = Transformations.switchMap(apiCall) {
        data
        when (apiCall.value) {
            null -> AbsentLiveData.create()
            else -> repo.fetchHomeSpaceDataList(false)
        }
    }

    fun init(value: String): Boolean {
        Logger.e(Thread.currentThread(), "value")
        return when {
            apiCall.value == value -> {
                Logger.e(Thread.currentThread(), "init apiCall.value ")
                true
            }
            value == "2" -> {
                Logger.e(Thread.currentThread(), "init value = 2 ")
                apiCall.value = value
                false
            }
            else -> {
                Logger.e(Thread.currentThread(), "init else")
                apiCall.value = value
                false
            }
        }
    }

    fun initSearch(query: String) {
        Logger.e(Thread.currentThread(), "initSearch")
        searchApiCal.value = query
    }

    fun deleteHomeSpaceData(data: HomeSpaceTable?) {
        repo.deleteHomeSpaceData(data = data)
    }

    fun deleteAllHomeSpaceData() {
        repo.deleteAllHomeSpaceData()
    }

    fun deleteHomeSpaceListData(list: List<HomeSpaceTable?>) {
        repo.deleteHomeSpaceDataList(list = list)
    }

    fun insertCategory(data: HomeSpaceTable?) {
        repo.insertCategoryData(data)
    }

    fun updateCategory(data: HomeSpaceTable?) {
        repo.updateCategory(data)
    }

}