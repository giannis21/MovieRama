package com.example.movierama

import androidx.paging.PageKeyedDataSource
import com.example.movierama.RemoteRepository
import com.example.movierama.data.movie.MovieResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class ShowsDataSource(var remoteRepository: RemoteRepository, private var scope: CoroutineScope, var query: String) :
    PageKeyedDataSource<Int, MovieResult>() {

    companion object{
        var firstResults: ((Int) -> Unit)? =null
    }
    var FirstPage = 1
    private var supervisorJob = SupervisorJob()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MovieResult>) {
        if(query.isEmpty())
        {
            scope.launch(supervisorJob) {
                try {
                    val response = remoteRepository.getMostPopular(FirstPage)
                    response.body()!!.results.let {
                        firstResults?.invoke(it.size)
                        callback.onResult(it, null, (FirstPage + 1))
                    }
                } catch (exception: Exception) {}
            }
        }else{
            firstResults?.invoke(0)
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {
        scope.launch(supervisorJob)  {
            try {
                val key = if(params.key.toInt() > 1 ){ params.key.toInt() -1 }else null
                val response = remoteRepository.getMostPopular(key!!.toInt())
                response.body()!!.results.let {
                   callback.onResult(it,key)
                }
            } catch (exception: Exception) {}

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieResult>) {
        scope.launch(supervisorJob)  {
            try {
                val response = remoteRepository.getMostPopular(params.key)
                val key =  params.key.toInt() + 1
                response.body()!!.results.let {
                    callback.onResult(it,key)
                }
            } catch (exception: Exception) {}
        }
    }
    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()
    }
}