package com.example.movierama.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movierama.data.fav_movies.MovieFav

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: MovieFav): Long

    @Delete
    suspend fun delete(fav: MovieFav) : Int

    @Query("SELECT * from fav_table")
    fun get_favorites(): LiveData<List<MovieFav>>

}