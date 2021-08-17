package com.example.movierama.ui.popular

import android.content.Context
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
import com.example.movierama.MyApplication
import com.example.movierama.R
import com.example.movierama.databinding.PopularFragmentBinding
import com.example.movierama.network.NetworkMethods.isInternetAvailable
import com.example.movierama.paging.PagedItemAdapter
import com.example.movierama.utils.ApiCallState
import com.example.movierama.viewmodels.SharedViewModel
import javax.inject.Inject

class PopularFragment : Fragment(), ItemHandler {


    var itemHandler: ItemHandler? = null

    @Inject
    lateinit var viewModel:SharedViewModel

    lateinit var manager: LinearLayoutManager
    lateinit var pagedAdapter: PagedItemAdapter
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

        setUpViews()
        observeViewmodel()
    }

    private fun setUpViews() {
        viewModel = (activity as MainActivity).viewModel

        //setting up the recyclerview with the adapter
        manager = LinearLayoutManager(this.context)
        binding.recyclerviewPopular.apply {
            layoutManager = manager
            setHasFixedSize(true)
            pagedAdapter = PagedItemAdapter(requireContext(), itemHandler, viewModel = viewModel)
            adapter = pagedAdapter
        }

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

    //i call thid function from the activity when the text in the input field changes
    fun updateSearch(query: String) {
        viewModel.searchTvShows(query.trim()) // this line updates the factory's query
        pagedAdapter.setQuery(query)              //this updates the searched text inside the adapter, so the binding adapter to get the updated value
        pagedAdapter.currentList?.dataSource?.invalidate()  //when invalidating factory oncreate method is called again so it creates Datasource object with the updated query
    }

    private fun observeViewmodel() {

        viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {
                pagedAdapter.submitList(it)
            }
        })

        //i use Sealed class in order to decide if i show the noInternet layout Message.
        // apiCallState variable is MutableLiveData which is passed into DataSource, where api calls is done
        viewModel.apiCallState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiCallState.NoInternetErrorMessage -> {
                    binding.noInternetMessage.apply {
                        visibility = View.VISIBLE
                        findViewById<TextView>(R.id.noconnection).text = it.data
                    }
                }
                is ApiCallState.Success -> {
                    binding.noInternetMessage.visibility = View.GONE
                }
                else -> { }
            }
        })


        viewModel.favorites.observe(viewLifecycleOwner, Observer { //when the favorites are updated i want to notify the adapter but not the first time
                                                                  //that's why i check if favIsAdded is not null

                viewModel.favIdDbChanged.value?.let { movieId ->

                    pagedAdapter.currentList?.firstOrNull { it.id.toString() == movieId }?.let {

                        pagedAdapter.notifyItemChanged(pagedAdapter.currentList?.indexOf(it) ?: 0)  // i must notify the position of the movie that changed favorite status
                                                                                                             // so the binding adapter to be called in order to change image

                        if (viewModel.favIdAdded)
                            (activity as MainActivity).showBanner("the movie added to the favorites!", true)
                        else
                            (activity as MainActivity).showBanner("the movie removed from the favorites!", true)

                        viewModel.favIdDbChanged.value = null
                    }
                }
            })
    }

    //------------- INTERFACE IMPLEMENTATION FUNCTIONS--------------//
    override fun onClick(id: String) {
        if (isInternetAvailable(requireContext()))
            findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToDetailsFragment(id))
        else
            (activity as MainActivity).showBanner("No internet connection!")
    }

    override fun onLikeClicked(id: String) {
        viewModel.addFavorite(id)
    }
    //-------------END INTERFACE IMPLEMENTATION FUNCTIONS--------------//



    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MyApplication).appComponent.inject(this)
        itemHandler = this
    }
}