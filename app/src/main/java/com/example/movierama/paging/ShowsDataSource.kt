package com.example.movierama

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PageKeyedDataSource
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.utils.ApiCallState
import com.example.movierama.utils.NoInternetException
import kotlinx.coroutines.*
import javax.inject.Inject

class ShowsDataSource(
    var remoteRepository: RemoteRepository,
    var scope: CoroutineScope,
    var query: String,
    context: Context,
    var apiCallState:MutableLiveData<ApiCallState>
) :
    PageKeyedDataSource<Int, MovieResult>() {


   //only in initial api call i want to show the internet message layout, that's why i use different exception handler
    private val exceptionHandlerInitial = CoroutineExceptionHandler { _, e ->
       if(e is NoInternetException)
          apiCallState.postValue(ApiCallState.NoInternetErrorMessage(context.getString(R.string.connectivity_problem_npull_down_to_refresh)))
       else
          apiCallState.postValue(ApiCallState.GeneralErrorMessage(context.getString(R.string.an_error_occured)))
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        println(e.message)
    }

    private var FirstPage = 1

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MovieResult>
    ) {

            scope.launch(Dispatchers.IO + exceptionHandlerInitial) {

                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(FirstPage)
                else
                    remoteRepository.searchTvShows(FirstPage, query)

                if(response.isSuccessful){
                    response.body()!!.results.let {
                        apiCallState.postValue(ApiCallState.Success("data fetched succesfully!"))
                        callback.onResult(it, null, (FirstPage + 1))
                    }
                }else{
                    apiCallState.postValue(ApiCallState.GeneralErrorMessage("Oups, an error occured! try again later."))
                }

            }

    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {

            scope.launch(Dispatchers.IO + exceptionHandler) {

                val key = if (params.key.toInt() > 1) {
                    params.key.toInt() - 1
                } else null

                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(key!!.toInt())
                else
                    remoteRepository.searchTvShows(key!!.toInt(), query)

                response.body()!!.results.let {
                    callback.onResult(it, key)
                }
            }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {

            scope.launch(Dispatchers.IO + exceptionHandler) {
                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(params.key)
                else
                    remoteRepository.searchTvShows(params.key, query)

                val key = params.key.toInt() + 1

                response.body()!!.results.let {
                    callback.onResult(it, key)
                }
            }

    }

}