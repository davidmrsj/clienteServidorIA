package com.example.aplicacion1.data.network.services

import android.content.Context
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
import java.io.FileOutputStream
import javax.inject.Inject

class ServicesDataSource @Inject constructor(private val apiService: ApiService) {

    fun uploadImage(file: File, callback: (Bitmap?) -> Unit, context: Context) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        apiService.uploadImage(body).enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body()!=null) {
                    // 1) Escribir el stream a un fichero
                    val outFile = File(context.cacheDir, "tmp_scaled.jpg")
                    response.body()!!.byteStream().use { input ->
                        FileOutputStream(outFile).use { output -> input.copyTo(output) }
                    }
                    // 2) Decodificar con downsampling si lo vas a mostrar
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                        BitmapFactory.decodeFile(outFile.path, this)
                        val reqWidth = 1024 // tamaño de tu ImageView
                        inSampleSize = calculateInSampleSize(this, reqWidth, reqWidth)
                        inJustDecodeBounds = false
                    }
                    val bmp = BitmapFactory.decodeFile(outFile.path, options)
                    callback(bmp)
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(null)
            }
        })
    }

    // helper para calcular inSampleSize
    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}