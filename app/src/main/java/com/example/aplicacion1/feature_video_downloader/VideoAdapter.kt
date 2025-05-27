package com.example.aplicacion1.feature_video_downloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer // Corrected import
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicacion1.R // Assuming R is correctly resolved
import com.example.aplicacion1.databinding.ItemVideoBinding // Assuming ViewBinding is ItemVideoBinding

class VideoAdapter(
    private val onVideoFocused: (VideoItem) -> Unit, // Callback when an item might be focused by RecyclerView scroll
    private val isPlaying: (VideoItem) -> Boolean // Callback to check if this item is the one that should be playing
) : ListAdapter<VideoItem, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding, onVideoFocused, isPlaying)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer() // Release player when view is recycled
    }

    class VideoViewHolder(
        private val binding: ItemVideoBinding,
        private val onVideoFocused: (VideoItem) -> Unit, // Not directly used here, but passed for potential future use or setup
        private val isPlaying: (VideoItem) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        private var player: ExoPlayer? = null // Corrected type to ExoPlayer

        fun bind(videoItem: VideoItem) {
            binding.root.tag = videoItem // Tag the root view with the item for easier access from scroll listeners if needed

            Glide.with(binding.ivVideoThumbnail.context)
                .load(videoItem.thumbnailUrl)
                .placeholder(R.drawable.ic_launcher_background) // Replace with a proper placeholder
                .error(R.drawable.ic_launcher_foreground) // Replace with a proper error image
                .into(binding.ivVideoThumbnail)

            if (isPlaying(videoItem)) {
                startPlayer(videoItem)
            } else {
                releasePlayer() // Or pausePlayer() if you want to keep the player instance around but paused
                binding.playerViewVideo.visibility = View.GONE
                binding.ivVideoThumbnail.visibility = View.VISIBLE
                binding.viewSelectionIndicator.visibility = View.GONE
            }
        }

        private fun initializePlayer() {
            if (player == null) {
                player = ExoPlayer.Builder(binding.playerViewVideo.context).build() // Corrected type
                binding.playerViewVideo.player = player
                // Configure player listeners, etc. if needed
            }
        }

        fun startPlayer(videoItem: VideoItem) {
            initializePlayer() // Ensure player is initialized

            // Prefer localPath if video is downloaded
            val videoUrl = videoItem.localPath ?: videoItem.driveUrl
            if (videoUrl.isNotEmpty()) { // Check if URL is not empty
                val mediaItem = MediaItem.fromUri(videoUrl)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true // Autostart
            }

            binding.playerViewVideo.visibility = View.VISIBLE
            binding.ivVideoThumbnail.visibility = View.GONE
            binding.viewSelectionIndicator.visibility = View.VISIBLE // Show selection
        }

        fun pausePlayer() {
            player?.pause()
            // Optionally, you could also hide the selection indicator here if needed
            // binding.viewSelectionIndicator.visibility = View.GONE
        }

        fun releasePlayer() {
            player?.release()
            player = null
            binding.playerViewVideo.player = null
            binding.playerViewVideo.visibility = View.GONE
            binding.ivVideoThumbnail.visibility = View.VISIBLE
            binding.viewSelectionIndicator.visibility = View.GONE // Ensure deselected state
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem == newItem
        }
    }
}
