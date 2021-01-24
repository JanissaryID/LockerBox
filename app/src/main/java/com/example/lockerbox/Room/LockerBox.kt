package com.example.lockerbox.Room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "LockerBox")
data class LockerBox(
    @PrimaryKey(autoGenerate = true)

    val idBox: Int? = null,

    val codeLocker: String? = null,

    val noBox: Int? = null,

    val password: String? = null,

    val duration: Int? = null,

    val TimeOut: Boolean? = null,
) : Parcelable
