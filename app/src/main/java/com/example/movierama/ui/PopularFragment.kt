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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.viewmodels.SharedViewModel
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter
import kotlinx.android.synthetic.main.popular_fragment.*

class PopularFragment : Fragment(),ItemHandler {

    companion object {
        fun newInstance() = PopularFragment()
        var itemHandler: ItemHandler?=null
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
        manager = LinearLayoutManager(this.context)
        recyclerview_popular.layoutManager = manager
        recyclerview_popular.setHasFixedSize(true)
        itemHandler=this
        adapter = PagedItemAdapter(requireContext(),itemHandler)
        recyclerview_popular.adapter = adapter

       // (activity as MainActivity).viewModel.getPopularMovies()
        (activity as MainActivity).viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {

                adapter.submitList(it)
            }

        })
    }

    override fun onClick(id:String) {
        findNavController().navigate(PopularFragmentDirections.actionPopularFragmentToDetailsFragment(id))
    }

    override fun onLikeClicked(id:String){
        TODO("Not yet implemented")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

}