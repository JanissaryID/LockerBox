package com.example.lockerbox.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LockerBox::class], version = 1)
abstract class LBDatabase :RoomDatabase() {

    abstract fun LockerBoxDao(): LockerBoxDao

    companion object{
        @Volatile
        private var INSTANCE: LBDatabase? = null

        fun getDatabase(context: Context) : LBDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LBDatabase::class.java,
                    "lockerbox_databse"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
        fun destroyInstance(){
            INSTANCE = null
        }
    }
}