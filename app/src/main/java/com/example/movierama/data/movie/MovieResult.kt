package com.example.movierama.data.movie

data class MovieResult(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

/*

open class Result(
    open var id: Int,
    open var original_title: String,
    open var poster_path: String,
    open var favorite_status:Int = 0
)

data class MovieRows(
    override var id: Int,
    override var original_title: String,
    override var poster_path: String,
    override var favorite_status:Int = 0,
    val backdrop_path: String,
    val release_date: String,
    val vote_average: Double,
): Result(id,original_title,poster_path,favorite_status)
 */