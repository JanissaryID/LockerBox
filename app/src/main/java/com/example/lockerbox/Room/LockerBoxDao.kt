package com.example.lockerbox.Room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LockerBoxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLockerBox(lockerBox : LockerBox)

    @Query("SELECT * FROM LockerBox ORDER BY idBox ASC")
    fun readAllData(): LiveData<List<LockerBox>>

    @Delete
    suspend fun deleteLockerBox(lockerBox: LockerBox)

    @Update
    fun updateLockerBox(lockerBox: LockerBox)

}