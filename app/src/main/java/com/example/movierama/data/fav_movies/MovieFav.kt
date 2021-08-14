package com.example.movierama.data.fav_movies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_table")
data class MovieFav (
    @PrimaryKey
    val id:String
)