package com.dicoding.sortify.data.api

import com.dicoding.sortify.data.model.ImageUpload
import com.dicoding.sortify.data.model.Status
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("api")
    fun getStatus(
    ): Call<Status>

    @Multipart
    @POST("api")
    fun uploadImage(
        @Part file: MultipartBody.Part,
    ): Call<ImageUpload>
}