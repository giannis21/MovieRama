package com.example.movierama

import com.example.movierama.data.movie.Movie
import retrofit2.Response


class RemoteRepository(private val my_Api: ApiClient) {


    suspend fun searchTvShows(page:Int,query:String): Movie {
        return my_Api.searchTvShow("en_US",page.toString(),query)
    }

    suspend fun getMostPopular(page:Int):Response<Movie> {
        return my_Api.getMostPopular("en_US",page.toString())
    }

    suspend fun getMovieReviews(id:String,page:Int): Movie {
        return my_Api.getMovieReviews(id,"en_US",page.toString())
    }

    suspend fun getSimilarMovies(id:String,page:Int): Movie {
        return my_Api.getSimilarMovies(id,"en_US",page.toString())
    }


}