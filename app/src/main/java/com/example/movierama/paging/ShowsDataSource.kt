package com.example.movierama

import android.content.Context
import android.widget.Toast
import androidx.paging.PageKeyedDataSource
import com.example.movierama.RemoteRepository
import com.example.movierama.data.movie.Movie
import com.example.movierama.data.movie.MovieResult
import kotlinx.coroutines.*
import retrofit2.Response

class ShowsDataSource(var remoteRepository: RemoteRepository, private var scope: CoroutineScope, var query: String, var context: Context, var isSearch: Boolean) :
        PageKeyedDataSource<Int, MovieResult>() {

    companion object {
        var firstResults: ((Int) -> Unit)? = null
    }

    var FirstPage = 1
    private var supervisorJob = SupervisorJob()


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MovieResult>) {


                scope.launch(supervisorJob) {
                    try {

                        val response = if (query.isEmpty())
                            remoteRepository.getMostPopular(FirstPage)
                        else
                            remoteRepository.searchTvShows(FirstPage, query)

                        response.body()!!.results.let {
                            firstResults?.invoke(it.size)
                            callback.onResult(it, null, (FirstPage + 1))
                        }


                    } catch (exception: Exception) {}

                }


        }





    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {

        scope.launch(supervisorJob) {
            try {
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
            } catch (exception: Exception) {
            }

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {

        scope.launch(supervisorJob) {
            try {

                val response = if (query.isEmpty())
                    remoteRepository.getMostPopular(params.key)
                else
                    remoteRepository.searchTvShows(params.key, query)

                val key = params.key.toInt() + 1

                response.body()!!.results.let {
                    callback.onResult(it, key)
                }
            } catch (exception: Exception) {
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }
}