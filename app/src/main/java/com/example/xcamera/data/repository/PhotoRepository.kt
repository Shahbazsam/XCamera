package com.example.xcamera.data.repository

import com.example.xcamera.data.local.PhotoDao
import com.example.xcamera.data.local.Photos
import kotlinx.coroutines.flow.Flow

interface PhotoRepository  {

    suspend fun insertPhoto(photos: Photos)
    suspend fun deletePhoto(photos: Photos)
    fun getAllPhotos() : Flow<List<Photos>>
    fun getPhotoById(photoId : Int) : Flow<Photos>

}

class ImplPhotoRepository(private val photoDao: PhotoDao) : PhotoRepository {

    override suspend fun insertPhoto(photos: Photos) {
        photoDao.insert(photos)
    }

    override suspend fun deletePhoto(photos: Photos) {
        photoDao.delete(photos)
    }

    override fun getAllPhotos(): Flow<List<Photos>> {
        return photoDao.getAllPhoto()
    }

    override fun getPhotoById(photoId: Int): Flow<Photos> {
        return photoDao.getPhotoById(photoId)
    }


}