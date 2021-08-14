package com.example.movierama.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.ShowsDataSource
import com.example.movierama.viewmodels.SharedViewModel
import com.example.tvshows.tvshows.ui.adapters.paging.PagedItemAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(),ItemHandler {

    enum class SearchResult {
        NO_RESULT,
        HAS_RESULT,
        EMPTY_SEARCH_TEXT
    }
    var itemHandler: ItemHandler ?=null
    lateinit var viewModel:SharedViewModel
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        viewModel=(activity as MainActivity).viewModel

        val manager = LinearLayoutManager(this.context)
        recyclerview_search.layoutManager = manager
        recyclerview_search.setHasFixedSize(true)
        itemHandler=this

        val adapter = PagedItemAdapter(requireContext(),itemHandler,viewModel = viewModel)
        recyclerview_search.adapter = adapter
        viewModel.createFactory()

        ShowsDataSource.firstResults = {
            if(it == 0 && searcheditText.text!!.isNotEmpty()){
                putPlaceholders(SearchResult.NO_RESULT, adapter, "")
            }
        }
       // var list=PagedList<>()
       // adapter.submitList(paged)
        viewModel.itemPagedList?.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        searcheditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s.toString())) {
                    adapter.setQuery(s.toString())
                    viewModel.searchTvShows(s.toString().trim())
                    adapter.currentList?.dataSource?.invalidate()
                    putPlaceholders(SearchResult.HAS_RESULT, adapter, s.toString())
                } else {
                    adapter.currentList?.dataSource?.invalidate()
                    putPlaceholders(SearchResult.EMPTY_SEARCH_TEXT, adapter, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


        })
    }

    private fun putPlaceholders(res: SearchResult, adapter: PagedItemAdapter, searchQuery: String) {

        when (res) {
            SearchResult.EMPTY_SEARCH_TEXT -> {
                viewModel.reserfactory()
                Handler(Looper.getMainLooper()).postDelayed({
                    search?.visibility = View.VISIBLE
                    no_res.visibility = View.GONE
                }, 200)

            }

            SearchResult.HAS_RESULT -> {
                search?.visibility = View.GONE
                no_res?.visibility = View.GONE
            }

            else -> {
                lifecycleScope.launch(Dispatchers.Main){
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (adapter.itemCount == 0) {
                            search.visibility = View.GONE
                            no_res.visibility = View.VISIBLE
                        }
                    }, 200)
                }

            }
        }
    }

    override fun onClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onLikeClicked(id: String) {
        TODO("Not yet implemented")
    }




}