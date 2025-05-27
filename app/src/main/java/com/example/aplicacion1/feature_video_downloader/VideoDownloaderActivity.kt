package com.example.aplicacion1.feature_video_downloader

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion1.databinding.ActivityVideoDownloaderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoDownloaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoDownloaderBinding
    private val viewModel: VideoDownloaderViewModel by viewModels()
    private lateinit var videoAdapter: VideoAdapter
    private val pagerSnapHelper = PagerSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDownloaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        // Pause all currently visible players
        val layoutManager = binding.rvVideoItems.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        if (firstVisible != RecyclerView.NO_POSITION && lastVisible != RecyclerView.NO_POSITION) {
            for (i in firstVisible..lastVisible) {
                val viewHolder = binding.rvVideoItems.findViewHolderForAdapterPosition(i) as? VideoAdapter.VideoViewHolder
                viewHolder?.pausePlayer()
            }
        }
    }


    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(
            onVideoFocused = { videoItem ->
                // This lambda is kept for potential direct interaction if needed,
                // but primary focus update is through the scroll listener.
                // viewModel.onVideoFocused(videoItem.id)
            },
            isPlaying = { videoItem ->
                videoItem.id == viewModel.currentlyPlayingVideoId.value
            }
        )

        binding.rvVideoItems.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(this@VideoDownloaderActivity, LinearLayoutManager.HORIZONTAL, false)
            pagerSnapHelper.attachToRecyclerView(this) // Attach PagerSnapHelper
        }

        binding.rvVideoItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyFocusedItem()
                }
            }
        })
    }

    private fun notifyFocusedItem() {
        val layoutManager = binding.rvVideoItems.layoutManager as LinearLayoutManager
        val snapView = pagerSnapHelper.findSnapView(layoutManager)
        snapView?.let { view ->
            val snapPosition = layoutManager.getPosition(view)
            if (snapPosition != RecyclerView.NO_POSITION && videoAdapter.currentList.isNotEmpty() && snapPosition < videoAdapter.currentList.size) {
                val videoItem = videoAdapter.currentList[snapPosition]
                viewModel.onVideoFocused(videoItem.id)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnFetchVideos.setOnClickListener {
            val url = binding.etYoutubeUrl.text.toString().trim()
            if (url.isNotBlank()) {
                viewModel.fetchVideoClips(url)
            } else {
                // Optionally, show a Toast or error that URL is empty
                // For example: Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.videoItems.observe(this) { items ->
            videoAdapter.submitList(items) {
                // After list is submitted, ensure the initially focused item is correctly processed
                // This is important if the list loads and no scroll has occurred yet.
                if (viewModel.currentlyPlayingVideoId.value == null && items.isNotEmpty()) {
                    viewModel.onVideoFocused(items[0].id)
                } else {
                    // If a video was already focused, its state should be updated by notifyDataSetChanged below
                    // or by the adapter's natural re-binding due to isPlaying check.
                    // Forcing a focus notification here could be redundant or cause loops if not handled carefully.
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.currentlyPlayingVideoId.observe(this) { playingId ->
            // When the playing ID changes, we need to refresh the visible items
            // so their playing state (PlayerView vs Thumbnail) is re-evaluated by onBindViewHolder.
            // notifyDataSetChanged will ensure all visible items are rebound.
            videoAdapter.notifyDataSetChanged()

            // The PagerSnapHelper should handle centering the new item if the list itself doesn't change order.
            // If playback needs to be explicitly started/stopped, that logic will go into a later step.
        }
    }
}
