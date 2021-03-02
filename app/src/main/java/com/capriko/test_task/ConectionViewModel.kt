package com.capriko.test_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConectionViewModel() : ViewModel() {

    private var isConnected = MutableLiveData<Boolean>()

    val listenNetWorkState: LiveData<Boolean>
        get() = isConnected

    fun setNetworkState(isLoading: Boolean) {
        isConnected.postValue(isLoading)
    }

}