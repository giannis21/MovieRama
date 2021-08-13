package com.example.movierama.data.movieDetail

import com.example.movierama.data.movie.Movie
import com.example.movierama.data.reviews.Reviews

data class Detail_Movie(

    val belongs_to_collection: BelongsToCollection,
    val genres: List<Genre>,
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: Double,
    var similarMovies: Movie?=null,
    var Reviews: Reviews?=null

)