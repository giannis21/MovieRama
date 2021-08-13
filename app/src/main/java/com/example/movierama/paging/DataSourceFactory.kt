package com.example.movierama

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.movierama.data.movie.Movie
import com.example.movierama.data.movie.MovieResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DataSourceFactory(var remoteRepository: RemoteRepository, private var query: String ="") : DataSource.Factory<Int, MovieResult>() {

     var itemLiveDataSource= MutableLiveData<PageKeyedDataSource<Int, MovieResult>>()

    override fun create(): DataSource<Int, MovieResult>? {
       // val item=
       // itemLiveDataSource.postValue(item)
        return ShowsDataSource(remoteRepository,CoroutineScope(Dispatchers.Default),query)
    }

//    fun getQuery() = query
//
//    fun getSource() = itemLiveDataSource.value

    fun updateQuery(query: String) {
        this.query = query
    }


}