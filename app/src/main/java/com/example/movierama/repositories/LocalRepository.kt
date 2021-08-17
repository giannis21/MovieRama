package com.example.movierama


import androidx.lifecycle.LiveData
import com.example.movierama.data.database.MovieDao
import com.example.movierama.data.fav_movies.MovieFav
import javax.inject.Inject


class LocalRepository @Inject constructor(private val movieDao: MovieDao) {

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
