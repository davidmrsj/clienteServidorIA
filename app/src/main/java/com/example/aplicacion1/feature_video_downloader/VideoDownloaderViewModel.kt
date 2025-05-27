package com.example.aplicacion1.feature_video_downloader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VideoDownloaderViewModel @Inject constructor() : ViewModel() {

    private val _videoItems = MutableLiveData<List<VideoItem>>()
    val videoItems: LiveData<List<VideoItem>> get() = _videoItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentlyPlayingVideoId = MutableLiveData<String?>()
    val currentlyPlayingVideoId: LiveData<String?> get() = _currentlyPlayingVideoId

    fun fetchVideoClips(youtubeUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Simulate network call

            val dummyItems = List(10) { index ->
                VideoItem(
                    id = UUID.randomUUID().toString(),
                    youtubeUrl = youtubeUrl,
                    driveUrl = "https://example.com/drive_video_$index.mp4",
                    thumbnailUrl = "https://example.com/thumbnail_$index.jpg",
                    // Other fields default
                )
            }
            _videoItems.value = dummyItems
            _isLoading.value = false
        }
    }

    fun onVideoFocused(videoId: String) {
        _currentlyPlayingVideoId.value = videoId
    }

    fun startDownload(videoItemToDownload: VideoItem) {
        _videoItems.value = _videoItems.value?.map { item ->
            if (item.id == videoItemToDownload.id) {
                item.copy(isDownloading = true, downloadProgress = 0)
            } else {
                item
            }
        }
        // Simulate download progress later
    }

    fun onDownloadComplete(completedVideoItem: VideoItem, localPath: String) {
        _videoItems.value = _videoItems.value?.map { item ->
            if (item.id == completedVideoItem.id) {
                item.copy(isDownloading = false, isDownloaded = true, downloadProgress = 100, localPath = localPath)
            } else {
                item
            }
        }
    }
}
