package com.example.xcamera.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photos(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0 ,
    val photos : ByteArray
)
