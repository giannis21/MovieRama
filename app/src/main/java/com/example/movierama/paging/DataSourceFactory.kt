package com.example.movierama

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.movierama.data.movie.Movie
import com.example.movierama.data.movie.MovieResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DataSourceFactory(var remoteRepository: RemoteRepository, private var query: String ="",var context:Context,var isSearch:Boolean) : DataSource.Factory<Int, MovieResult>() {

     var itemLiveDataSource= MutableLiveData<PageKeyedDataSource<Int, MovieResult>>()

    override fun create(): DataSource<Int, MovieResult>? {
        val item= ShowsDataSource(remoteRepository,CoroutineScope(Dispatchers.Default),query,context,isSearch)
        itemLiveDataSource.postValue(item)
        return item
    }

//    fun getQuery() = query
//
//    fun getSource() = itemLiveDataSource.value

    fun updateQuery(query: String) {
        this.query = query
    }


}