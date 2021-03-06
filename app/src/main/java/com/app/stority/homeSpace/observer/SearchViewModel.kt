package com.app.stority.homeSpace.observer

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


class SearchViewModel @Inject constructor(

    var repo: HomeSpaceRepository
) : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    var apiCall = MutableLiveData<String>()

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
        return if (apiCall.value == value) {
            true
        } else {
            Logger.e(Thread.currentThread(), "init")
            apiCall.value = value
            false
        }
    }
}