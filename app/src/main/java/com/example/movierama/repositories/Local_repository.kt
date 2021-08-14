package com.example.movierama


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.movierama.data.fav_movies.MovieFav


class Local_repository(val movieDao: MovieDao) {


    //val countOfNowPLaying = movieDao.countOfNowPLaying()
    fun getFavorites():  LiveData<List<MovieFav>>  {
        return movieDao.get_favorites()
    }
    suspend fun addFavorite(moviefav: MovieFav): Long {
        return movieDao.insert(moviefav)
    }

    suspend fun deleteFavorite(moviefav: MovieFav): Int {
        return movieDao.delete(moviefav)
    }

//    fun insertFromAPItoDb(tvShow: NowPlaying, viewModelScope: CoroutineScope) {
//        viewModelScope.launch(Dispatchers.IO) {
//            tvShowDao.insert(tvShow)
//        }
//    }
//
//
//
//
//    //-----------------------------Most popular methods-----------------------------//
//    suspend fun getMostPopularPerPage(page: Int): NowPlaying {
//        return tvShowDao.getMostPopularPerPage(page)
//    }
//
//    suspend fun deleteAllFromMostPopular(viewModelScope: CoroutineScope) {
//        viewModelScope.launch(Dispatchers.IO) {
//            tvShowDao.deleteAllFromPopular()
//        }
//    }
//    //-----------------------------Top rated methods-----------------------------//
//    suspend fun getTopRated(page: Int): NowPlaying {
//        return tvShowDao.getTopRated(page)
//    }
//
//
//    fun insertTvshowDetailstoDb(tvShow: TvShowDetails, viewModelScope: CoroutineScope): Job {
//        var supervisorJob1 = SupervisorJob()
//        return viewModelScope.launch {
//            runCatching {
//                tvShowDao.insertToTvShowDetails(tvShow)
//            }.onFailure {
//                Timber.e("ssssssss failed $it")
//            }.onSuccess {
//                Timber.e("ssssssss insert success $it")
//            }
//
//        }
//    }
//    suspend fun deleteAllFromTop(viewModelScope: CoroutineScope) {
//        viewModelScope.launch(Dispatchers.IO) {
//            tvShowDao.deleteAllFromTop()
//        }
//    }
//
//
//    //-----------------------------watchlist methods-----------------------------//
//    fun getWatchlist(): LiveData<MutableList<TvShowDetails>> {
//        return tvShowDao.getWatchlistShows()
//    }
//
//    suspend fun getTvShowDetails(id: String): TvShowDetails {
//        return tvShowDao.getTvShowDetails(id)
//    }
//
//     fun getTvShowDetailsAll(): LiveData<MutableList<TvShowDetails>> {
//        return tvShowDao.getTvShowDetailsAll()
//    }
//    suspend fun deleteTvShowFromWatchlist(id: String) {
//        tvShowDao.deleteTvShowFromWatchlist(id)
//    }
//
//
//    suspend fun moveToSeen(id: Int) {
//        try {
//            tvShowDao.moveToSeen(id.toString())
//        } catch (e: Exception) {
//            Log.i("aaaaa", e.message.toString())
//        }
//
//    }
//
//    fun countTvShowsFromWatchlist(): LiveData<Int> {
//        return tvShowDao.countTvShowsFromWatchlist()
//    }
//
//    //------------------favorites methods--------------------------//
//    fun countTvShowsFromFavorites(): LiveData<Int> {
//        return tvShowDao.countTvShowsFromFavorites()
//    }
//
//    fun getFavorites(): LiveData<MutableList<TvShowDetails>> {
//        return tvShowDao.getFavorites()
//    }
//
//    suspend fun deleteTvShowFromFavorites(id: String) {
//        tvShowDao.deleteTvShowFromFavorites(id)
//    }
//
//
//    //-----------Seen methods-------------------//
//    fun getSeen(): LiveData<MutableList<TvShowDetails>> {
//        return tvShowDao.getSeen()
//    }
//
//
//    fun countTvShowsFromSeen(): LiveData<Int> {
//        return tvShowDao.countTvShowsFromSeen()
//    }
//
//    suspend fun moveToFavorites(id: Int) {
//        try {
//            tvShowDao.moveToFavorites(id.toString())
//        } catch (e: Exception) {
//            Log.i("aaaaa", e.message.toString())
//        }
//    }
//
//    suspend fun moveToWatchlist(id: Int) {
//        try {
//            tvShowDao.moveToWatchlist(id.toString())
//        } catch (e: Exception) {
//            Log.i("aaaaa", e.message.toString())
//        }
//
//    }
//
//    suspend fun deleteTvShowFromSeen(id: String) {
//        tvShowDao.deleteTvShowFromSeen(id)
//    }
//
//    //------------------------DEtails----------------------------------//
//
//    suspend fun rowExists(id: String, currentFragment: String, viewModelScope: CoroutineScope): Boolean { return tvShowDao.RowExists(id, currentFragment) }
//
//
//    fun fetchNeeded(context: Context): Boolean {
//
//        val calendar = Calendar.getInstance()
//        val timeInMilli = calendar.timeInMillis
//        val minutes = timeInMilli / (60 * 1000)
//        val lastTime = PreferenceUtils.getLastTime(context).toInt()
//
//        if (minutes - lastTime > 60) {
//            PreferenceUtils.setLastTime(minutes.toString(), context)
//            println("SSSSSSS  $minutes  $lastTime --- ${minutes-lastTime}")
//            return true
//        }
//        return false
//    }
//
//    suspend fun underNotification(id: String, b: Boolean,release_date:String) {
//        try {
//             tvShowDao.underNotification(id ,b)
//        } catch (e: Exception) {
//           Timber.e(e)
//        }
//    }
//
//    suspend fun update_exact_time_of_notification(id: String, date: String)  {
//        try {
//            tvShowDao.update_exact_time_of_notification(id ,date)
//        } catch (e: Exception) {
//            Timber.e(e)
//        }
//    }


}
