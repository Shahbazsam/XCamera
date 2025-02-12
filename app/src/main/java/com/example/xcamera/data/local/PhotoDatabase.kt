package com.example.xcamera.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Photos::class] ,
    version = 2,
    exportSchema = false
)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao() : PhotoDao

}