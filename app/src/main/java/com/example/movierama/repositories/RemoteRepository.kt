package com.example.movierama

import com.example.movierama.data.movie.Movies
import com.example.movierama.data.movieDetail.Detail_Movie
import com.example.movierama.data.reviews.Reviews
import retrofit2.Response


class RemoteRepository(private val my_Api: ApiClient) {


    suspend fun searchTvShows(page:Int,query:String): Response<Movies> {
        return my_Api.searchTvShow("en_US",page.toString(),query)
    }

    suspend fun getMostPopular(page:Int):Response<Movies> {
        return my_Api.getMostPopular("en_US",page.toString())
    }

    suspend fun getMovieReviews(id:String): Response<Reviews> {
        return my_Api.getMovieReviews(id,"en_US")
    }

    suspend fun getSimilarMovies(id:String,page:Int): Response<Movies> {
        return my_Api.getSimilarMovies(id,"en_US",page.toString())
    }

    suspend fun getMovieDetails(id:String): Response<Detail_Movie> {
        return my_Api.getMovieDetails(id,"en_US")
    }


}