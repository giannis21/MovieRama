package com.example.movierama.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.viewmodels.SharedViewModel
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter
import kotlinx.android.synthetic.main.popular_fragment.*

class PopularFragment : Fragment(), ItemHandler {

    companion object {
        fun newInstance() = PopularFragment()
        var itemHandler: ItemHandler? = null
        var searchListener: ((String) -> (Unit))? = null
    }

    private lateinit var viewModel: SharedViewModel
    lateinit var manager: LinearLayoutManager
    lateinit var adapter: PagedItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        manager = LinearLayoutManager(this.context)
        recyclerview_popular.layoutManager = manager
        recyclerview_popular.setHasFixedSize(true)
        itemHandler = this


        adapter = PagedItemAdapter(requireContext(), itemHandler, viewModel = viewModel)
        recyclerview_popular.adapter = adapter

        MainActivity.internetExceptionListener ={
            no_internet_message.visibility=View.VISIBLE
        }

        observeViewmodel()
        swipeRefreshLayout.setOnRefreshListener {
            // do not change chips
            // remove chips and build new


            viewModel.itemPagedList?.value?.dataSource?.invalidate()
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    swipeRefreshLayout.isRefreshing = false
                } catch (e: Exception) {
                    println(e)
                }
            }, 1500)
        }
    }

    private fun observeViewmodel() {

        viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                //if(it.isNotEmpty())
               // if(no_internet_message.visibility==View.VISIBLE)
                  //  no_internet_message.visibility=View.GONE
            }
        })

        viewModel.searchedText.observe(viewLifecycleOwner, Observer {
            viewModel.searchTvShows(it.trim())
            adapter.setQuery(it)
            adapter.currentList?.dataSource?.invalidate()

        })

        viewModel.favorites.observe(viewLifecycleOwner, Observer { //when the favorites are updated i want to notify the adapter but not the first time
                //that's why i check if favIsAdded is not null

                viewModel.favIdDbChanged.value.let { movieId ->

                    adapter.currentList?.firstOrNull { it.id.toString() == movieId }?.let {

                        viewModel.favIdDbChanged.value = null
                        adapter.notifyItemChanged(adapter.currentList?.indexOf(it) ?: 0)


                        if (viewModel.favIdAdded)
                            (activity as MainActivity).showBanner("the movie has been added to the favorites!", true)
                        else
                            (activity as MainActivity).showBanner("the movie has been removed from the favorites!", true)

                    }

                }
            })
    }

    override fun onClick(id: String) {
        findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToDetailsFragment(id))
    }

    override fun onLikeClicked(id: String) {
        viewModel.addFavorite(id)
    }
}