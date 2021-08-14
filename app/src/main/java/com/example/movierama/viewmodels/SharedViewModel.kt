package com.example.movierama.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.movierama.*
import com.example.movierama.data.fav_movies.MovieFav
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.ui.bindingAdapters.updateSrc
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SharedViewModel(var remoteRepository: RemoteRepository,  var context: Context) : ViewModel() {

    var job: Job = Job()

    var loading = MutableLiveData<Boolean>(false)
    var popularMovies=MutableLiveData<MutableList<MovieResult>>()
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)
    var favIdAdded=MutableLiveData<String?>(null)
    var favIdDeleted=MutableLiveData<String?>(null)
    var searchedText=MutableLiveData<String>()

    var favorites: LiveData<List<MovieFav>>

    val exceptionHandler = CoroutineExceptionHandler { _, e ->
       // dialog.hideLoadingDialog()
     //   noInternetException.postValue(true)
        job.cancel()
    }

    var itemPagedList: LiveData<PagedList<MovieResult>>? = null
    var itemPagedListSearched: LiveData<PagedList<MovieResult>>? = null
    val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

    var factory: DataSourceFactory
    lateinit var searchFactory: DataSourceFactory
    lateinit var local_repository: Local_repository

    init {
        factory = DataSourceFactory(remoteRepository,context = context,isSearch = false)
        itemPagedList = LivePagedListBuilder<Int, MovieResult>(factory, config).build()

        val moviesDao = MovieRoomDatabase.getDatabase(context = context).movieDao()
        local_repository = Local_repository(moviesDao)
        favorites = local_repository.getFavorites()
    }

    fun createFactory() {
        searchFactory = DataSourceFactory(remoteRepository,context = context,isSearch=true)
        itemPagedListSearched = LivePagedListBuilder<Int, MovieResult>(searchFactory, config).build()
    }


    fun searchTvShows(query: String) {
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

    fun addFavorite(id:String) {

          viewModelScope.launch(exceptionHandler + Dispatchers.Default) {
            runCatching {
                favorites.value?.firstOrNull{it.id == id}?.let {
                    local_repository.deleteFavorite(moviefav = MovieFav(id = id))
                } ?: kotlin.run {
                    local_repository.addFavorite(moviefav = MovieFav(id = id))
                }
                  //  local_repository.addFavorite(moviefav = MovieFav(id = id))

            }.onFailure {
                loading.postValue(false)
            }.onSuccess {

                favIdAdded.postValue(id)
            }
        }
    }

    fun deleteFavorite(id:String) {

        viewModelScope.launch(exceptionHandler + Dispatchers.Default) {
            runCatching {
                local_repository.deleteFavorite(moviefav = MovieFav(id = id))
            }.onFailure {
                loading.postValue(false)
            }.onSuccess {

                favIdDeleted.postValue(id)
            }
        }
    }
}