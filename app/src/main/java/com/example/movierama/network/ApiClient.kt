package com.example.movierama

import com.example.movierama.data.movie.Movie
import com.example.movierama.data.movieDetail.Detail_Movie
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiClient {



    @GET("movie/popular")
    suspend fun getMostPopular(@Query("language") lang:String="en-US", @Query("page") page:String) : Response<Movie>

    @GET("search/movie")
    suspend fun searchTvShow(@Query("language") lang: String="en-US", @Query("page") page:String ,@Query("query") query: String): Movie

    @GET("tv/{movie_id}")
    suspend fun getTvShowDetails(@Path("movie_id") id:String, @Query("language") lang: String="en-US" ): Detail_Movie

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(@Query("language") lang:String="en-US", @Path("movie_id") id:String, @Query("page") page:String) : Movie

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(@Query("language") lang:String="en-US", @Path("movie_id") id:String, @Query("page") page:String) : Movie

    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClient {
            val interceptor = Interceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("api_key", "30842f7c80f80bb3ad8a2fb98195544d").build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                chain.proceed(request)
            }

            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val okHttpClient1 = OkHttpClient.Builder()
                .addInterceptor(networkConnectionIncterceptor)
                .addInterceptor(interceptor)
                .addInterceptor(logging)

            return Retrofit.Builder().client(okHttpClient1.build())
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
    }

    // https://api.themoviedb.org/3/genre/movie/list?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US


}