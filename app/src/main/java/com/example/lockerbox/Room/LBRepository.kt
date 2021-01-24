package com.example.lockerbox.Room

import androidx.lifecycle.LiveData

class LBRepository(private val lockerboxDao: LockerBoxDao) {

    val readAllData: LiveData<List<LockerBox>> = lockerboxDao.readAllData()

    suspend fun addLockerBox(lockerBox : LockerBox){
        lockerboxDao.addLockerBox(lockerBox)
    }

    suspend fun deleteLockerBox(lockerBox: LockerBox){
        lockerboxDao.deleteLockerBox(lockerBox)
    }

    fun updateLockerBox(lockerBox: LockerBox){
        lockerboxDao.updateLockerBox(lockerBox)
    }
}