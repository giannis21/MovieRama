package com.example.movierama

import com.example.movierama.data.movie.Movies
import com.example.movierama.data.movieDetail.Detail_Movie
import com.example.movierama.data.reviews.Reviews
import com.example.movierama.network.NetworkConnectionIncterceptor
import com.example.movierama.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiClient {

    @GET("movie/popular")
    suspend fun getMostPopular(@Query("language") lang:String="en-US", @Query("page") page:String) : Response<Movies>

    @GET("search/movie")
    suspend fun searchTvShow(@Query("language") lang: String="en-US", @Query("page") page:String ,@Query("query") query: String): Response<Movies>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") id:String, @Query("language") lang: String="en-US" ): Response<Detail_Movie>


    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(@Path("movie_id") id:String ,@Query("language") lang:String="en-US") : Response<Reviews>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies( @Path("movie_id") id:String,@Query("language") lang:String="en-US", @Query("page") page:String) : Response<Movies>

}