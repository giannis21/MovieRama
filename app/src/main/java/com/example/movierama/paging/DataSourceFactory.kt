package com.example.movierama

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.utils.ApiCallState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DataSourceFactory(var remoteRepository: RemoteRepository, private var query: String ="",var context: Context,var apiCallState: MutableLiveData<ApiCallState>) : DataSource.Factory<Int, MovieResult>() {

    override fun create(): DataSource<Int, MovieResult>? {
        return ShowsDataSource(remoteRepository,CoroutineScope(Dispatchers.Default),query,context = context,apiCallState)
    }


    fun updateQuery(query: String) {
        this.query = query
    }


}