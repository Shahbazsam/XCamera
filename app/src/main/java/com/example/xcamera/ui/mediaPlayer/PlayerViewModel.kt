package com.example.xcamera.ui.mediaPlayer

import android.media.session.PlaybackState
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject





@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private val playBackState = mutableStateOf(PlayBackState())


    fun initializePlayer(uri: Uri) {

        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = playBackState.value.playWhenReady
        exoPlayer.seekTo(playBackState.value.mediaItemIndex , playBackState.value.playbackPosition)
        exoPlayer.prepare()
    }

    fun savePlayerState() {
        playBackState.value = PlayBackState(
            playWhenReady = exoPlayer.playWhenReady,
            mediaItemIndex = exoPlayer.currentMediaItemIndex,
            playbackPosition = exoPlayer.currentPosition
        )
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    fun getPlayer(): ExoPlayer = exoPlayer
    data class PlayBackState(
        val playWhenReady: Boolean = true,
        val mediaItemIndex: Int = 0,
        val playbackPosition: Long = 0L
    )
}