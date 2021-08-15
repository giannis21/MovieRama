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
import com.example.movierama.data.movie.Movies
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.data.movieDetail.Detail_Movie
import com.example.movierama.data.reviews.Reviews
import com.example.movierama.utils.NoInternetException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Response


class SharedViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var job: Job = Job()

    var loading = MutableLiveData<Boolean>(false)

    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)
    var favIdDbChanged = MutableLiveData<String?>(null)
    var favIdAdded: Boolean = false

    var currentDetailObj = MutableLiveData<Detail_Movie?>(null)
    var currentSimilarObj = MutableLiveData<Movies?>(null)
    var currentReviewsObj = MutableLiveData<Reviews?>(null)
    var favorites: LiveData<List<MovieFav>>

    var showLoader= MutableLiveData<Boolean?>(null)
    val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // dialog.hideLoadingDialog()
        //   noInternetException.postValue(true)
        job.cancel()
    }

    var itemPagedList: LiveData<PagedList<MovieResult>>? = null

    val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

    var factory: DataSourceFactory

    var local_repository: Local_repository

    init {
        factory = DataSourceFactory(remoteRepository, context = context, isSearch = false)
        itemPagedList = LivePagedListBuilder<Int, MovieResult>(factory, config).build()

        val moviesDao = MovieRoomDatabase.getDatabase(context = context).movieDao()
        local_repository = Local_repository(moviesDao)
        favorites = local_repository.getFavorites()
    }

    fun searchTvShows(query: String) {
        factory.updateQuery(query.trim())
    }


    fun getMovieDetails(id: String) {
        showLoader.postValue(true)
        viewModelScope.launch {
            getDetailsFlow(id)
                .catch { e ->
                    handleExceptions(e)
                    showLoader.postValue(false)
                }.flowOn(Dispatchers.IO)
                .onCompletion {

                    getReviews(id)
                    getSimilar(id)
                    showLoader.postValue(false)
                }.collect {
                    if(it.isSuccessful)
                        currentDetailObj.postValue(it.body())
                    else
                        error.postValue(true)

                }

        }

    }



    private fun getSimilar(id: String) {
        viewModelScope.launch(Dispatchers.IO + Job()) {

            getSimilarFlow(id).catch { e ->
                println("call failed(similar) ${e.localizedMessage}")
            }.collect { similar ->
                if(similar.isSuccessful)
                    currentSimilarObj.postValue(similar.body())
            }
        }
    }

    private fun getReviews(id: String) {
        viewModelScope.launch(Dispatchers.IO + Job()) {
            getReviewsFlow(id).catch { e ->
                println("call failed(reviews) ${e.localizedMessage}")
            }.collect { reviews ->
                if(reviews.isSuccessful)
                    currentReviewsObj.postValue(reviews.body())
            }
        }
    }



    fun getDetailsFlow(id: String): Flow<Response<Detail_Movie>> = flow {
        emit(remoteRepository.getMovieDetails(id))
    }

    fun getReviewsFlow(id: String): Flow<Response<Reviews>> = flow {
        emit(remoteRepository.getMovieReviews(id))
    }

    fun getSimilarFlow(id: String): Flow<Response<Movies>> = flow {
        emit(remoteRepository.getSimilarMovies(id, 1))
    }

    fun addFavorite(id: String) {
        var inserted = false
        viewModelScope.launch(exceptionHandler + Dispatchers.Default) {

            runCatching {

                favorites.value?.firstOrNull { it.id == id }?.let {
                    local_repository.deleteFavorite(moviefav = MovieFav(id = id))
                } ?: kotlin.run {
                    inserted = true
                    local_repository.addFavorite(moviefav = MovieFav(id = id))
                }

            }.onFailure {
                loading.postValue(false)
            }.onSuccess {
                favIdAdded = inserted
                favIdDbChanged.postValue(id)

            }
        }
    }

    private fun handleExceptions(e: Throwable) {
        if (e is NoInternetException)
            noInternetException.postValue(true)
        else
            error.postValue(true)
    }

    /*
        private suspend fun getOptionalInfo(id: String) {
        getReviewsFlow(id).zip(getSimilarFlow(id)) { reviews, similarMovies ->
            println("reviews result ${reviews}")
            println("reviews similar result ${similarMovies.toString()}")
            return@zip
        }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                println("reviews exc ${e.localizedMessage}")

            }
            .collect {

            }
    }
     */
}