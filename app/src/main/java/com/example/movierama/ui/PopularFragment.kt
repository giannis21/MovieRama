package com.example.movierama.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.data.fav_movies.MovieFav
import com.example.movierama.viewmodels.SharedViewModel
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter
import kotlinx.android.synthetic.main.popular_fragment.*

class PopularFragment : Fragment(),ItemHandler {

    companion object {
        fun newInstance() = PopularFragment()
        var itemHandler: ItemHandler?=null
        var searchListener: ((String) -> (Unit))?=null
    }

    private lateinit var viewModel: SharedViewModel
    lateinit var manager:LinearLayoutManager
    lateinit var adapter: PagedItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=(activity as MainActivity).viewModel

        manager = LinearLayoutManager(this.context)
        recyclerview_popular.layoutManager = manager
        recyclerview_popular.setHasFixedSize(true)
        itemHandler=this


        adapter = PagedItemAdapter(requireContext(),itemHandler,viewModel = viewModel)
        recyclerview_popular.adapter = adapter

        observeViewmodel()


    }

    private fun observeViewmodel() {

        viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.searchedText.observe(viewLifecycleOwner, Observer {
            viewModel.searchTvShows(it.trim())
            adapter.setQuery(it)
            adapter.currentList?.dataSource?.invalidate()

        })

        viewModel.favorites.observe(viewLifecycleOwner, Observer { //when the favorites are updated i want to notify the adapter but not the first time
                                                                   //that's why i check if favIsAdded is not null

            viewModel.favIdAdded.value.let { movieId ->

                adapter.currentList?.firstOrNull{it.id.toString() == movieId}?.let {

                    viewModel.favIdAdded.value = null
                    adapter.notifyItemChanged(adapter.currentList?.indexOf(it) ?: 0)

                }

            }
        })
    }

    override fun onClick(id:String) {
        findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToDetailsFragment(id))
    }

    override fun onLikeClicked(id:String){
        viewModel.addFavorite(id)
    }

}