package com.example.movierama

import com.example.movierama.data.movie.Movie
import com.example.movierama.data.movieDetail.Detail_Movie
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
    suspend fun getMostPopular(@Query("language") lang:String="en-US", @Query("page") page:String) : Response<Movie>

    @GET("search/movie")
    suspend fun searchTvShow(@Query("language") lang: String="en-US", @Query("page") page:String ,@Query("query") query: String): Response<Movie>

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
                    .addQueryParameter("api_key", Constants.API_KEY).build()
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
                .baseUrl(Constants.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
    }



}