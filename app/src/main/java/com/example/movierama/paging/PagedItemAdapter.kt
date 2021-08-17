package com.example.movierama.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movierama.BR
import com.example.movierama.R
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.databinding.ItemLayoutBinding
import com.example.movierama.ui.popular.ItemHandler


class PagedItemAdapter(var context: Context, var itemHandler: ItemHandler?, var query: String = "", var viewModel:ViewModel) : PagedListAdapter<MovieResult, PagedItemAdapter.ItemViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList?.size ?: 0
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemLayoutBinding.setVariable(BR.movie, getItem(position))
        holder.itemLayoutBinding.setVariable(BR.viewModel,viewModel)
        holder.itemLayoutBinding.setVariable(BR.currentSearchName,query)

        // This is important, because it forces the data binding to execute immediately,
        // which allows the RecyclerView to make the correct view size measurements
        holder.itemLayoutBinding.executePendingBindings()

        holder.itemLayoutBinding.image.setOnClickListener {
            itemHandler?.onClick(getItem(position)?.id.toString())
        }
        holder.itemLayoutBinding.favorite.setOnClickListener {
            itemHandler?.onLikeClicked(getItem(position)?.id.toString())
        }
    }

    class ItemViewHolder(var itemLayoutBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root)

    @JvmName("set_query")
    fun setQuery(query: String){
        this.query=query
    }

}