package com.example.xcamera.data.repository

import com.example.xcamera.data.local.PhotoDao
import com.example.xcamera.data.local.Photos
import kotlinx.coroutines.flow.Flow

interface PhotoRepository  {

    suspend fun insertPhoto(photos: Photos)
    suspend fun insertVideo(photos: Photos)
    suspend fun deletePhoto(photos: Photos)
    fun getAllPhotos() : Flow<List<Photos>>
    fun getAllVideos() : Flow<List<Photos>>
    fun getPhotoOrVideoById(photoId : Int) : Flow<Photos>

}

class ImplPhotoRepository(private val photoDao: PhotoDao) : PhotoRepository {

    override suspend fun insertPhoto(photos: Photos) {
        photoDao.insertPhoto(photos)
    }

    override suspend fun insertVideo(photos: Photos) {
        photoDao.insertVideo(photos)
    }

    override suspend fun deletePhoto(photos: Photos) {
        photoDao.delete(photos)
    }

    override fun getAllPhotos(): Flow<List<Photos>> {
        return photoDao.getAllPhoto()
    }

    override fun getAllVideos(): Flow<List<Photos>> {
        return photoDao.getAllVideos()
    }

    override fun getPhotoOrVideoById(photoId: Int): Flow<Photos> {
        return photoDao.getPhotoOrVideoById(photoId)
    }
}