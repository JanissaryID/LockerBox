package com.example.lockerbox.api

import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("lb")
    fun getBoxLocker(@Query("CodeLocker") CodeLocker : String ): Call<List<ResponseAPI>>

    @GET("lb/find")
    fun getIdPassword(
        @Query("id") NoBox: Int?,
        @Query("Password") Password: String?
    ): Call<List<ResponseAPI>>

    @FormUrlEncoded
//    @Headers("Accept:application/json", "Content-Type:application/json;")
    @PUT("lb/{id}/")
    fun putBoxLocker(
            @Path("id") id: Int,
            @Field("CodeLocker") CodeLocker: String?,
            @Field("NoBox") NoBox: Int?,
            @Field("Password") Password: String?,
            @Field("Duration") Duration: Int?,
            @Field("IsRent") IsRent: Boolean?,
            @Field("IsOpen") IsOpen: Boolean?
    ): Call<ResponseAPI>

    @FormUrlEncoded
//    @Headers("Accept:application/json", "Content-Type:application/json;")
    @PATCH("lb/{id}/")
    fun patchBoxLocker(
            @Path("id") id: Int,
            @Field("CodeLocker") CodeLocker: String?,
            @Field("NoBox") NoBox: Int?,
            @Field("Password") Password: String?,
            @Field("Duration") Duration: Int?,
            @Field("IsRent") IsRent: Boolean?,
            @Field("IsOpen") IsOpen: Boolean?
    ): Call<ResponseAPI>

}