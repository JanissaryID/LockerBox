package com.example.lockerbox.Room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LockerBoxViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<LockerBox>>
    private val repository:  LBRepository

    init {
        val LBDao = LBDatabase.getDatabase(application).LockerBoxDao()
        repository = LBRepository(LBDao)
        readAllData = repository.readAllData
    }

    fun addLockerBox(lockerbox: LockerBox){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLockerBox(lockerbox)
        }
    }

    fun deleteLockerBox(lockerbox: LockerBox){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLockerBox(lockerbox)
        }
    }

    fun updateLockerBox(lockerbox: LockerBox){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLockerBox(lockerbox)
        }
    }
}