package com.example.aplicacion1.feature_video_downloader

data class VideoItem(
    val id: String,
    val youtubeUrl: String,
    val driveUrl: String,
    val thumbnailUrl: String,
    var isDownloading: Boolean = false,
    var downloadProgress: Int = 0,
    var isDownloaded: Boolean = false,
    var localPath: String? = null
)
