package com.example.aplicacion1.ui

import android.content.Context
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
    private val _loading = MutableLiveData<Boolean>(false)

    val imageLiveData: LiveData<Bitmap?> = _imagenLiveData

    fun uploadImage(file: File, context: Context) {
        _loading.postValue(true)
        servicesRepository.uploadImage(file, callback = { bitmap ->
            _imagenLiveData.postValue(bitmap)
            _loading.postValue(false)
        }, context)
    }

    public fun getLoading(): MutableLiveData<Boolean> = _loading
}
