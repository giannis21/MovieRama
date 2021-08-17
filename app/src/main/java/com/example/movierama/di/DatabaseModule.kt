package com.example.movierama.di

import android.content.Context
import com.example.movierama.data.database.MovieDao
import com.example.movierama.MovieRoomDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule{

   @Singleton
   @Provides
   internal fun getRoomDbInstance(context:Context) : MovieRoomDatabase{
       return  MovieRoomDatabase.getDatabase(context = context.applicationContext)
   }

    @Singleton
    @Provides
   internal fun getMovieDao(movieRoomDatabase: MovieRoomDatabase): MovieDao {
       return movieRoomDatabase.movieDao()
   }
}