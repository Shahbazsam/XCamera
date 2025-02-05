package com.example.xcamera.repository

import com.example.xcamera.data.PhotoDao
import com.example.xcamera.data.Photos
import kotlinx.coroutines.flow.Flow

interface PhotoRepository  {

    suspend fun insertPhoto(photos: Photos)
    suspend fun deletePhoto(photos: Photos)
    suspend fun getAllPhotos() : Flow<List<Photos>>
    suspend fun getPhotoById(photoId : Int) : Flow<Photos>

}

class ImplPhotoRepository(private val photoDao: PhotoDao ) : PhotoRepository {

    override suspend fun insertPhoto(photos: Photos) {
        photoDao.Insert(photos)
    }

    override suspend fun deletePhoto(photos: Photos) {
        photoDao.delete(photos)
    }

    override suspend fun getAllPhotos(): Flow<List<Photos>> {
        return photoDao.getAllPhoto()
    }

    override suspend fun getPhotoById(photoId: Int): Flow<Photos> {
        return photoDao.getPhotoById(photoId)
    }


}