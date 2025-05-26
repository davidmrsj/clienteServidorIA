package com.example.aplicacion1.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.aplicacion1.R
import com.example.aplicacion1.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.newSingleThreadContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.core.graphics.scale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val imageViewModel: ImageViewModel by viewModels()

    private var selectedImageFile: File? = null
    private var downloadedImageFile: File? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        binding.buttonUploadImage.setOnClickListener {
            openFilePicker()
        }
        binding.buttonDownloadImage.setOnClickListener {
            downloadImage()
        }
    }

    private fun setupListener() {
        imageViewModel.imageLiveData.observe(this) { bitmap ->
            saveImageToLocal(bitmap)
        }
    }

    private fun scaleBitmap(src: Bitmap, maxWidth: Int): Bitmap {
        val ratio = src.width.toFloat() / src.height
        val newWidth = maxWidth
        val newHeight = (maxWidth / ratio).toInt()
        return src.scale(newWidth, newHeight)
    }


    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedUri ->
                selectedImageFile = uriToFile(selectedUri)
                selectedImageFile?.let { file ->
                    imageViewModel.uploadImage(file)
                }
            } ?: run {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openFilePicker() {
        filePickerLauncher.launch("image/*")
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileName = getFileNameFromUri(uri)
        val tempFile = File(cacheDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "temp_image.jpg"
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    fileName = it.getString(index)
                }
            }
        }

        return fileName
    }

    private fun saveImageToLocal(bitmap: Bitmap?) {
        val fileName = "imagen_4k.png"
        val directory = File(getExternalFilesDir(null), "ProcessedImages")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, fileName)
        FileOutputStream(file).use { output ->
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        downloadedImageFile = file
        Toast.makeText(this, "Imagen guardada en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    private fun downloadImage() {
        downloadedImageFile?.let { file ->
            val uri =
                FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/png")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Abrir imagen con"))
        } ?: run {
            Toast.makeText(this, "No hay imagen descargada aún", Toast.LENGTH_SHORT).show()
        }
    }
}