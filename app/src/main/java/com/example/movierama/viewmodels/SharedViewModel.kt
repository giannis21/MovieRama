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
import com.example.movierama.utils.SingleLiveEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor(var remoteRepository: RemoteRepository, var localRepository: LocalRepository, var context: Context) : ViewModel() {

    var job: Job = SupervisorJob()

    //the reason i use a custom class extending MutableLiveData is because after posting a value it posts null to value so i don't do that on my own in every single value
    var error = SingleLiveEvent<Boolean?>()
    var currentDetailObj = SingleLiveEvent<Detail_Movie?>()
    var currentSimilarObj = SingleLiveEvent<Movies?>()
    var currentReviewsObj = SingleLiveEvent<Reviews?>()


    var apiCallState = MutableLiveData<ApiCallState>()
    var favIdDbChanged = MutableLiveData<String>()
    var isFavoriteDetails = MutableLiveData<Boolean?>(null)
    var showLoader = MutableLiveData<Boolean?>(null)

    var favIdAdded: Boolean = false

    var favorites: LiveData<List<MovieFav>>


    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        println(e.message)
    }

    var itemPagedList: LiveData<PagedList<MovieResult>>? = null

    private val config = PagedList.Config.Builder()
        .setPageSize(20)
        .setEnablePlaceholders(false)
        .build()

    var factory: DataSourceFactory

    init {
        factory = DataSourceFactory(remoteRepository,context=context, apiCallState = apiCallState)
        itemPagedList = LivePagedListBuilder<Int, MovieResult>(factory, config).build()
        favorites = localRepository.getFavorites()
    }

    fun searchTvShows(query: String) {
        factory.updateQuery(query.trim())
    }


    fun getMovieDetails(id: String) {
        showLoader.postValue(true)   //i show a generic loader until the main api call finish

        viewModelScope.launch {
            getDetailsFlow(id)
                .catch {

                    error.postValue(true)    //i observe this value and display a layout which forces user to go back
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

        //i launch a coroutine with job which is SupervisorJob,so if an api call fails the other wont stop

        viewModelScope.launch {
            launch(job) {
                getSimilarFlow(id).catch { e ->
                    println(e.localizedMessage)
                }.collect { similar ->
                    if (similar.isSuccessful)
                        currentSimilarObj.postValue(similar.body())  //i set the result to the object in order to bind it in the layout from the fragment(observing)
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




    private fun getDetailsFlow(id: String): Flow<Response<Detail_Movie>> = flow {
        emit(remoteRepository.getMovieDetails(id))
    }

    private fun getReviewsFlow(id: String): Flow<Response<Reviews>> = flow {
        emit(remoteRepository.getMovieReviews(id))
    }

    private fun getSimilarFlow(id: String): Flow<Response<Movies>> = flow {
        emit(remoteRepository.getSimilarMovies(id, 1))
    }

    fun addFavorite(id: String) {
        var inserted = false
        viewModelScope.launch(exceptionHandler + Dispatchers.Default) {

            runCatching {

                favorites.value?.firstOrNull { it.id == id }?.let {
                    localRepository.deleteFavorite(moviefav = MovieFav(id = id))
                    isFavoriteDetails.postValue(true)
                } ?: kotlin.run {
                    inserted = true
                    localRepository.addFavorite(moviefav = MovieFav(id = id))
                    isFavoriteDetails.postValue(false)
                }

            }.onSuccess {
                favIdAdded = inserted  //i use this boolean in order to decide what message to show with banner layout(if it is added or removed)
                favIdDbChanged.postValue(id)
            }.onFailure {
              //  favIdDbChanged.postValue(null)
            }
        }
    }

}