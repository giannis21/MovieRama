package com.example.movierama.data.movieDetail

data class Detail_Movie(

    val belongs_to_collection: BelongsToCollection,
    val genres: List<Genre>,
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: Double

)