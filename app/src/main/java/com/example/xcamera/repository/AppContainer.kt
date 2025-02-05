package com.example.xcamera.repository

import android.content.Context
import com.example.xcamera.data.PhotoDatabase

interface AppContainer {
    val photoRepository : PhotoRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val photoRepository: PhotoRepository by lazy {
        ImplPhotoRepository(PhotoDatabase.getDatabase(context).photoDao())
    }
}