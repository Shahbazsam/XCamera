package com.example.xcamera.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photos: Photos)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideo(photos: Photos)

    @Delete
    suspend fun delete(photos: Photos)

    @Query("SELECT * FROM photos WHERE photoPath IS NOT NULL")
    fun getAllPhoto(): Flow<List<Photos>>

    @Query("SELECT * FROM photos WHERE videoPath IS NOT NULL")
    fun getAllVideos(): Flow<List<Photos>>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoOrVideoById(photoId : Int) : Flow<Photos>
}