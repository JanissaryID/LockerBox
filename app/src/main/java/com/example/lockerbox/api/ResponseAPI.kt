package com.example.lockerbox.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseAPI(

	@field:SerializedName("NoBox")
	val noBox: Int? = null,

	@field:SerializedName("Duration")
	val duration: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("IsRent")
	val isRent: Boolean? = null,

	@field:SerializedName("CodeLocker")
	val codeLocker: String? = null,

	@field:SerializedName("Password")
	val password: String? = null,

	@field:SerializedName("IsOpen")
	val isOpen: Boolean? = null,

	@field:SerializedName("TimeOut")
	val timeOut: Boolean? = null
) : Parcelable
