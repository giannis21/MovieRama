package com.example.movierama.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.R
import com.example.movierama.databinding.FragmentDetailsBinding
import com.example.movierama.viewmodels.SharedViewModel

class DetailsFragment : Fragment() {

    lateinit var viewModel:SharedViewModel
    lateinit var binding: FragmentDetailsBinding
    val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(activity as MainActivity).viewModel

        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.viewModel=viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMovieDetails(args.id)
        observeViewmodel()

        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700)
        binding.NestedScrollView.layoutParams = layoutParams

        binding.reviewsTitle.setOnClickListener {
            if(binding.NestedScrollView.isGone){
                binding.NestedScrollView.visibility=View.VISIBLE

                binding.reviewsTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_bottom_details, 0)
                binding.ScrollView.post(Runnable { binding.ScrollView.fullScroll(ScrollView.FOCUS_DOWN) })
                binding.reviewsTitle.text= getString(R.string.hide_reviews)
            }else{
                binding.NestedScrollView.visibility=View.GONE

                binding.reviewsTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_details, 0)
                binding.reviewsTitle.text= getString(R.string.show_reviews)
            }
        }

        binding.favorite.setOnClickListener {
            viewModel.addFavorite(args.id)
        }



    }



    fun observeViewmodel() {

        viewModel.currentDetailObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.movieDetails = it
            }
        })

        viewModel.isFavoriteDetails.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }else{
                binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        })

        viewModel.favorites.observe(viewLifecycleOwner, Observer { //when the favorites are updated i want to notify the adapter but not the first time
            //that's why i check if favIsAdded is not null

            viewModel.favIdDbChanged.value?.let { _ ->

                    viewModel.favIdDbChanged.value = null
                    if (viewModel.favIdAdded)
                        (activity as MainActivity).showBanner("the movie has been added to the favorites!", true)
                    else
                        (activity as MainActivity).showBanner("the movie has been removed from the favorites!", true)

            }
        })

        viewModel.currentReviewsObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.reviews = it
            }
        })

        viewModel.currentSimilarObj.observe(viewLifecycleOwner, Observer {
            it?.let {

                val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerviewSimilar.layoutManager = manager
                binding.recyclerviewSimilar.setHasFixedSize(true)

                val adapter = SimilarMoviesAdapter(this.requireContext(), it.results)
                binding.recyclerviewSimilar.adapter = adapter

            }
        })
    }
    fun goBack(){
        findNavController().navigateUp()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.favIdDbChanged.value=null
    }

}