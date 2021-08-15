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
import com.example.movierama.databinding.FragmentDetailsBinding
import com.example.movierama.databinding.PopularFragmentBinding
import com.example.movierama.viewmodels.SharedViewModel
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter

class PopularFragment : Fragment(), ItemHandler {


    var itemHandler: ItemHandler? = null
    private lateinit var viewModel: SharedViewModel
    lateinit var manager: LinearLayoutManager
    lateinit var adapter: PagedItemAdapter
    lateinit var binding: PopularFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopularFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        manager = LinearLayoutManager(this.context)
        binding.recyclerviewPopular.layoutManager = manager
        binding.recyclerviewPopular.setHasFixedSize(true)
        itemHandler = this


        adapter = PagedItemAdapter(requireContext(), itemHandler, viewModel = viewModel)
        binding.recyclerviewPopular.adapter = adapter

        MainActivity.internetExceptionListener ={
            binding.noInternetMessage.visibility=View.VISIBLE
        }

        observeViewmodel()
        binding.swipeRefreshLayout.setOnRefreshListener {

            viewModel.itemPagedList?.value?.dataSource?.invalidate()
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    binding.swipeRefreshLayout.isRefreshing = false
                } catch (e: Exception) {
                    println(e)
                }
            }, 1000)
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