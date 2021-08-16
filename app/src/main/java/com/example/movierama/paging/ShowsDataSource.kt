package com.example.movierama

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.example.movierama.MainActivity.Companion.internetExceptionListener
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.utils.NoInternetException
import kotlinx.coroutines.*

class ShowsDataSource(
    var remoteRepository: RemoteRepository,
    private var scope: CoroutineScope,
    var query: String,
    var context: Context,
    var isSearch: Boolean
) :
    PageKeyedDataSource<Int, MovieResult>() {

    val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        internetExceptionListener?.invoke()
    }
    var FirstPage = 1
    private var supervisorJob = SupervisorJob()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MovieResult>
    ) {

        try {
            scope.launch(supervisorJob + exceptionHandler) {


                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(FirstPage)
                else
                    remoteRepository.searchTvShows(FirstPage, query)

                response.body()!!.results.let {
                    callback.onResult(it, null, (FirstPage + 1))

                }


            }

        } catch (exception: Exception) {

            if (exception is NoInternetException) {
                internetExceptionListener?.invoke()
            }
        }

    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {
        try {
            scope.launch(supervisorJob + exceptionHandler) {

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
        } catch (exception: Exception) {
            if (exception is NoInternetException) {
                internetExceptionListener?.invoke()
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {
        try {
            scope.launch(supervisorJob + exceptionHandler) {
                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(params.key)
                else
                    remoteRepository.searchTvShows(params.key, query)

                val key = params.key.toInt() + 1

                response.body()!!.results.let {
                    callback.onResult(it, key)
                }
            }
        } catch (exception: Exception) {
            if (exception is NoInternetException) {
                internetExceptionListener?.invoke()
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }
}