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


class SharedViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var job: Job = Job()

    var loading = MutableLiveData<Boolean>(false)
    var popularMovies = MutableLiveData<MutableList<MovieResult>>()
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)
    var favIdDbChanged = MutableLiveData<String?>(null)
    var favIdAdded: Boolean = false
    var searchedText = MutableLiveData<String>()
    var currentDetailObj = MutableLiveData<Detail_Movie>()
    var currentSimilarObj = MutableLiveData<Movies>()
    var currentReviewsObj = MutableLiveData<Reviews>()
    var favorites: LiveData<List<MovieFav>>

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
        val search = query.trim()
        factory.updateQuery(search)
    }

    fun reserfactory() {
        factory.updateQuery("")
    }


    fun getMovieDetails(id: String) {
        //  dialog.displayLoadingDialog()
        viewModelScope.launch {
            getDetailsFlow(id)
                .catch { e ->
                    handleExceptions(e)

                }.flowOn(Dispatchers.IO)
                .onCompletion {

                    getReviews(id)
                    getSimilar(id)

                }.collect {
                    currentDetailObj.postValue(it)

                }

        }

    }

    private fun handleExceptions(e: Throwable) {
        if (e is NoInternetException)
            noInternetException.postValue(true)
        else
            error.postValue(true)
    }

    private fun getSimilar(id: String) {
        viewModelScope.launch(Dispatchers.IO + Job()) {

            getSimilarFlow(id).catch { e ->
                println("call failed(similar) ${e.localizedMessage}")
            }.collect { similar ->
                currentSimilarObj.postValue(similar)
            }
        }
    }

    private fun getReviews(id: String) {
        viewModelScope.launch(Dispatchers.IO + Job()) {

            getReviewsFlow(id).catch { e ->
                println("call failed(reviews) ${e.localizedMessage}")
            }.collect { reviews ->
                currentReviewsObj.postValue(reviews)
            }
        }
    }

    private suspend fun getOptionalInfo(id: String) {
        getReviewsFlow(id).zip(getSimilarFlow(id)) { reviews, similarMovies ->
            println("reviews result ${reviews.results}")
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

    fun getDetailsFlow(id: String): Flow<Detail_Movie> = flow {
        emit(remoteRepository.getMovieDetails(id).body()!!)
    }

    fun getReviewsFlow(id: String): Flow<Reviews> = flow {
        emit(remoteRepository.getMovieReviews(id).body()!!)
    }

    fun getSimilarFlow(id: String): Flow<Movies> = flow {
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


}