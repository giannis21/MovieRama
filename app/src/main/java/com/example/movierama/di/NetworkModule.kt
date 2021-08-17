package com.example.movierama.di


import com.example.movierama.ApiClient
import com.example.movierama.network.NetworkConnectionIncterceptor
import com.example.movierama.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    internal fun provideRetrofitInterface(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClient {

        val interceptor = Interceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter("api_key", Constants.API_KEY).build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            val response = chain.proceed(request)
            response
        }


        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient1 = OkHttpClient.Builder()
            .addInterceptor(networkConnectionIncterceptor)
            .addInterceptor(logging)
            .addInterceptor(interceptor)

        return Retrofit.Builder().client(okHttpClient1.build())
            .baseUrl(Constants.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiClient::class.java)

    }



}