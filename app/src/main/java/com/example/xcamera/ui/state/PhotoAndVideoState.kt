package com.example.xcamera.ui.state

data class PhotoState(
    val id : Int ,
    val photoPath : String ? = null
)

data class VideoState(
    val id : Int,
    val videoPath : String ? = null
)
