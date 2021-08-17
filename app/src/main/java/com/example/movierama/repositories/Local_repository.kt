package com.example.movierama


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.movierama.data.fav_movies.MovieFav
import javax.inject.Inject


class Local_repository @Inject constructor(val movieDao: MovieDao) {


    //val countOfNowPLaying = movieDao.countOfNowPLaying()
    fun getFavorites():  LiveData<List<MovieFav>>  {
        return movieDao.get_favorites()
    }
    suspend fun addFavorite(moviefav: MovieFav): Long {
        return movieDao.insert(moviefav)
    }

    suspend fun deleteFavorite(moviefav: MovieFav): Int {
        return movieDao.delete(moviefav)
    }

}
