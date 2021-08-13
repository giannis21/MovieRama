package com.example.tvshows.tvshows.ui.adapters.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movierama.BR
import com.example.movierama.R
import com.example.movierama.data.movie.MovieResult
import com.example.movierama.databinding.ItemLayoutBinding
import com.example.movierama.ui.ItemHandler

import java.util.*


class PagedItemAdapter(var context: Context, var itemHandler: ItemHandler?, var query: String = "") : PagedListAdapter<MovieResult, PagedItemAdapter.ItemViewHolder>(DiffUtilCallBack) {

    var listener_itemCount: ((Int) -> (Unit))?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemLayoutBinding.setVariable(BR.movie, getItem(position))
        // This is important, because it forces the data binding to execute immediately,
        // which allows the RecyclerView to make the correct view size measurements
        holder.itemLayoutBinding.executePendingBindings()
        holder.itemLayoutBinding.image.setOnClickListener {
            itemHandler?.onClick(getItem(position)?.id.toString())
        }
    }

    override fun getItemCount(): Int {
        this.listener_itemCount?.invoke(super.getItemCount())
        return super.getItemCount()
    }

    @JvmName("setQuery1")
    fun setQuery(query11: String){
        this.query=query11
    }

    class ItemViewHolder(var itemLayoutBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root)

    companion object {
        val DiffUtilCallBack = object : DiffUtil.ItemCallback<MovieResult>() {
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
    }

//    override fun getFilter(): Filter {
//        return SearchFilter
//    }
//
//    private val SearchFilter = object : Filter() {
//        override fun performFiltering(constraint: CharSequence): Filter.FilterResults? {
//            var filterList = mutableListOf<MovieResult>()
//            if (constraint.isNullOrEmpty()) {
//                filterList= currentList?.toMutableList()!!
//            } else {
//                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
//                currentList?.forEach { item ->
//                    if (item.original_title.toLowerCase(Locale.ROOT).contains(filterPattern))
//                        filterList?.add(item)
//                }
//
//
//            }
//            val results = FilterResults()
//            results.values = filterList
//            return results
//        }
//
//        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
//            submitList(results.values as PagedList<MovieResult?>)
//        }
//    }


}