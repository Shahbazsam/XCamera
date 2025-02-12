package com.example.xcamera.data.local

import android.graphics.Bitmap
import android.graphics.Path
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photos(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0 ,
    val photoPath : String
)
