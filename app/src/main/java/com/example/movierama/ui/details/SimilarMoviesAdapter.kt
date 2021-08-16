package com.example.movierama.ui.details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movierama.R
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.databinding.SimilarLayoutBinding

class SimilarMoviesAdapter(var context: Context, var list: List<MovieResult>) : RecyclerView.Adapter<SimilarMoviesAdapter.ItemViewHolder>() {


    class ItemViewHolder(var recyclerviewNowPLayingbinding: SimilarLayoutBinding) : RecyclerView.ViewHolder(recyclerviewNowPLayingbinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<SimilarLayoutBinding>(LayoutInflater.from(parent.context), R.layout.similar_layout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.recyclerviewNowPLayingbinding.imageUrl= list[position].poster_path
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

