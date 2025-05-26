package com.example.aplicacion1.data.network.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.aplicacion1.data.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import javax.inject.Inject

class ServicesDataSource @Inject constructor(private val apiService: ApiService) {

    fun uploadImage(file: File, callback: (Bitmap?) -> Unit) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val call = apiService.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                        callback(bitmap)
                    }
                } else {
                    callback(null)
                    Log.e("ServicesDataSource", "Error al subir la imagen" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(null)
                Log.e("ServicesDataSource", "Error al subir la imagen", t)
            }
        })
    }

}