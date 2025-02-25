package com.example.xcamera.ui.mediaPlayer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L


    fun initializePlayer(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.seekTo(mediaItemIndex , playbackPosition)
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare()
    }

    fun releasePlayer() {
        playbackPosition = exoPlayer.currentPosition
        mediaItemIndex = exoPlayer.currentMediaItemIndex
        playWhenReady = exoPlayer.playWhenReady
        exoPlayer.release()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun getPlayer(): ExoPlayer = exoPlayer
}