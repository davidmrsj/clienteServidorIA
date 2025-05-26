package com.example.aplicacion1.data.network.services

import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

interface ApiService {

    @Multipart
    @Streaming
    @POST("escalar-imagen/")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>
}
