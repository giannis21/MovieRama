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
import com.example.movierama.utils.ApiCallState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Response


class SharedViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var job: Job = SupervisorJob()

    var loading = MutableLiveData<Boolean>(false)

    var apiCallState = MutableLiveData<ApiCallState>()
    var error = MutableLiveData<Boolean>(false)
    var favIdDbChanged = MutableLiveData<String?>(null)
    var favIdAdded: Boolean = false
    var currentDetailObj = MutableLiveData<Detail_Movie?>(null)
    var currentSimilarObj = MutableLiveData<Movies?>(null)
    var currentReviewsObj = MutableLiveData<Reviews?>(null)
    var favorites: LiveData<List<MovieFav>>
    var isFavoriteDetails = MutableLiveData<Boolean>(false)
    var showLoader = MutableLiveData<Boolean?>(null)

    val exceptionHandler = CoroutineExceptionHandler { _, e ->
        println("Error ${e.message}")
    }

    var itemPagedList: LiveData<PagedList<MovieResult>>? = null

    val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

    var factory: DataSourceFactory

    var local_repository: Local_repository

    init {
        factory = DataSourceFactory(remoteRepository, context = context, apiCallState = apiCallState)
        itemPagedList = LivePagedListBuilder<Int, MovieResult>(factory, config).build()

        val moviesDao = MovieRoomDatabase.getDatabase(context = context).movieDao()
        local_repository = Local_repository(moviesDao)
        favorites = local_repository.getFavorites()
    }

    fun searchTvShows(query: String) {
        factory.updateQuery(query.trim())
    }


    fun getMovieDetails(id: String) {
        showLoader.postValue(true)   //i show a generic loader until the api call finish

        viewModelScope.launch {
            getDetailsFlow(id)
                .catch {

                    error.postValue(true)
                    showLoader.postValue(false)

                }.flowOn(Dispatchers.IO)
                .onCompletion {
                    showLoader.postValue(false)  //when the main details fetched from the api call i hide the dialog using the observer
                }.collect {

                    if (it.isSuccessful) {
                        currentDetailObj.postValue(it.body())
                        getNonCriticalInfo(id)     //i get the non critical information once the main details api call is succesfull, otherwise it's meaningless
                    }else
                        error.postValue(true)

                }

        }

    }


    private suspend fun getNonCriticalInfo(id: String) {
        viewModelScope.launch {
            launch(job) {
                getSimilarFlow(id).catch { e ->
                    println(e.localizedMessage)
                }.collect { similar ->
                    if (similar.isSuccessful)
                        currentSimilarObj.postValue(similar.body())
                }
            }

            launch(job) {
                getReviewsFlow(id).catch { e ->
                    println(e.localizedMessage)
                }.collect { reviews ->
                    if (reviews.isSuccessful)
                        currentReviewsObj.postValue(reviews.body())
                }
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
                    isFavoriteDetails.postValue(true)
                } ?: kotlin.run {
                    inserted = true
                    local_repository.addFavorite(moviefav = MovieFav(id = id))
                    isFavoriteDetails.postValue(false)
                }

            }.onFailure {
                loading.postValue(false)
            }.onSuccess {
                favIdAdded = inserted
                favIdDbChanged.postValue(id)

            }
        }
    }




   /* private fun getSimilar(id: String) {
        viewModelScope.launch(Dispatchers.IO + job) {
            getSimilarFlow(id).catch { e ->
                println("call failed(similar) ${e.localizedMessage}")
            }.collect { similar ->
                if (similar.isSuccessful)
                    currentSimilarObj.postValue(similar.body())
            }
        }
    }

    private fun getReviews(id: String) {
        viewModelScope.launch(Dispatchers.IO + job) {
            getReviewsFlow("123123123").catch { e ->
                println("call failed(reviews) ${e.localizedMessage}")
            }.collect { reviews ->
                if (reviews.isSuccessful)
                    currentReviewsObj.postValue(reviews.body())
            }
        }
    } */

}