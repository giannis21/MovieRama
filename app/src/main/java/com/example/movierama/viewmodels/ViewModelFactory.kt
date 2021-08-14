
package com.example.movierama
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movierama.viewmodels.SharedViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(val remoteRepository: RemoteRepository ,var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
             modelClass.isAssignableFrom(SharedViewModel::class.java) -> SharedViewModel(remoteRepository,context) as T

             else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}

