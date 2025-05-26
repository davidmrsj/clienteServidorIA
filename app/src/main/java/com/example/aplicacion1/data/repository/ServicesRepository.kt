package com.example.aplicacion1.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.aplicacion1.data.network.services.ServicesDataSource
import java.io.File
import javax.inject.Inject

class ServicesRepository @Inject constructor(private val servicesDataSource: ServicesDataSource) {

    fun uploadImage(file: File, callback: (Bitmap?) -> Unit, context: Context) {
        servicesDataSource.uploadImage(file, callback, context)
    }

}