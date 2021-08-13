package com.example.movierama.data.reviews

data class Reviews(
    val id: Int,
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)