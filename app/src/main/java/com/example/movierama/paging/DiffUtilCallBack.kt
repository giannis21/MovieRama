package com.example.movierama.paging

import androidx.recyclerview.widget.DiffUtil
import com.example.movierama.data.movie.MovieResult


object DiffCallback : DiffUtil.ItemCallback<MovieResult>() {

    override fun areItemsTheSame(
        oldItem: MovieResult,
        newItem: MovieResult
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MovieResult,
        newItem: MovieResult
    ): Boolean {
        return oldItem == newItem
    }
}