package com.example.movierama.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.movierama.utils.NoInternetException


object NetworkMethods {

    fun hasInternet(applicationContext: Context) {

            if (!isInternetAvailable(applicationContext)) {
                  throw NoInternetException("No internet connection")
            }

    }

    fun isInternetAvailable(applicationContext: Context): Boolean {
        var result = false
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }
}