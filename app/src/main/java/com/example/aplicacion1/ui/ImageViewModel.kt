package com.example.aplicacion1.ui

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplicacion1.data.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val servicesRepository: ServicesRepository) : ViewModel() {

    private val _imagenLiveData = MutableLiveData<Bitmap?>()

    val imageLiveData: LiveData<Bitmap?> = _imagenLiveData

    fun uploadImage(file: File) {
        servicesRepository.uploadImage(file) { bitmap ->
            _imagenLiveData.postValue(bitmap)
        }
    }
}
