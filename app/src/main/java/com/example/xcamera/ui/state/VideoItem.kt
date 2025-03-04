package com.example.xcamera.ui.state

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem

data class VideoItem(
    val contentUri : Uri,
    val mediaItem : MediaItem,
    val bitmap : Bitmap,
    val name : String
    )
