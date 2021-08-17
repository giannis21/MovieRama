package com.example.movierama.di

import android.content.Context
import com.example.movierama.MainActivity
import com.example.movierama.ui.PopularFragment
import com.example.movierama.ui.details.DetailsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class,ViewModelModule::class,DatabaseModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(popularFragment: PopularFragment)
    fun inject(detailsFragment: DetailsFragment)

}