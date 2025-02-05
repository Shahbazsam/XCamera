package com.example.xcamera.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun Insert(photos: Photos)

    @Delete
    suspend fun delete(photos: Photos)

    @Query("SELECT * FROM photos")
    fun getAllPhoto(): Flow<List<Photos>>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoById(photoId : Int) : Flow<Photos>
}