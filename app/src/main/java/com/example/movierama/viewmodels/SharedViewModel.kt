package com.example.movierama.viewmodels

import android.content.ClipData.Item
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.movierama.DataSourceFactory
import com.example.movierama.RemoteRepository
import com.example.movierama.data.movie.MovieResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SharedViewModel(var remoteRepository: RemoteRepository, context: Context) : ViewModel() {
    var job: Job = Job()
    var loading = MutableLiveData<Boolean>(false)
    var popularMovies=MutableLiveData<MutableList<MovieResult>>()
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)

    val exceptionHandler = CoroutineExceptionHandler { _, e ->
       // dialog.hideLoadingDialog()
     //   noInternetException.postValue(true)
        job.cancel()
    }

    var itemPagedList: LiveData<PagedList<MovieResult>>? = null
    val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()
    val factory = DataSourceFactory(remoteRepository)
    init {

        itemPagedList = LivePagedListBuilder<Int, MovieResult>(factory, config).build()
    }


    fun searchTvShows1(query: String) {
        val search = query.trim()
        factory.updateQuery(search)
    }

    fun reserfactory(){
        factory.updateQuery("")
    }


    fun getPopularMovies( ) {
      //  dialog.displayLoadingDialog()

        job= viewModelScope.launch(exceptionHandler + Dispatchers.Default) {
            runCatching {
                remoteRepository.getMostPopular(3)
            }.onFailure {

              //  error.value= true
                loading.postValue(false)
                //dialog.hideLoadingDialog()


            }.onSuccess {
                loading.postValue(false)
             //   dialog.hideLoadingDialog()

                if(it.isSuccessful){
                    popularMovies.postValue((it.body()!!.results.toMutableList()))
                }else{
                    error.postValue(true)
                }
            }
        }
    }
}