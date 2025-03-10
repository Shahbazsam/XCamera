package com.example.xcamera.ui.mediaPlayer

import android.app.Application
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.xcamera.data.repository.PhotoRepository
import com.example.xcamera.ui.state.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val repository : PhotoRepository,
    private val metaDataReader: MetaDataReader
) : ViewModel() {

    private val videoList = repository.getAllVideos()
        .mapNotNull { videos -> videos.mapNotNull { it.videoPath } }
        .stateIn(viewModelScope , SharingStarted.Lazily , emptyList())

    val videoItemState = videoList.map { uris ->
        uris.map { path  ->
            val thumbnail = runBlocking { getThumbnailAsync(path) }
            VideoItem(
                contentUri =Uri.parse(path),
                mediaItem = MediaItem.fromUri(Uri.parse(path)),
                bitmap = thumbnail,
                name = metaDataReader.getMetaDataFromUri(Uri.parse(path))?.fileName ?: "No Name !"
            )
        }
    }.stateIn(viewModelScope , SharingStarted.WhileSubscribed(5000) , emptyList())

    init {
        player.prepare()
        viewModelScope.launch {
            videoList.collect { videoPaths -> // Collect the flow
                val mediaItems = videoPaths.map { path ->
                    MediaItem.fromUri(Uri.parse(path))
                }
                Log.d("MetaData", "Number of video paths: ${videoPaths.size}") // Log here
                player.addMediaItems(mediaItems)
            }
        }
    }

    fun playVideo(uri: Uri) {
        player.setMediaItem(
            videoItemState.value.find {
                it.contentUri == uri
            } ?.mediaItem ?: return
        )


    }

    private suspend fun getThumbnailAsync(videoPath: String): Bitmap {
        return withContext(Dispatchers.IO) {
            ThumbnailUtils.createVideoThumbnail(
                videoPath,
                MediaStore.Video.Thumbnails.MINI_KIND
            ) ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }
    }


}