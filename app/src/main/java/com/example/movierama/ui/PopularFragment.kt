package com.example.movierama.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.databinding.PopularFragmentBinding
import com.example.movierama.network.netMethods.isInternetAvailable
import com.example.movierama.paging.PagedItemAdapter
import com.example.movierama.utils.ApiCallState
import com.example.movierama.viewmodels.SharedViewModel

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
        itemHandler = this

        viewModel = (activity as MainActivity).viewModel


        //setting up the recyclerview with the adapter
        manager = LinearLayoutManager(this.context)
        binding.recyclerviewPopular.layoutManager = manager
        binding.recyclerviewPopular.setHasFixedSize(true)
        adapter = PagedItemAdapter(requireContext(), itemHandler, viewModel = viewModel)
        binding.recyclerviewPopular.adapter = adapter

        observeViewmodel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            try {
                viewModel.itemPagedList?.value?.dataSource?.invalidate()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.swipeRefreshLayout.isRefreshing = false
                }, 1000)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun updateSearch(query: String) {
        viewModel.searchTvShows(query.trim())
        adapter.setQuery(query)
        adapter.currentList?.dataSource?.invalidate()
    }

    private fun observeViewmodel() {

        viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //i use Sealed class in order to decide if i show the noInternet layout Message.
        // apiCallState variable is MutableLiveData which is passed into DataSource, where api calls is done
        viewModel.apiCallState.observe(viewLifecycleOwner, Observer {
            when(it){
                is ApiCallState.NoInternetErrorMessage -> {
                    binding.noInternetMessage.visibility = View.VISIBLE
                    binding.noInternetMessage.findViewById<TextView>(R.id.noconnection).text= it.data
                }
                is ApiCallState.Success -> {
                    binding.noInternetMessage.visibility = View.GONE
                }
                else -> {}
            }

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
        if (isInternetAvailable(requireContext()))
            findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToDetailsFragment(id))
        else
            (activity as MainActivity).showBanner("No internet connection!")
    }

    override fun onLikeClicked(id: String) {
        viewModel.addFavorite(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.favIdDbChanged.value = null
    }

}