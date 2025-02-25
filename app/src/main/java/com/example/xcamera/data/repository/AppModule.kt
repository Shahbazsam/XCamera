package com.example.xcamera.data.repository

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.example.xcamera.data.local.PhotoDao
import com.example.xcamera.data.local.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context) : PhotoDatabase {
        return Room.databaseBuilder(
            context,
            PhotoDatabase::class.java,
            "photo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesPhotoDao(photoDatabase: PhotoDatabase) : PhotoDao {
        return photoDatabase.photoDao()
    }

    @Provides
    @Singleton
    fun providesPhotoRepository(photoDao: PhotoDao) : PhotoRepository {
        return  ImplPhotoRepository(photoDao)
    }

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context) : ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }
}