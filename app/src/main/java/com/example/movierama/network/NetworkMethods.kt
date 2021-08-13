package com.example.tvshows.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build



object netMethods {

    //var listener: InternetCallback? = null

    fun hasInternet(applicationContext: Context, return_boolean_state:Boolean) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!isInternetAvailable1(applicationContext)) {
                //  listener?.hasInternet(false)
                if (!return_boolean_state)
                // throw NoInternetException("No internet connection")
                else{}

            }
        }
    }



    fun isInternetAvailable1(applicationContext: Context): Boolean {
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