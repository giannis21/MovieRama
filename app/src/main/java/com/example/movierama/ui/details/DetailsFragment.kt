package com.example.movierama.ui.details

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movierama.MainActivity
import com.example.movierama.MyApplication
import com.example.movierama.R
import com.example.movierama.databinding.FragmentDetailsBinding
import com.example.movierama.viewmodels.SharedViewModel
import javax.inject.Inject

class DetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel:SharedViewModel

    lateinit var binding: FragmentDetailsBinding
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //viewModel=(activity as MainActivity).viewModel
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.viewModel=viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMovieDetails(args.id)
        setUpViews()
        observeViewmodel()

    }

    private fun setUpViews() {

        //i set static height in nestedScrollview dynamically
        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700)
        binding.NestedScrollView.layoutParams = layoutParams

        binding.reviewsTitle.setOnClickListener {
            if(binding.NestedScrollView.isGone){
                binding.NestedScrollView.visibility=View.VISIBLE

                binding.reviewsTitle.apply {   //changing text and arrow direction when user clicks the textview
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_bottom_details, 0)
                    text= getString(R.string.hide_reviews)
                }

                binding.ScrollView.post(Runnable { binding.ScrollView.fullScroll(ScrollView.FOCUS_DOWN) })  //when the view is opened i scroll to the bottom

            }else{
                binding.NestedScrollView.visibility=View.GONE

                binding.reviewsTitle.apply {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_details, 0)
                    text= getString(R.string.show_reviews)
                }

            }
        }

        binding.favorite.setOnClickListener {
            viewModel.addFavorite(args.id)
        }
    }

    private fun observeViewmodel() {

        viewModel.currentDetailObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.movieDetails = it
            }
        })

        viewModel.isFavoriteDetails.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        })

        viewModel.favorites.observe(viewLifecycleOwner, Observer { //when the favorites are updated i want to notify the adapter but not the first time
                                                                  //that's why i check if favIsAdded is not null

                viewModel.favIdDbChanged.value?.let {

                    if (viewModel.favIdAdded)
                        (activity as MainActivity).showBanner("the movie added to the favorites!", true)
                    else
                        (activity as MainActivity).showBanner("the movie removed from the favorites!", true)

                    viewModel.favIdDbChanged.postValue(null)
                }
            })

        //once the api calls finish i am observing the object and bind it to the layout
        viewModel.currentReviewsObj.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.reviews = it
            }
        })

        //if error is true i change the visibilty of the layout which forces the user to go back
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.customDialogLayout.apply {
                    visibility=View.VISIBLE
                }

                binding.firstContainer.visibility=View.INVISIBLE
                binding.customDialogLayout.findViewById<Button>(R.id.dialog_okayBtn).setOnClickListener {
                    findNavController().navigateUp()
                }
            }
            viewModel.error.postValue(false)
        })

        viewModel.currentSimilarObj.observe(viewLifecycleOwner, Observer {
            it?.let {

                val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerviewSimilar.apply {
                    layoutManager = manager
                    setHasFixedSize(true)
                    val similarMoviesAdapter = SimilarMoviesAdapter(context, it.results)
                    adapter = similarMoviesAdapter
                }
                binding.similarGroup.visibility = View.VISIBLE
            }
        })
    }

    fun goBack(){
        findNavController().navigateUp()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MyApplication).appComponent.inject(this)
    }
}