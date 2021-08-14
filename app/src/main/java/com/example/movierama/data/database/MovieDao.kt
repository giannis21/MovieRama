package com.example.movierama

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

//    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='nowPlaying'")
//    suspend fun get_now_playing_per_page(page: Int): NowPlaying
//
//
//    @Query("SELECT count(*) from now_playing")
//    fun isDbEmpty(): Int
//
//    @Query("DELETE FROM now_playing ")
//    suspend fun deleteAllFromNowPlaying()





}